package com.example.gateway.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Provider
public class LoggingFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(LoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        String headers = requestContext.getHeaders().toString();

        // Читаем тело (только если Content-Type JSON)
        String body = "";
        if ("application/json".equals(requestContext.getMediaType().toString())) {
            InputStream inputStream = requestContext.getEntityStream();
            body = readFromStream(inputStream);
            // Восстанавливаем InputStream для дальнейшего использования
            requestContext.setEntityStream(new java.io.ByteArrayInputStream(body.getBytes()));
        }

        log.info("🌐 HTTP REQUEST:");
        log.info("   Method: " + method);
        log.info("   URI: " + uri);
        log.info("   Headers: " + headers);
        log.info("   Body: " + body);
        log.info("   ------------------------");
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}