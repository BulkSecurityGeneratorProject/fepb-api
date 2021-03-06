package br.org.fepb.api.web.rest;

import br.org.fepb.api.domain.Inscricao;
import br.org.fepb.api.domain.Pagamento;
import br.org.fepb.api.service.InscricaoService;
import br.org.fepb.api.service.PagamentoService;
import br.org.fepb.api.service.dto.InscricaoDTO;
import br.org.fepb.api.service.dto.PreferenceDTO;
import com.mercadopago.MercadoPago;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.Payer;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-gateway")
public class PaymentsGatewayResource {

    private InscricaoService inscricaoService;
    private PagamentoService pagamentoService;

    private Environment env;

    public PaymentsGatewayResource(InscricaoService inscricaoService,
                                   PagamentoService pagamentoService,
                                   Environment env) throws MPException {
        this.inscricaoService = inscricaoService;
        this.pagamentoService = pagamentoService;
        this.env = env;

        MercadoPago.SDK.configure(env.getProperty("mercadopago.client-id"),
                                env.getProperty("mercadopago.client-secret"));

    }

    @PostMapping("/ipn")
    @ResponseStatus(HttpStatus.OK)
    public void recieveIpn(@RequestParam(name = "data.id", required = false) Long dataId,
                           @RequestParam(name = "type", required = false) String type) throws MPException {

        if ((type != null) &&
            (type.equals("payment")) &&
            (dataId != null)) {

            Payment p = Payment.findById(dataId.toString());

            if (p.getStatus() != null) {

                Inscricao i = inscricaoService.getInscricaoEntity(Long.parseLong(p.getExternalReference()));
                Pagamento pag = i.getPagamento();
                if (pag != null) {
                    pag.setStatus(p.getStatus().toString());
                    this.pagamentoService.salvar(pag);
                }
            }
        }
    }

    @PostMapping("/preferencia")
    public PreferenceDTO criarPreferencia(@RequestBody InscricaoDTO ic) throws MPException {

        Inscricao i = this.inscricaoService.getInscricaoEntity(ic.getId());
        if (i == null) {
            return null;
        }

        if (i.getPagamento() != null) {
            if (i.getPagamento().getReferenceId() == null) {
                this.pagamentoService.deletar(i.getPagamento().getId());
            } else {
                Preference p = Preference.findById(i.getPagamento().getReferenceId());
                if (p != null) {
                    return new PreferenceDTO(p);
                }
            }
        }

        Preference p = new Preference();
        p.setExpires(new Boolean(false));
        p.setExternalReference(i.getId().toString());

        Payer payer = new Payer();
        payer.setName(i.getPessoa().getNome());
        payer.setEmail(i.getPessoa().getEmail());

        Item item = new Item();
        item.setQuantity(1);
        item.setTitle("Inscricao AJE 2019");
        item.setUnitPrice(new Float(i.getValor()));

        p.appendItem(item);
        p.setPayer(payer);

        p.save();

        Pagamento pag = new Pagamento();
        pag.setReferenceId(p.getId());
        pag.setUnitPrice(p.getItems().get(0).getUnitPrice());
        pag.setInitPoint(p.getInitPoint());
        pag.setSandboxInitPoint(p.getSandboxInitPoint());
        pag.setInscricao(i);

        pag = this.pagamentoService.salvar(pag);

        return new PreferenceDTO(p);
    }

}
