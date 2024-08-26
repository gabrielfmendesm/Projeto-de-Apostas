package br.insper.aposta.aposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aposta")
public class ApostaController {

    @Autowired
    private ApostaService apostaService;

    @GetMapping
    public List<Aposta> listar(@RequestParam(required = false) String status) {
        return apostaService.listar(status);
    }

    @GetMapping("/{idAposta}")
    public Aposta verificarAposta(@PathVariable String idAposta) {
        return apostaService.verificarAposta(idAposta);
    }

    @PostMapping
    public Aposta salvar(@RequestBody Aposta aposta) {
        return apostaService.salvar(aposta);
    }
}
