package com.example.gateway;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.atlassian.braid.source.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import graphql.ExecutionInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteRetriever<C> implements GraphQLRemoteRetriever<C> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final       String    url;

    RemoteRetriever(final String url) {
        this.url = url;
    }

    @Override
    public CompletableFuture<Map<String, Object>> queryGraphQL(final Query query, final C c) {

        final Gson         gson   = new Gson();
        final OkHttpClient client = new OkHttpClient();

        final ExecutionInput executionInput = query.asExecutionInput();

        try {

            final String      json = new ObjectMapper().writeValueAsString(executionInput);
            final RequestBody body = RequestBody.create(JSON, json);
            final Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();

            final Response                response   = client.newCall(request).execute();
            final HashMap<String, Object> jsonResult = gson.fromJson(response.body().string(), HashMap.class);
            return CompletableFuture.completedFuture(jsonResult);
        } catch (final IOException error) {
            System.out.println(error);
            return null;
        }
    }
}