package io.opentelemetry.examples.fish;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.examples.utils.HttpServletRequestExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static io.opentelemetry.examples.utils.OpenTelemetryConfig.injectContext;

@RestController
public class FishController {
  private static final Map<String, String> PORTS = Map.of("mammals", "8081", "fish", "8083");

  private static final HttpServletRequestExtractor EXTRACTOR = new HttpServletRequestExtractor();

  @Autowired private HttpServletRequest httpServletRequest;

  @GetMapping("/battle")
  public String makeBattle() throws IOException, InterruptedException {
    // Extract the propagated context from the request. In this example, no context will be
    // extracted from the request since this route initializes the trace.
    var extractedContext = extractContext();

    try (var scope = extractedContext.makeCurrent()) {
      // Start a span in the scope of the extracted context.
      var span = serverSpan("/battle", HttpMethod.GET.name());

      // Send the two requests and return the response body as the response, and end the span.
      try {
        var good = fetchAnimal(span);
        var evil = fetchAnimal(span);
        return "{ \"good\": \""+ good + "\", \"evil\": \""+ evil + "\" }";
      } finally {
        span.end();
      }
    }
  }

  private String fetchAnimal(Span span) throws IOException, InterruptedException {
    List<String> keys = List.copyOf(PORTS.keySet());
    var world = keys.get((int) (PORTS.size() * Math.random()));
    var location = "http://localhost:"+ PORTS.get(world) +"/getAnimal";

    var client = HttpClient.newHttpClient();
    var requestBuilder = HttpRequest.newBuilder().uri(URI.create(location));

    // Inject the span's content into the request's headers.
    injectContext(span, requestBuilder);

    return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).body();
  }

  /**
   * Extract the propagated context from the {@link #httpServletRequest}.
   *
   * @return the extracted context
   */
  private Context extractContext() {
    return GlobalOpenTelemetry.getPropagators()
        .getTextMapPropagator()
        .extract(Context.current(), httpServletRequest, EXTRACTOR);
  }

  /**
   * Create a {@link SpanKind#SERVER} span, setting the parent context if available from the {@link
   * #httpServletRequest}.
   *
   * @param path the HTTP path
   * @param method the HTTP method
   * @return the span
   */
  private Span serverSpan(String path, String method) {
    return GlobalOpenTelemetry.getTracer(FishController.class.getName())
        .spanBuilder(path)
        .setSpanKind(SpanKind.SERVER)
        .setAttribute(SemanticAttributes.HTTP_METHOD, method)
        .setAttribute(SemanticAttributes.HTTP_SCHEME, "http")
        .setAttribute(SemanticAttributes.HTTP_HOST, "localhost:8080")
        .setAttribute(SemanticAttributes.HTTP_TARGET, path)
        .startSpan();
  }



}
