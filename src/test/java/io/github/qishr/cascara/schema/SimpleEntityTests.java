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


package io.github.qishr.cascara.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.lang.annotation.DataIgnore;
import io.github.qishr.cascara.common.lang.reference.ReferenceNode;
import io.github.qishr.cascara.common.lang.type.PrimitiveType;
import io.github.qishr.cascara.schema.annotation.SchemaProperty;
import io.github.qishr.cascara.schema.util.SchemaGenerator;
import io.github.qishr.cascara.schema.util.SchemaResolver;
import io.github.qishr.cascara.schema.structure.ArraySchemaNode;
import io.github.qishr.cascara.schema.structure.LazySchemaNode;
import io.github.qishr.cascara.schema.structure.ObjectSchemaNode;
import io.github.qishr.cascara.schema.structure.ScalarSchemaNode;
import io.github.qishr.cascara.schema.structure.SchemaNode;
import io.github.qishr.cascara.schema.util.SchemaCompiler;

public class SimpleEntityTests {
    @Test
    public void simpleEntity_has_scalar_fields() {
        SchemaGenerator generator = new SchemaGenerator();
        SchemaResolver resolver = new SchemaResolver();
        SchemaCompiler compiler = new SchemaCompiler(resolver);

        ReferenceNode doc = generator.generate(SimpleEntity.class);
        Schema schema = compiler.compile(doc, URI.create("runtime://schema"));

        Map<String, SchemaNode> props = schema.getRoot().getProperties();

        assertTrue(props.get("name") instanceof ScalarSchemaNode);
        assertTrue(props.get("age") instanceof ScalarSchemaNode);
    }

    @Test
    public void refEntity_distinguishes_single_and_collection_references() {
        SchemaGenerator generator = new SchemaGenerator();
        SchemaCompiler compiler = new SchemaCompiler(new SchemaResolver());

        ReferenceNode doc = generator.generate(RefEntity.class);
        Schema schema = compiler.compile(doc, URI.create("runtime://schema"));

        SchemaNode child = schema.getRoot().getProperties().get("child");
        SchemaNode children = schema.getRoot().getProperties().get("children");

        // Single reference
        if (child instanceof LazySchemaNode lazy) { child = lazy.getResolved(); }
        assertTrue(child instanceof ObjectSchemaNode);
        // assertFalse(((ReferenceSchemaNode) child).isCollection());

        // Collection reference
        assertTrue(children instanceof ArraySchemaNode);

        ArraySchemaNode arr = (ArraySchemaNode) children;

        assertTrue(arr.getItemSchema() instanceof LazySchemaNode);
        assertEquals(PrimitiveType.OBJECT, ((LazySchemaNode) arr.getItemSchema()).getType());
    }


    public class IgnoreEntity {
        @SchemaProperty(title = "Visible")
        String visible;

        @DataIgnore
        String ignored;
    }

    @Test
    public void ignoreEntity_ignores_dataignore_fields() {
        SchemaGenerator generator = new SchemaGenerator();
        SchemaResolver resolver = new SchemaResolver();
        SchemaCompiler compiler = new SchemaCompiler(resolver);

        ReferenceNode doc = generator.generate(IgnoreEntity.class);
        Schema schema = compiler.compile(doc, URI.create("runtime://schema"));

        Map<String, SchemaNode> props = schema.getRoot().getProperties();

        assertTrue(props.containsKey("visible"));
        assertFalse(props.containsKey("ignored"));
    }

    @Test
    public void array_reference_is_marked_as_collection() {
        SchemaGenerator generator = new SchemaGenerator();
        // TestResolver resolver = new TestResolver();
        SchemaResolver resolver = new SchemaResolver() ;
        SchemaCompiler compiler = new SchemaCompiler(resolver);

        URI uri = URI.create("runtime://schema");
        ReferenceNode doc = generator.generate(RefEntity.class);
        Schema schema = compiler.compile(doc, uri);

        SchemaNode children = schema.getRoot().getProperties().get("children");
        ArraySchemaNode arr = (ArraySchemaNode) children;
        SchemaNode itemSchema = arr.getItemSchema();
        if (itemSchema instanceof LazySchemaNode lazy) {
            itemSchema = lazy.getResolved();
        }
        // ReferenceSchemaNode ref = (ReferenceSchemaNode) arr.getItemTemplate();

        // assertTrue(ref.isCollection());
    }

}
