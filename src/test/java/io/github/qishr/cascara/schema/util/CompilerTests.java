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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.lang.json.ast.JsonNode;
import io.github.qishr.cascara.lang.json.processor.JsonAstParser;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.structure.LazySchemaNode;
import io.github.qishr.cascara.schema.structure.ObjectSchemaNode;
import io.github.qishr.cascara.schema.structure.SchemaNode;

public class CompilerTests {
    @Test
    void compiler_shouldPreserveCustomMetadata() {
        String json = """
        {
        "$id": "cascara://core/schema-service/dynamic/cascara.schema/compiler-tests",
        "definitions": {
            "item": { "type": "object", "properties": { "status": { "type": "string", "x-tracked": true } } },
            "task": { "x-parent": "item", "type": "object", "properties": { "name": { "type": "string" } } }
        }
        }
        """;
        JsonAstParser parser = new JsonAstParser();
        JsonNode doc = parser.parse(json);

        SchemaResolver resolver = new SchemaResolver();
        SchemaCompiler compiler = new SchemaCompiler(resolver);
        Schema schema = compiler.compile(doc);

        ObjectSchemaNode taskNode = (ObjectSchemaNode) schema.getDefinition("task");

        assertNotNull(taskNode.getExtension("x-parent"), "Compiler dropped 'parent' keyword!");
        ObjectSchemaNode item = (ObjectSchemaNode)schema.getDefinition("item");
        SchemaNode statusNode = item.getProperty("status");
        assertNotNull(statusNode.getExtension("x-tracked"), "Compiler dropped 'x-tracked' hint!");
    }

    @Test
    void compiler_test_01() {
        String json = getStringResource("/io/github/qishr/cascara/schema/util/schema-01.json");

        JsonAstParser parser = new JsonAstParser();
        JsonNode doc = parser.parse(json);

        SchemaResolver resolver = new SchemaResolver();
        SchemaCompiler compiler = new SchemaCompiler(resolver);
        Schema schema = compiler.compile(doc);

        ObjectSchemaNode taskNode = (ObjectSchemaNode) schema.getDefinition("task");

        SchemaNode status = taskNode.getProperty("status");
        if (status instanceof LazySchemaNode lazy) {
            SchemaNode resolved = lazy.getResolved();
            assertNotNull(resolved);
            if (resolved instanceof ObjectSchemaNode resolvedNode) {
                SchemaNode order = resolvedNode.getProperty("order");
                assertNotNull(order);
            }
        }
    }


    public static String getStringResource(String path) {
        try (var is = CompilerTests.class.getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource: " + path, e);
        }
    }
}
