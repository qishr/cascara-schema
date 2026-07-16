// # License & Terms
//
// This file is part of **Cascara**.
//
// **Cascara** is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
//
// ---
//
// ## Special Runtime Exception
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules,
// and to copy and distribute the resulting executable under terms of your
// choice, provided that you also meet, for each linked independent module,
// the terms and conditions of the license of that module.
//
// An independent module is a module which is not derived from or based on
// this library. If you modify this library, you may extend this exception
// to your version of the library, but you are not obligated to do so. If
// you do not wish to do so, delete this exception statement from your
// version.


package io.github.qishr.cascara.schema.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.Diagnostic;
import io.github.qishr.cascara.common.diagnostic.SilentCollectingReporter;
import io.github.qishr.cascara.common.lang.reference.ReferenceMapNode;
import io.github.qishr.cascara.common.lang.reference.ReferenceNode;
import io.github.qishr.cascara.common.lang.type.DateTimeTypeDescriptor;
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
        private ZonedDateTime dateTime;
    }

    @Test
    void test_validDateTime() {
        SchemaGenerator generator = new SchemaGenerator();
        generator.registerTypeDescriptor(new DateTimeTypeDescriptor());
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