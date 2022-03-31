package com.example.gateway;

import com.atlassian.braid.Braid;
import com.atlassian.braid.BraidGraphQL;
import com.atlassian.braid.SchemaNamespace;
import com.atlassian.braid.SchemaSource;
import com.atlassian.braid.source.QueryExecutorSchemaSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GraphQLController {

    private static final SchemaNamespace PROJECT1_NAMESPACE  = SchemaNamespace.of("project1");
    private static final String          PROJECT1_SCHEMA_URL = "http://localhost:8081/graphql";
    private static final SchemaNamespace PROJECT2_NAMESPACE  = SchemaNamespace.of("project2");
    private static final String          PROJECT2_SCHEMA_URL = "http://localhost:8082/graphql";

    private static SchemaSource createSchemaSource(final SchemaNamespace namespace, final String schemaUrl) {

        return QueryExecutorSchemaSource
            .builder()
            .namespace(namespace)
            .schemaProvider(() -> new RemoteIntrospection(schemaUrl).get())
            .remoteRetriever(new RemoteRetriever<>(schemaUrl))
            // .schemaLoader(() -> new RemoteIntrospectionLoader(schemaUrl).load())
            //.remoteRetriever(new RemoteFluxRetriever<>(webClient, schemaUrl))
            .build();
    }

    @PostMapping(path = "/graphql", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    String graphql(@RequestBody final GraphQLParameters params) throws JsonProcessingException {

        final BraidGraphQL graphql = Braid
            .builder()
            .schemaSource(createSchemaSource(PROJECT1_NAMESPACE, PROJECT1_SCHEMA_URL))
            .schemaSource(createSchemaSource(PROJECT2_NAMESPACE, PROJECT2_SCHEMA_URL))
            .build()
            .newGraphQL();

        final ExecutionResult result = graphql
            .execute(
                ExecutionInput
                    .newExecutionInput()
                    .query(params.getQuery())
                    .build())
            .join();

        return new ObjectMapper().writeValueAsString(result.toSpecification());
    }
}