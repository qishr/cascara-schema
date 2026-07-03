package io.github.qishr.cascara.schema.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.Diagnostic;
import io.github.qishr.cascara.common.diagnostic.SilentCollectingReporter;
import io.github.qishr.cascara.common.diagnostic.StandardReporter;
import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.lang.reference.ReferenceMapNode;
import io.github.qishr.cascara.common.lang.reference.ReferenceNode;
import io.github.qishr.cascara.common.lang.type.LocalDateTimeTypeDescriptor;
import io.github.qishr.cascara.common.service.ServiceProviderLayer;
import io.github.qishr.cascara.lang.json.ast.JsonNode;
import io.github.qishr.cascara.lang.json.processor.JsonAstParser;
import io.github.qishr.cascara.lang.json.processor.JsonConverter;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.annotation.SchemaDefinition;
import io.github.qishr.cascara.schema.annotation.SchemaProperty;

public class FormatTests {
    @SchemaDefinition
    public static class TestClass {
        @SchemaProperty
        private LocalDateTime dateTime;
    }

    @Test
    void test_validDateTime() {
        SchemaGenerator generator = new SchemaGenerator();
        generator.registerTypeDescriptor(new LocalDateTimeTypeDescriptor());
        ReferenceNode schemaDoc = generator.generate(TestClass.class);

        Schema schema = new SchemaCompiler().compile(schemaDoc);

        ReferenceMapNode decompiled = new SchemaDecompiler().decompile(schema);
        String schemaString = new JsonConverter().toText(decompiled);
        System.out.println(schemaString);


        String json = "{\"dateTime\": \"2026-07-02T15:16:07Z\"}";
        JsonNode root = new JsonAstParser().parse(json);

        List<Diagnostic> errors = new ArrayList<>();
        SilentCollectingReporter collector = new SilentCollectingReporter();
        collector.setProblemCollector(p -> errors.add(p));

        boolean valid = schema.validate(root, collector);

        assertTrue(errors.isEmpty());
        assertTrue(valid);
    }

    // ServiceProviderLayer.getRootLayer(new StandardReporter().setLevel(Level.DEBUG));

    @Test
    void test_invalidDateTime() {
        SchemaGenerator generator = new SchemaGenerator();
        ReferenceNode schemaDoc = generator.generate(TestClass.class);

        SchemaCompiler compiler = new SchemaCompiler();
        Schema schema = compiler.compile(schemaDoc);

        String json = "{\"dateTime\": \"2026-xx-02T15:16:07Z\"}";
        JsonNode root = new JsonAstParser().parse(json);

        List<Diagnostic> errors = new ArrayList<>();
        SilentCollectingReporter collector = new SilentCollectingReporter();
        collector.setProblemCollector(p -> errors.add(p));

        boolean valid = schema.validate(root, collector);

        for (Diagnostic error : errors) {
            System.err.println("Got expected validation error: " + error.getMessage());
        }

        assertFalse(errors.isEmpty());
        assertFalse(valid);
    }
}