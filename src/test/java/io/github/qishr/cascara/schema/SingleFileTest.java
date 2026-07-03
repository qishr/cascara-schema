package io.github.qishr.cascara.schema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.diagnostic.SilentCollectingReporter;
import io.github.qishr.cascara.lang.json.JsonOptions;
import io.github.qishr.cascara.lang.json.ast.JsonNode;
import io.github.qishr.cascara.lang.json.processor.JsonAstParser;
import io.github.qishr.cascara.schema.util.SchemaCompiler;
import io.github.qishr.cascara.schema.util.SchemaValidator;

class SingleFileTest {

    // private final JsonOptions options = new JsonOptions().setStrict(true);

    // // TODO: diagnostic level in one place for all tests?

    // private JsonAstParser parser = new JsonAstParser()
    //         .setOptions(options);
    //         // .setReporter(new StandardReporter().setLevel(Level.TRACE));

    // @Disabled
    @Test
    void testSingleFile() throws IOException {
        String schemaString = readStringResource("complex-schema.json");
        String dataString = readStringResource("complex-data.json");

        JsonAstParser parser = new JsonAstParser();
        Schema cascaraSchema = new SchemaCompiler().compile(parser.parse(schemaString));
        JsonNode cascaraData = parser.parse(dataString);

        int times = 1000000;

        for (int i = 0; i < times; i++) {
            cascaraSchema.validate(cascaraData);
        }
    }

    private String readStringResource(String name) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(name);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        String content = reader.readAllAsString();
        return content;
    }
}