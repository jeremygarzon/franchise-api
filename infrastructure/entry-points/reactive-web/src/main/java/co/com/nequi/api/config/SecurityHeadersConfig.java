package co.com.nequi.api.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersConfig implements WebFilter {

    private static final String STRICT_CSP =
            "default-src 'self'; frame-ancestors 'self'; form-action 'self'";

    // Swagger UI needs inline styles/scripts and loads webjar assets, so it
    // requires a relaxed policy scoped only to the documentation endpoints.
    private static final String DOCS_CSP =
            "default-src 'self'; style-src 'self' 'unsafe-inline'; "
                    + "script-src 'self' 'unsafe-inline'; img-src 'self' data:; "
                    + "frame-ancestors 'self'; form-action 'self'";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        boolean isDocs = path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars");

        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set("Content-Security-Policy", isDocs ? DOCS_CSP : STRICT_CSP);
        headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.remove("Server");
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Referrer-Policy", "strict-origin-when-cross-origin");
        return chain.filter(exchange);
    }
}
