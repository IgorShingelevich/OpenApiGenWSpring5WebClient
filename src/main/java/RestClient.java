package org.openapitools;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.http.HttpMethod;
import java.net.URI;
import java.util.List;

@Service
public class RestClient {
    private static final String HEADER_RQ_ID = "X-Request-ID";
    private final WebClient apiUiClient;

    public RestClient(final WebClient apiUiClient) {
        this.apiUiClient = apiUiClient;
    }

    private String buildRQID() {
        return java.util.UUID.randomUUID().toString();
    }

    public <T> T post(final Object body, final Class<T> returnedClass, final Object... pathSegments) {
        return apiUiClient.post()
                          .uri(uriBuilder -> buildURI(uriBuilder, pathSegments))
                          .header(HEADER_RQ_ID, buildRQID())
                          .bodyValue(body)
                          .retrieve()
                          .bodyToMono(returnedClass)
                          .block();
    }

    public <T> T get(final Class<T> returnedClass, final Object... pathSegments) {
        return apiUiClient.get()
                          .uri(uriBuilder -> buildURI(uriBuilder, pathSegments))
                          .header(HEADER_RQ_ID, buildRQID())
                          .retrieve()
                          .bodyToMono(returnedClass)
                          .block();
    }

    public <T> T get(final Class<T> returnedClass, final List<String> queryParams, final Object... pathSegments) {
        return apiUiClient.get()
                          .uri(uriBuilder -> buildURIwQueryParams(uriBuilder, queryParams, pathSegments))
                          .header(HEADER_RQ_ID, buildRQID())
                          .retrieve()
                          .bodyToMono(returnedClass)
                          .block();
    }

    private URI buildURIwQueryParams(
            final UriBuilder uriBuilder,
            final List<String> queryParams,
            final Object[] pathSegments
    ) {
        for (final Object pathSegment : pathSegments) {
            uriBuilder.pathSegment(String.valueOf(pathSegment).replaceAll("/", ""));
        }
        for (final String param : queryParams) {
            String[] parts = param.split("=");
            if (parts.length == 2) {
                uriBuilder.queryParam(parts[0], parts[1]);
            }
        }
        return uriBuilder.build();
    }

    public <T> T delete(final Object body, final Class<T> returnedClass, final Object... pathSegments) {
        return apiUiClient.method(HttpMethod.DELETE)
                          .uri(uriBuilder -> buildURI(uriBuilder, pathSegments))
                          .header(HEADER_RQ_ID, buildRQID())
                          .bodyValue(body)
                          .retrieve()
                          .bodyToMono(returnedClass)
                          .block();
    }

    public <T> T delete(final Class<T> returnedClass, final Object... pathSegments) {
        return apiUiClient.delete()
                          .uri(uriBuilder -> buildURI(uriBuilder, pathSegments))
                          .header(HEADER_RQ_ID, buildRQID())
                          .retrieve()
                          .bodyToMono(returnedClass)
                          .block();
    }

    public <T> T put(final Object body, final Class<T> returnedClass, final Object... pathSegments) {
        return apiUiClient.put()
                          .uri(uriBuilder -> buildURI(uriBuilder, pathSegments))
                          .header(HEADER_RQ_ID, buildRQID())
                          .bodyValue(body)
                          .retrieve()
                          .bodyToMono(returnedClass)
                          .block();
    }

    private URI buildURI(final UriBuilder uriBuilder, final Object... pathSegments) {
        for (final Object pathSegment : pathSegments) {
            uriBuilder.pathSegment(String.valueOf(pathSegment).replaceAll("/", ""));
        }
        return uriBuilder.build();
    }
}