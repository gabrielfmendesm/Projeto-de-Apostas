package br.insper.aposta.aposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApostaService {

    @Autowired
    private ApostaRepository apostaRepository;

    public Aposta salvar(Aposta aposta) {
        aposta.setId(UUID.randomUUID().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPartidaDTO> partida = restTemplate.getForEntity(
                "http://3.81.161.81:8080/partida/" + aposta.getIdPartida(),
                RetornarPartidaDTO.class);

        if (partida.getStatusCode().is2xxSuccessful()) {
            apostaRepository.save(aposta);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada.");
        }
        return aposta;
    }

    public Aposta verificarAposta(String idAposta) {
        Optional<Aposta> aposta = apostaRepository.findById(idAposta);
        if (aposta.isPresent()) {
            if ("REALIZADA".equals(aposta.get().getStatus())) {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<RetornarPartidaDTO> partidaResponse = restTemplate.getForEntity(
                        "http://3.81.161.81:8080/partida/" + aposta.get().getIdPartida(),
                        RetornarPartidaDTO.class);

                RetornarPartidaDTO partida = partidaResponse.getBody();

                if (partida != null && "REALIZADA".equals(partida.getStatus())) {
                    if ((aposta.get().getResultado().equals("VITORIA_MANDANTE") && partida.getPlacarMandante() > partida.getPlacarVisitante()) ||
                            (aposta.get().getResultado().equals("VITORIA_VISITANTE") && partida.getPlacarVisitante() > partida.getPlacarMandante()) ||
                            (aposta.get().getResultado().equals("EMPATE") && partida.getPlacarMandante().equals(partida.getPlacarVisitante()))) {
                        aposta.get().setStatus("GANHOU");
                    } else {
                        aposta.get().setStatus("PERDEU");
                    }
                    apostaRepository.save(aposta.get());
                }
                return aposta.get();
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aposta ainda não foi realizada.");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aposta não encontrada.");
    }

    public List<Aposta> listar(String status) {
        if (status != null) {
            return apostaRepository.findByStatus(status);
        }
        return apostaRepository.findAll();
    }
}
