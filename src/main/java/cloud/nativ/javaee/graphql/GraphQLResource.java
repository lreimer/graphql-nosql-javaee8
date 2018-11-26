package cloud.nativ.javaee.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

@Slf4j
@ApplicationScoped
@Path("graphql")
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLResource {

    private GraphQLSchema graphQLSchema;

    /**
     * Initialize the GraphQL schema with a type definition registry and a runtime wiring.
     */
    @PostConstruct
    void initialize() {
        InputStream schema = GraphQLResource.class.getResourceAsStream("/schema.graphqls");
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(new InputStreamReader(schema));

        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("QueryType", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    /**
     * Perform standard GraphQL GET request. The GraphQL query should be specified in the "query" query string.
     * Query variables can be sent as a JSON-encoded string in an additional query parameter called variables.
     * If the query contains several named operations, an operationName query parameter can be used to control
     * which one should be executed.
     *
     * @param query         the GraphQL query string
     * @param operationName the operation name, is optional
     * @param variables     the JSON encoded string with additional query parameters, is optional
     * @return the JSON query response
     */
    @GET
    public Response get(@QueryParam("query") @NotBlank String query,
                        @QueryParam("operationName") @DefaultValue("") String operationName,
                        @QueryParam("variables") @DefaultValue("") String variables) {
        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();

        // TODO add operationName and variables
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(query).build();
        ExecutionResult executionResult = build.execute(executionInput);

        return Response.ok(executionResult.toSpecification()).build();
    }

    /**
     * Perform standard GraphQL POST request. Use the application/json content type, and include a JSON-encoded body
     * of the following form:
     * <code>
     * {
     * "query": "...",
     * "operationName": "...",
     * "variables": { "myVariable": "someValue", ... }
     * }
     * </code>
     * <p>
     * The operationName and variables are optional fields. operationName is only required if multiple
     * operations are present in the query.
     * <p>
     * If the "query" query string parameter is present (as in the GET example above), it should be parsed and
     * handled in the same way as the HTTP GET case.
     *
     * @param queryPayload the GraphQL query, as JSON-encoded body
     * @param query        the GraphQL query string
     * @return the JSON query response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@NotNull JsonObject queryPayload, @QueryParam("query") @DefaultValue("") String query) {
        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();

        ExecutionResult executionResult;
        if ("".equalsIgnoreCase(query)) {
            // TODO add operationName and variables
            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(queryPayload.getString("query"))
                    .build();
            executionResult = build.execute(executionInput);
        } else {
            executionResult = build.execute(query);
        }

        return Response.ok(executionResult.toSpecification()).build();
    }

    /**
     * Perform GraphQL query. Use dedicated "application/graphql" Content-Type header and treat the
     * HTTP POST body contents as the GraphQL query string.
     *
     * @param query the GraphQL query string from the POST body
     * @return the JSON query response
     */
    @POST
    @Consumes("application/graphql")
    public Response post(@NotNull String query) {
        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        ExecutionResult executionResult = build.execute(query);

        return Response.ok(executionResult.toSpecification()).build();
    }
}
