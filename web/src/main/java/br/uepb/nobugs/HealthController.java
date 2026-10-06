package br.uepb.nobugs;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  //diz pro Spring que a classe atende requisições web e devolve JSON (euacho)
public class HealthController {

    @GetMapping("/saude")
    public Map<String, String> saude() {
        return Map.of("status", "ok");
    }
}