package io.opentelemetry.examples.feline;

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
public class FelineController {
    private final List<String> CATS = List.of("tabby", "jaguar", "leopard");

    private static final HttpServletRequestExtractor EXTRACTOR = new HttpServletRequestExtractor();

    @Autowired private HttpServletRequest httpServletRequest;

    @GetMapping("/getAnimal")
    public String makeBattle() throws IOException, InterruptedException {
        // Extract the propagated context from the request. In this example, context will be
        // extracted from the Animal Service.
        var extractedContext = extractContext(httpServletRequest,EXTRACTOR);


        try (var scope = extractedContext.makeCurrent()) {
            // Start a span in the scope of the extracted context.
            var span = serverSpan("/getAnimal", HttpMethod.GET.name(), FelineController.class.getName(), "feline-service:8085");

            try {
                // Random pause
                Thread.sleep((int) (20 * Math.random()));
                // Return random mammal
                return CATS.get((int)(CATS.size() * Math.random()));
            } finally {
                span.end();
            }
        }
    }
}
