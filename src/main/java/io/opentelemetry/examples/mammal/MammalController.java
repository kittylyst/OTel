package io.opentelemetry.examples.mammal;

import io.opentelemetry.examples.utils.HttpServletRequestExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.opentelemetry.examples.utils.Misc.fetchAnimal;

@RestController
public class MammalController {
    private static final Map<String, String> SERVICES = Map.of(
            "mustelids", "http://mustelid-service:8080/getAnimal",
            "felines", "http://feline-service:8080/getAnimal");

    private static final HttpServletRequestExtractor EXTRACTOR = new HttpServletRequestExtractor();

    @Autowired private HttpServletRequest httpServletRequest;

    @GetMapping("/getAnimal")
    public String makeBattle() throws IOException, InterruptedException {
        return fetchRandomAnimal();
    }

    private String fetchRandomAnimal() throws IOException, InterruptedException {
        List<String> keys = List.copyOf(SERVICES.keySet());
        var world = keys.get((int) (SERVICES.size() * Math.random()));
        var location = SERVICES.get(world);

        return fetchAnimal(location);
    }
}
