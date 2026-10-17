package io.github.qishr.cascara.schema.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.lang.generic.GenericAstMapEntry;
import io.github.qishr.cascara.common.lang.generic.GenericAstMap;

public class SchemaGeneratorStaticUriFieldTest {
    public static class SchemaFieldTestClass {
        public static final String SCHEMA = "cascara://core/schema-service/draft/cascara.common/schema-field-test/0.1.0";
    }

    @Test
    void test_staticSchemaUri() {
        SchemaGenerator generator = new SchemaGenerator();
        GenericAstMap schemaDoc = generator.generate(SchemaFieldTestClass.class);
        assertTrue(schemaDoc != null);

        // SCHEMA should not end up as a property
        GenericAstMap props = schemaDoc.getMap("properties");
        for (GenericAstMapEntry entry : props.entrySet()) {
            String name = entry.getKeyString();
            assertNotEquals("SCHEMA", name, "SCHEMA should not be a property");
        }

        // $id should reflect what SCHEMA is in the class
        String schemaUri = schemaDoc.getString("$id");
        assertEquals(SchemaFieldTestClass.SCHEMA, schemaUri);
    }
}
