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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.lang.plain.PlainMapNode;
import io.github.qishr.cascara.lang.json.processor.JsonConverter;
import io.github.qishr.cascara.schema.Schema;

public class SchemaGeneratorTests extends SchemaTestBase {

    @Test
    void test_buildSchema_2classes() {
        URI uri = URI.create("cascara://core/schema-service/draft/cascara.schema/generator-test-3/0.1.0");
        SchemaBuilder sb = new SchemaBuilder();
        Schema schema = sb.buildSchema(uri, OuterTestClass.class, TestClass.class);

        PlainMapNode decompiled = new SchemaDecompiler().decompile(schema);
        String json = new JsonConverter().toString(decompiled);
        assertNotNull(json);
    }

    @Test
    void test_oneClass() {
        SchemaGenerator generator = new SchemaGenerator();
        PlainMapNode schemaDoc = generator.generate(TestClass.class);
        assertTrue(schemaDoc != null);
    }

    // TODO: This should work but it doesn't
    // Is it just failing when the class is inside another one?
    // No, test_oneClass proves that works.
    // The problem here is a class that refers to another class that has no
    // schema in the system yet.
    @Test
    void test_classReferingToOtherClass1() {
        Schema schema = new SchemaResolver().getSchemaForClass(OuterTestClass.class);
        PlainMapNode decompiled = new SchemaDecompiler().decompile(schema);
        String json = new JsonConverter().toString(decompiled);
        assertNotNull(json);
    }

    // To prove this, if we cause TestClass's schema to be generated first, the
    // code in the failing test will then wwork. This test does just that:
    @Test
    void test_classReferingToOtherClass2() {
        SchemaGenerator generator = new SchemaGenerator();
        SchemaCompiler compiler = new SchemaCompiler();

        PlainMapNode testClassSchemaRoot = generator.generate(TestClass.class);
        compiler.compile(testClassSchemaRoot);

        PlainMapNode outerTestClassSchemaRoot = generator.generate(OuterTestClass.class);
        Schema outerTestClassSchema = compiler.compile(outerTestClassSchemaRoot);

        listCachedSchemas();

        PlainMapNode decompiled = new SchemaDecompiler().decompile(outerTestClassSchema);
        String json = new JsonConverter().toString(decompiled);
        assertNotNull(json);
    }

    // -----------------------------------------------------------------------

    // This is also broken in a similar way to test_classReferingToOtherClass1.
    // It should have worked like test_classReferingToOtherClass2.
    // getSchemaForClass *should* have picked testClassSchema up from the cache.
    // Why did it fail?
    @Test
    void test_classReferingToOtherClass3() {
        SchemaGenerator generator = new SchemaGenerator();
        SchemaCompiler compiler = new SchemaCompiler();

        PlainMapNode testClassSchemaRoot = generator.generate(TestClass.class);
        compiler.compile(testClassSchemaRoot);

        Schema outerTestClassSchema = new SchemaResolver().getSchemaForClass(OuterTestClass.class);

        listCachedSchemas();

        PlainMapNode decompiled = new SchemaDecompiler().decompile(outerTestClassSchema);
        String json = new JsonConverter().toString(decompiled);
        assertNotNull(json);
    }



}
