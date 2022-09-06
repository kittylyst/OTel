package io.opentelemetry.examples.animal;

import io.micrometer.core.instrument.MeterRegistry;
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
public class AnimalController {
  private static final Map<String, String> SERVICES = Map.of(
          "mammals", "http://mammal-service:8082/getAnimal",
          "fish", "http://fish-service:8081/getAnimal");

  private final MeterRegistry registry;

  private static final HttpServletRequestExtractor EXTRACTOR = new HttpServletRequestExtractor();

  @Autowired private HttpServletRequest httpServletRequest;

  public AnimalController(MeterRegistry registry) {
    this.registry = registry;
  }

  @GetMapping("/battle")
  public String makeBattle() throws IOException, InterruptedException {
    var good = fetchRandomAnimal();
    var evil = fetchRandomAnimal();
    return "{ \"good\": \""+ good + "\", \"evil\": \""+ evil + "\" }";
  }

  private String fetchRandomAnimal() throws IOException, InterruptedException {
    List<String> keys = List.copyOf(SERVICES.keySet());
    var world = keys.get((int) (SERVICES.size() * Math.random()));
    var location = SERVICES.get(world);

    return fetchAnimal(location);
  }

}
