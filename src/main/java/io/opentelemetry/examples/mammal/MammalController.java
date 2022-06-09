package io.opentelemetry.examples.mammal;

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

import static io.opentelemetry.examples.utils.OpenTelemetryConfig.extractContext;
import static io.opentelemetry.examples.utils.OpenTelemetryConfig.injectContext;

@RestController
public class MammalController {
    private final List<String> MAMMALS = List.of("monkey", "jaguar", "platypus");

    private static final HttpServletRequestExtractor EXTRACTOR = new HttpServletRequestExtractor();

    @Autowired private HttpServletRequest httpServletRequest;

    @GetMapping("/getAnimal")
    public String makeBattle() throws IOException, InterruptedException {
        // Extract the propagated context from the request. In this example, context will be
        // extracted from the Animal Service.
        var extractedContext = extractContext(httpServletRequest,EXTRACTOR);


        try (var scope = extractedContext.makeCurrent()) {
            // Start a span in the scope of the extracted context.
            var span = serverSpan("/getAnimal", HttpMethod.GET.name());

            try {
                // Random pause
                Thread.sleep((int) (20 * Math.random()));
                // Return random mammal
                return MAMMALS.get((int)(MAMMALS.size() * Math.random()));
            } finally {
                span.end();
            }
        }
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
        return GlobalOpenTelemetry.getTracer(io.opentelemetry.examples.mammal.MammalController.class.getName())
                .spanBuilder(path)
                .setSpanKind(SpanKind.SERVER)
                .setAttribute(SemanticAttributes.HTTP_METHOD, method)
                .setAttribute(SemanticAttributes.HTTP_SCHEME, "http")
                .setAttribute(SemanticAttributes.HTTP_HOST, "mammal-service:8081")
                .setAttribute(SemanticAttributes.HTTP_TARGET, path)
                .startSpan();
    }
}
