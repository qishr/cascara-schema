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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.lang.reference.ReferenceMapNode;
import io.github.qishr.cascara.common.lang.reference.ReferenceScalarNode;
import io.github.qishr.cascara.common.lang.reference.ReferenceSequenceNode;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.structure.ObjectSchemaNode;

public class SchemaCompilerTests {

    private SchemaResolver resolver = new SchemaResolver();
    private SchemaCompiler compiler = new SchemaCompiler(resolver);



    @Test
    void shouldCaptureUnevaluatedPropertiesAndTypedHints() {
        // 1. Setup 'parent' with x-tracked: true (Boolean)
        ReferenceMapNode parentProps = new ReferenceMapNode();
        parentProps.put("status", createScalarProperty("string", "x-tracked", true));

        ReferenceMapNode parentDef = new ReferenceMapNode();
        parentDef.put("type", new ReferenceScalarNode("object"));
        parentDef.put("properties", parentProps);

        // 2. Setup 'child' with allOf: [parent] and unevaluatedProperties: false
        ReferenceSequenceNode allOf = new ReferenceSequenceNode();
        ReferenceMapNode refNode = new ReferenceMapNode();
        refNode.put("$ref", new ReferenceScalarNode("#/definitions/parent"));
        allOf.add(refNode);

        ReferenceMapNode childDef = new ReferenceMapNode();
        childDef.put("type", new ReferenceScalarNode("object"));
        childDef.put("allOf", allOf);
        childDef.put("unevaluatedProperties", new ReferenceScalarNode(false));

        ReferenceMapNode defs = new ReferenceMapNode();
        defs.put("parent", parentDef);
        defs.put("child", childDef);

        ReferenceMapNode root = new ReferenceMapNode();
        root.put("$id", new ReferenceScalarNode("cascara://core/schema-service/dynamic/cascara.schema/compiler-unevaluated-test"));
        root.put("definitions", defs);

        Schema compiled = compiler.compile(root);

        ObjectSchemaNode childNode = (ObjectSchemaNode) compiled.getRoot()
                .getDefinition("child");

        assertFalse(childNode.areUnevaluatedPropertiesAllowed());

        // Ensure flattening worked: status should be in child properties
        assertTrue(childNode.getProperties().containsKey("status"));

        Object hint = childNode.getProperties().get("status").getExtension("x-tracked");
        assertTrue(hint instanceof Boolean);
        assertEquals(true, hint);
    }

    @Test
    void shouldFlattenAllOfInheritance() {
        // 1. Create Parent
        ReferenceMapNode parentProps = new ReferenceMapNode();
        parentProps.put("base_field", createScalarProperty("string", "x-tracked", true));

        ReferenceMapNode parentDef = new ReferenceMapNode();
        parentDef.put("type", new ReferenceScalarNode("object"));
        parentDef.put("properties", parentProps);

        // 2. Create Child using allOf
        ReferenceSequenceNode allOf = new ReferenceSequenceNode();
        ReferenceMapNode refNode = new ReferenceMapNode();
        refNode.put("$ref", new ReferenceScalarNode("#/definitions/parent"));
        allOf.add(refNode);

        ReferenceMapNode childDef = new ReferenceMapNode();
        childDef.put("allOf", allOf);

        ReferenceMapNode defs = new ReferenceMapNode();
        defs.put("parent", parentDef);
        defs.put("child", childDef);

        ReferenceMapNode root = new ReferenceMapNode();
        root.put("$id", new ReferenceScalarNode("cascara://core/schema-service/dynamic/cascara.schema/compiler-flatten-test"));
        root.put("definitions", defs);

        Schema compiled = compiler.compile(root);

        ObjectSchemaNode childNode = (ObjectSchemaNode) compiled.getRoot()
                .getDefinition("child");

        assertTrue(childNode.getProperties().containsKey("base_field"));
        Object hint = childNode.getProperties().get("base_field").getExtension("x-tracked");
        assertTrue(hint instanceof Boolean);
    }

    @Test
    void shouldRespectAdditionalPropertiesFalse() {
        ReferenceMapNode root = new ReferenceMapNode();
        root.put("$id", new ReferenceScalarNode("cascara://core/schema-service/dynamic/cascara.schema/compiler-additional-properties-test"));
        root.put("type", new ReferenceScalarNode("object"));
        root.put("additionalProperties", new ReferenceScalarNode(false));

        Schema compiled = compiler.compile(root);
        ObjectSchemaNode rootNode = (ObjectSchemaNode) compiled.getRoot();

        assertFalse(rootNode.areAdditionalPropertiesAllowed());
    }

    private ReferenceMapNode createScalarProperty(String type, String hintKey, Object hintVal) {
        ReferenceMapNode prop = new ReferenceMapNode();
        prop.put("type", new ReferenceScalarNode(type));
        prop.put(hintKey, new ReferenceScalarNode(hintVal));
        return prop;
    }
}