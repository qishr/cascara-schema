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

import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.reference.ReferenceMapNode;
import io.github.qishr.cascara.lang.json.processor.JsonConverter;
import io.github.qishr.cascara.schema.annotation.SchemaProperty;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassSchemaGeneratorTest {

    static class SimpleEntity {
        @SchemaProperty
        public String title;

        @SchemaProperty
        public int count;

        @SchemaProperty
        public boolean active;

        @SchemaProperty
        public double score;
    }


    static class NestedEntity {
        @SchemaProperty
        public String name;

        @SchemaProperty
        public SimpleEntity child;
    }


    private final SchemaGenerator generator = new SchemaGenerator();

    @Test
    void rootHasCorrectNameAndType() {
        var root = (ReferenceMapNode) generator.generate(SimpleEntity.class);

        // assertEquals("SimpleEntity", root.getString("name"));
        assertEquals("object", root.getString("type"));
    }

    @Test
    void generatesCorrectScalarProperties() {
        var root = (ReferenceMapNode) generator.generate(SimpleEntity.class);
        var props = (ReferenceMapNode) root.get("properties");

        assertEquals("string", ((ReferenceMapNode) props.get("title")).getString("type"));
        assertEquals("integer", ((ReferenceMapNode) props.get("count")).getString("type"));
        assertEquals("boolean", ((ReferenceMapNode) props.get("active")).getString("type"));
        assertEquals("number", ((ReferenceMapNode) props.get("score")).getString("type"));
    }

    @Test
    void generatesNestedObjectProperty() {
        var root = (ReferenceMapNode) generator.generate(NestedEntity.class);
        var props = (ReferenceMapNode) root.get("properties");

        var child = (ReferenceMapNode) props.get("child");
        assertNotNull(child);

        // Nested objects become references
        assertEquals("#/$defs/SimpleEntity", child.getString("$ref"));
        // assertEquals("SimpleEntity", child.getString("target"));
    }


    // @Test
    // void schemaIsStableAcrossRuns() {
    //     var doc1 = generator.generate(SimpleEntity.class);
    //     var doc2 = generator.generate(SimpleEntity.class);

        // class NoopResolver implements SchemaResolver {
        //     @Override public StructuredDocument resolve(URI uri) {
        //          return null; // no external schemas in tests
        //     }
        // }

    //     var compiler = new SchemaCompiler();
    //     var schema1 = compiler.compile(doc1, URI.create("runtime://schema1"));
    //     var schema2 = compiler.compile(doc2, URI.create("runtime://schema2"));


    //     // Object plain1 = toPlain.toPlain(doc1.getRoot());
    //     // Object plain2 = toPlain.toPlain(doc2.getRoot());
    //     // assertEquals(plain1, plain2);

    //     // // assertEquals(doc1.toString(), doc2.toString());
    //     // assertEquals(doc1.getRoot(), doc2.getRoot());

    // }

    @Test
    void schemaIsStableAcrossRuns() {
        var doc1 = generator.generate(SimpleEntity.class);
        var doc2 = generator.generate(SimpleEntity.class);

        SchemaResolver resolver = new SchemaResolver();
        SchemaCompiler compiler = new SchemaCompiler(resolver);

        var schema1 = compiler.compile(doc1, URI.create("runtime://schema1"));
        var schema2 = compiler.compile(doc2, URI.create("runtime://schema1"));

        JsonConverter converter = new JsonConverter();
        SchemaDecompiler decompiler = new SchemaDecompiler();

        AstNode doc1a = decompiler.decompile(schema1);
        String json1 = converter.toText(doc1a);

        AstNode doc2a = decompiler.decompile(schema2);
        String json2 = converter.toText(doc2a);

        assertEquals(json1, json2);
    }

    //
    //
    //

    // class NoopResolver implements SchemaResolver {
    //     @Override
    //     public AstNode resolve(String ref, SchemaNode relativeTo) {
    //         return null; // no external schemas in tests
    //     }

    //     @Override
    //     public AstNode resolveInternal(String fragment, AstNode root) {
    //         return null; // no external schemas in tests
    //     }
    // }

    @Test
    void scalarFieldsGenerateScalarTypes() {
        class Simple {
            @SchemaProperty public String name;
            @SchemaProperty public int age;
            @SchemaProperty public boolean active;
        }

        var root = (ReferenceMapNode) generator.generate(Simple.class);
        var props = (ReferenceMapNode) root.get("properties");

        assertEquals("string", ((ReferenceMapNode) props.get("name")).getString("type"));
        assertEquals("integer", ((ReferenceMapNode) props.get("age")).getString("type"));
        assertEquals("boolean", ((ReferenceMapNode) props.get("active")).getString("type"));
    }

    @Test
    void nestedObjectBecomesReference() {
        class Address {
            @SchemaProperty public String street;
        }
        class Person {
            @SchemaProperty public Address address;
        }

        var root = (ReferenceMapNode) generator.generate(Person.class);
        var props = (ReferenceMapNode) root.get("properties");
        var address = (ReferenceMapNode) props.get("address");

        assertEquals("#/$defs/Address", address.getString("$ref"));
        // assertEquals("Address", address.getString("target"));
    }

    @Test
    void listOfObjectsBecomesArrayOfReferences() {
        class Tag {
            @SchemaProperty public String name;
        }
        class Entry {
            @SchemaProperty public List<Tag> tags;
        }

        var root = (ReferenceMapNode) generator.generate(Entry.class);
        var props = (ReferenceMapNode) root.get("properties");
        var tags = (ReferenceMapNode) props.get("tags");

        assertEquals("array", tags.getString("type"));

        var items = (ReferenceMapNode) tags.get("items");
        assertEquals("#/$defs/Tag", items.getString("$ref"));
        // assertEquals("Tag", items.getString("target"));
    }

    @Test
    void mixedFieldsProduceCorrectSchema() {
        class Address {
            @SchemaProperty public String street;
        }
        class Tag {
            @SchemaProperty public String name;
        }
        class Person {
            @SchemaProperty public String name;
            @SchemaProperty public Address address;
            @SchemaProperty public List<Tag> tags;
        }

        var root = (ReferenceMapNode) generator.generate(Person.class);
        var props = (ReferenceMapNode) root.get("properties");

        // scalar
        assertEquals("string", ((ReferenceMapNode) props.get("name")).getString("type"));

        // reference
        var address = (ReferenceMapNode) props.get("address");
        assertEquals("#/$defs/Address", address.getString("$ref"));
        // assertEquals("Address", address.getString("target"));

        // array of references
        var tags = (ReferenceMapNode) props.get("tags");
        assertEquals("array", tags.getString("type"));
        var items = (ReferenceMapNode) tags.get("items");
        assertEquals("#/$defs/Tag", items.getString("$ref"));
        // assertEquals("Tag", items.getString("target"));
    }

    @Test
    void nestedObjectsDoNotGeneratePropertiesBlock() {
        class Address {
            @SchemaProperty public String street;
        }
        class Person {
            @SchemaProperty public Address address;
        }

        var doc = generator.generate(Person.class);
        var props = (ReferenceMapNode) ((ReferenceMapNode) doc).get("properties");
        var address = (ReferenceMapNode) props.get("address");

        assertFalse(address.containsKey("properties"));
    }
}
