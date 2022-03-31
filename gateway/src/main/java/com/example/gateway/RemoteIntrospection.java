package com.example.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaPrinter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteIntrospection {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String url;

    RemoteIntrospection(final String url) {
        this.url = url;
    }

    public Reader get() {
        final Gson         gson   = new Gson();
        final OkHttpClient client = new OkHttpClient();

        final Map<String, Object> bodyMap = new HashMap<>() {{
            this.put("query", introspectionQuery());
            this.put("operationName", "IntrospectionQuery");
        }};

        try {
//            final String      json = gson.toJson(bodyMap);
            final String      json = new ObjectMapper().writeValueAsString(bodyMap);
            final RequestBody body = RequestBody.create(JSON, json);
            final Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();

            final Response            response            = client.newCall(request).execute();
            final Map<String, Object> introspectionResult = gson.fromJson(response.body().string(), HashMap.class);
            final Document            schema              = new IntrospectionResultToSchema().createSchemaDefinition((Map<String, Object>) introspectionResult.get("data"));
            final String              printedSchema       = new SchemaPrinter().print(schema);
            return new StringReader(printedSchema);
        } catch (final IOException ex) {
            System.out.println(ex);
            return new StringReader("");
        }
    }

    private static String introspectionQuery2() {
        return "query IntrospectionQuery {\n" +
            "      __schema {\n" +
            "        \n" +
            "        queryType { name }\n" +
            "        mutationType { name }\n" +
            "        subscriptionType { name }\n" +
            "        types {\n" +
            "          ...FullType\n" +
            "        }\n" +
            "        directives {\n" +
            "          name\n" +
            "          description\n" +
            "          \n" +
            "          locations\n" +
            "          args {\n" +
            "            ...InputValue\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "\n" +
            "    fragment FullType on __Type {\n" +
            "      kind\n" +
            "      name\n" +
            "      description\n" +
            "      \n" +
            "      fields(includeDeprecated: true) {\n" +
            "        name\n" +
            "        description\n" +
            "        args {\n" +
            "          ...InputValue\n" +
            "        }\n" +
            "        type {\n" +
            "          ...TypeRef\n" +
            "        }\n" +
            "        isDeprecated\n" +
            "        deprecationReason\n" +
            "      }\n" +
            "      inputFields {\n" +
            "        ...InputValue\n" +
            "      }\n" +
            "      interfaces {\n" +
            "        ...TypeRef\n" +
            "      }\n" +
            "      enumValues(includeDeprecated: true) {\n" +
            "        name\n" +
            "        description\n" +
            "        isDeprecated\n" +
            "        deprecationReason\n" +
            "      }\n" +
            "      possibleTypes {\n" +
            "        ...TypeRef\n" +
            "      }\n" +
            "    }\n" +
            "\n" +
            "    fragment InputValue on __InputValue {\n" +
            "      name\n" +
            "      description\n" +
            "      type { ...TypeRef }\n" +
            "      defaultValue\n" +
            "      \n" +
            "      \n" +
            "    }\n" +
            "\n" +
            "    fragment TypeRef on __Type {\n" +
            "      kind\n" +
            "      name\n" +
            "      ofType {\n" +
            "        kind\n" +
            "        name\n" +
            "        ofType {\n" +
            "          kind\n" +
            "          name\n" +
            "          ofType {\n" +
            "            kind\n" +
            "            name\n" +
            "            ofType {\n" +
            "              kind\n" +
            "              name\n" +
            "              ofType {\n" +
            "                kind\n" +
            "                name\n" +
            "                ofType {\n" +
            "                  kind\n" +
            "                  name\n" +
            "                  ofType {\n" +
            "                    kind\n" +
            "                    name\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }";
    }

    private static String introspectionQuery() {
        return "query IntrospectionQuery {\n" +
            "__schema {\n" +
              "queryType { name }\n" +
              "mutationType { name }\n" +
              "subscriptionType { name }\n" +
              "types {\n" +
                "...FullType\n" +
              "}\n" +
              "directives {\n" +
                "name\n" +
                "description\n" +
                "locations\n" +
                "args {\n" +
                  "...InputValue\n" +
                "}\n" +
              "}\n" +
            "}\n" +
          "}\n" +
          "fragment FullType on __Type {\n" +
            "kind\n" +
            "name\n" +
            "description\n" +
            "fields(includeDeprecated: true) {\n" +
              "name\n" +
              "description\n" +
              "args {\n" +
                "...InputValue\n" +
              "}\n" +
              "type {\n" +
                "...TypeRef\n" +
              "}\n" +
              "isDeprecated\n" +
              "deprecationReason\n" +
            "}\n" +
            "inputFields {\n" +
              "...InputValue\n" +
            "}\n" +
            "interfaces {\n" +
              "...TypeRef\n" +
            "}\n" +
            "enumValues(includeDeprecated: true) {\n" +
              "name\n" +
              "description\n" +
              "isDeprecated\n" +
              "deprecationReason\n" +
            "}\n" +
            "possibleTypes {\n" +
              "...TypeRef\n" +
            "}\n" +
          "}\n" +
          "fragment InputValue on __InputValue {\n" +
            "name\n" +
            "description\n" +
            "type { ...TypeRef }\n" +
            "defaultValue\n" +
          "}\n" +
          "fragment TypeRef on __Type {\n" +
            "kind\n" +
            "name\n" +
            "ofType {\n" +
              "kind\n" +
              "name\n" +
              "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                  "kind\n" +
                  "name\n" +
                  "ofType {\n" +
                    "kind\n" +
                    "name\n" +
                    "ofType {\n" +
                      "kind\n" +
                      "name\n" +
                      "ofType {\n" +
                        "kind\n" +
                        "name\n" +
                        "ofType {\n" +
                          "kind\n" +
                          "name\n" +
                        "}\n" +
                      "}\n" +
                    "}\n" +
                  "}\n" +
                "}\n" +
              "}\n" +
            "}\n" +
          "}\n" ;
    }
}