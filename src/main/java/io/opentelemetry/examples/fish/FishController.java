package io.opentelemetry.examples.fish;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.examples.utils.HttpServletRequestExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static io.opentelemetry.examples.utils.OpenTelemetryConfig.extractContext;
import static io.opentelemetry.examples.utils.OpenTelemetryConfig.serverSpan;

@RestController
public class FishController {
  private static final HttpServletRequestExtractor EXTRACTOR = new HttpServletRequestExtractor();
  private static final List<String> FISH = List.of("salmon", "cod", "turbot");

  @Autowired private HttpServletRequest httpServletRequest;

  @GetMapping("/getAnimal")
  public String makeBattle() throws IOException, InterruptedException {
    // Extract the propagated context from the request. In this example, context will be
    // extracted from the Animal Service.
    var extractedContext = extractContext(httpServletRequest,EXTRACTOR);


    try (var scope = extractedContext.makeCurrent()) {
      // Start a span in the scope of the extracted context.
      var span = serverSpan("/getAnimal", HttpMethod.GET.name(), FishController.class.getName(), "fish-service:8083");

      try {
        // Random pause
        Thread.sleep((int) (20 * Math.random()));
        // Return random fish
        return FISH.get((int)(FISH.size() * Math.random()));
      } finally {
        span.end();
      }
    }
  }
}
