package com.example.gateway;

import com.atlassian.braid.source.SchemaLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static com.atlassian.braid.source.SchemaUtils.loadSchema;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class RemoteIntrospectionLoader implements SchemaLoader {


    private static final String INTROSPECTION_QUERY = readIntrospectionQuery();

    private final String schemaUrl;

    RemoteIntrospectionLoader(final String schemaUrl) {

        this.schemaUrl = schemaUrl;
    }

    public static String readIntrospectionQuery() {

        try {
            final InputStream inputStream = new ClassPathResource("inrospection.graphql").getInputStream();
            final String      query           = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            final Map<String, Object> bodyMap = new HashMap<>() {{
                this.put("query", query);
                this.put("operationName", "IntrospectionQuery");
            }};

            return new ObjectMapper().writeValueAsString(bodyMap);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public TypeDefinitionRegistry load() {

        try {
            final Map<String, Object> introspectionResult = WebClient.builder().build()
                .post()
                .uri(this.schemaUrl)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(INTROSPECTION_QUERY)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                .share()
                .block()
                .getBody();

            final String data = new ObjectMapper().writeValueAsString(introspectionResult.get("data"));

            return loadSchema(Type.INTROSPECTION, new StringReader(data));
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
