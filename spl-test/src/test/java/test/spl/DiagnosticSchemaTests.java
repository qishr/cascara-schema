package test.spl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.Diagnostic;
import io.github.qishr.cascara.common.lang.generic.GenericAstMap;
import io.github.qishr.cascara.lang.json.processor.JsonConverter;
import io.github.qishr.cascara.schema.util.SchemaGenerator;

public class DiagnosticSchemaTests extends SchemaTestBase {
    @Test
    void test_diagnosticSchema() {
        reporter.debug("Hello, World!");

        SchemaGenerator generator = new SchemaGenerator();
        GenericAstMap decompiled = generator.generate(Diagnostic.class);

        String json = new JsonConverter().toString(decompiled);
        assertNotNull(json);

        reporter.debug(json);
    }
}
