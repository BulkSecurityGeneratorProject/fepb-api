package br.org.fepb.api.service;

import br.org.fepb.api.domain.Inscricao;
import br.org.fepb.api.domain.Oficina;
import br.org.fepb.api.domain.Pessoa;
import br.org.fepb.api.enumeration.RestricaoAlimentarEnum;
import br.org.fepb.api.enumeration.SexoEnum;
import br.org.fepb.api.enumeration.TipoSanguineoEnum;
import br.org.fepb.api.repository.InscricaoRepository;
import br.org.fepb.api.repository.OficinaRepository;
import br.org.fepb.api.service.dto.InscricaoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InscricaoService {

    private final Logger log = LoggerFactory.getLogger(InscricaoService.class);

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatterUpdate = new SimpleDateFormat("yyyy-MM-dd");

    private InscricaoRepository inscricaoRepository;

    private OficinaRepository oficinaRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository,
                            OficinaRepository oficinaRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.oficinaRepository = oficinaRepository;
    }

    public InscricaoDTO getInscricao(Long id) {
        Optional<Inscricao> i = this.inscricaoRepository.findById(id);
        InscricaoDTO dto = new InscricaoDTO(i.get());
        return dto;
    }

    public List<Inscricao> listarInscricoes() {
        return inscricaoRepository.findAll();
    }

    public Inscricao salvarInscricao(InscricaoDTO i) throws ParseException {

        Pessoa newPessoa = new Pessoa();
        newPessoa.setNome(i.getPessoa().getNome());
        newPessoa.setComoChamar(i.getPessoa().getComoChamar());
        newPessoa.setSexo((SexoEnum.MASCULINO.toString().equals(i.getPessoa().getSexo())) ? SexoEnum.MASCULINO : SexoEnum.FEMININO);

        if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.A_POSITIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.A_POSITIVO);
        } else if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.A_NEGATIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.A_NEGATIVO);
        } else if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.B_POSITIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.B_POSITIVO);
        } else if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.B_NEGATIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.B_NEGATIVO);
        } else if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.AB_POSITIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.AB_POSITIVO);
        } else if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.AB_NEGATIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.AB_NEGATIVO);
        } else if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.O_POSITIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.O_POSITIVO);
        } else if (i.getPessoa().getTipoSanguineo().toString()
            .equals(TipoSanguineoEnum.O_NEGATIVO.toString())) {
            newPessoa.setTipoSanguineo(TipoSanguineoEnum.O_NEGATIVO);
        }

        newPessoa.setEmail(i.getPessoa().getEmail());
        newPessoa.setDataNascimento(formatter.parse(i.getPessoa().getDataNascimento()));

        if (RestricaoAlimentarEnum.COME_CARNE.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
            newPessoa.setRestricaoAlimentar(RestricaoAlimentarEnum.COME_CARNE);
        } else if (RestricaoAlimentarEnum.NAO_COME_CARNE_VERMELHA.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
            newPessoa.setRestricaoAlimentar(RestricaoAlimentarEnum.NAO_COME_CARNE_VERMELHA);
        } else if (RestricaoAlimentarEnum.VEGETARIANO.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
            newPessoa.setRestricaoAlimentar(RestricaoAlimentarEnum.VEGETARIANO);
        } else if (RestricaoAlimentarEnum.VEGANO.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
            newPessoa.setRestricaoAlimentar(RestricaoAlimentarEnum.VEGANO);
        }

        newPessoa.setRestricaoSaude(i.getPessoa().getRestricaoSaude());

        Inscricao newInscricao = new Inscricao();
        newInscricao.setInstituicao(i.getInstituicao());
        newInscricao.setEmailCoordenador(i.getEmailCoordenador());
        newInscricao.setNomeCoordenador(i.getNomeCoordenador());
        newInscricao.setTelefone(i.getTelefone());
        newInscricao.setTrabalhador(new Boolean(i.isTrabalhador()));
        newInscricao.setPago(new Boolean(false));

        newInscricao.setPessoa(newPessoa);

        if (i.getOficina() != null && i.getOficina().getId() != null) {
            Oficina o = oficinaRepository.getOne(i.getOficina().getId());
            newInscricao.setOficina(o);
        }
        return inscricaoRepository.save(newInscricao);

    }

    public Inscricao atualizarInscricao(InscricaoDTO i, Long id) throws ParseException {
        Inscricao iUpdate = inscricaoRepository.getOne(id);

        if (iUpdate != null) {
            iUpdate.getPessoa().setNome(i.getPessoa().getNome());
            iUpdate.getPessoa().setComoChamar(i.getPessoa().getComoChamar());
            iUpdate.getPessoa().setSexo((SexoEnum.MASCULINO.toString().equals(i.getPessoa().getSexo())) ? SexoEnum.MASCULINO : SexoEnum.FEMININO);

            if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.A_POSITIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.A_POSITIVO);
            } else if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.A_NEGATIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.A_NEGATIVO);
            } else if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.B_POSITIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.B_POSITIVO);
            } else if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.B_NEGATIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.B_NEGATIVO);
            } else if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.AB_POSITIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.AB_POSITIVO);
            } else if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.AB_NEGATIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.AB_NEGATIVO);
            } else if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.O_POSITIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.O_POSITIVO);
            } else if (i.getPessoa().getTipoSanguineo().toString()
                .equals(TipoSanguineoEnum.O_NEGATIVO.toString())) {
                iUpdate.getPessoa().setTipoSanguineo(TipoSanguineoEnum.O_NEGATIVO);
            }

            iUpdate.getPessoa().setEmail(i.getPessoa().getEmail());
            iUpdate.getPessoa().setDataNascimento(formatterUpdate.parse(i.getPessoa().getDataNascimento()));

            if (RestricaoAlimentarEnum.COME_CARNE.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
                iUpdate.getPessoa().setRestricaoAlimentar(RestricaoAlimentarEnum.COME_CARNE);
            } else if (RestricaoAlimentarEnum.NAO_COME_CARNE_VERMELHA.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
                iUpdate.getPessoa().setRestricaoAlimentar(RestricaoAlimentarEnum.NAO_COME_CARNE_VERMELHA);
            } else if (RestricaoAlimentarEnum.VEGETARIANO.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
                iUpdate.getPessoa().setRestricaoAlimentar(RestricaoAlimentarEnum.VEGETARIANO);
            } else if (RestricaoAlimentarEnum.VEGANO.toString().equals(i.getPessoa().getRestricaoAlimentar())) {
                iUpdate.getPessoa().setRestricaoAlimentar(RestricaoAlimentarEnum.VEGANO);
            }

            iUpdate.getPessoa().setRestricaoSaude(i.getPessoa().getRestricaoSaude());

            iUpdate.setInstituicao(i.getInstituicao());
            iUpdate.setEmailCoordenador(i.getEmailCoordenador());
            iUpdate.setNomeCoordenador(i.getNomeCoordenador());
            iUpdate.setTelefone(i.getTelefone());
            iUpdate.setTrabalhador(new Boolean(i.isTrabalhador()));
            iUpdate.setPago(new Boolean(i.isPago()));

            if (i.getOficina() != null && i.getOficina().getId() != null) {
                Oficina o = oficinaRepository.getOne(i.getOficina().getId());
                iUpdate.setOficina(o);
            }

            return inscricaoRepository.save(iUpdate);

        } else {
            return null;
        }

    }

}
