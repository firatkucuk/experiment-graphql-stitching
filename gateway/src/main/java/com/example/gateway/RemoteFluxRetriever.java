package com.example.gateway;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.atlassian.braid.source.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class RemoteFluxRetriever<C> implements GraphQLRemoteRetriever<C> {

    private final WebClient webClient;
    private final String schemaUrl;

    RemoteFluxRetriever(final WebClient webClient, final String schemaUrl) {
        this.webClient = webClient;
        this.schemaUrl = schemaUrl;
    }

    @Override
    public CompletableFuture<Map<String, Object>> queryGraphQL(final Query query, final C c) {

        try {

            final ExecutionInput executionInput = query.asExecutionInput();
            final String      json = new ObjectMapper().writeValueAsString(executionInput);

            final CompletableFuture<Map<String, Object>> completableFuture = new CompletableFuture<>();

            this.webClient
                .post()
                .uri(this.schemaUrl)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(2))
                .doOnNext(stringObjectMap -> System.out.println(stringObjectMap))
                .subscribe(stringObjectMap -> {
                    System.out.println(stringObjectMap);
                    completableFuture.complete(stringObjectMap);
                });
                // .toEntity()
//                .doOnNext(stringObjectMap -> {
//                    System.out.println(stringObjectMap);
//                    completableFuture.complete(stringObjectMap);
//                })
//                .subscribe();

            return completableFuture;
        } catch (final IOException error) {
            System.out.println(error);
            return null;
        }
    }
}