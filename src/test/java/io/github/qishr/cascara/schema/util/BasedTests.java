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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.reference.ReferenceMapNode;
import io.github.qishr.cascara.common.lang.reference.ReferenceScalarNode;
import io.github.qishr.cascara.schema.structure.LazySchemaNode;
import io.github.qishr.cascara.schema.structure.ObjectSchemaNode;
import io.github.qishr.cascara.schema.structure.ScalarSchemaNode;
import io.github.qishr.cascara.schema.structure.SchemaNode;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.common.lang.type.PrimitiveType;
import io.github.qishr.cascara.schema.exception.SchemaException;

public class BasedTests extends SchemaIntegrationTestBase {

    @Test
    void testLazyNodeTriggeringResolution() throws SchemaException {
        SchemaResolver mockResolver = mock(SchemaResolver.class);
        AstNode mockAst = mock(AstNode.class);
        URI baseUri = URI.create("https://myserver.com/schema.json");
        SchemaNode mockMeta = mock(SchemaNode.class);

        // 1. Remove "common", add mockMeta
        SchemaNode expectedNode = new ScalarSchemaNode(PrimitiveType.STRING, mockMeta);

        // 2. Add mockMeta to the end of LazySchemaNode constructor
        LazySchemaNode lazy = new LazySchemaNode("common.json", mockResolver, null, baseUri, mockAst, null, mockMeta);

        // 3. Stub the 3-parameter version (same as your previous update)
        when(mockResolver.resolve(eq("common.json"), eq(lazy), any(DynamicScope.class)))
            .thenReturn(expectedNode);

        SchemaNode resolved = lazy.getResolved();

        verify(mockResolver).resolve(eq("common.json"), eq(lazy), any(DynamicScope.class));
        assertEquals(expectedNode, resolved);
        assertEquals(expectedNode.getOriginAst(), lazy.getOriginAst());
    }

    @Test
    void testInternalFragmentResolution() {
        SchemaResolver localResolver = new SchemaResolver();
        SchemaCompiler compiler = new SchemaCompiler(localResolver);

        ReferenceMapNode addrAst = new ReferenceMapNode();
        addrAst.put("type", new ReferenceScalarNode("string"));

        ReferenceMapNode defsAst = new ReferenceMapNode();
        defsAst.put("address", addrAst);

        URI uri = URI.create("file:///schema.json");
        ReferenceMapNode rootAst = new ReferenceMapNode();
        rootAst.put("$id", new ReferenceScalarNode(uri.toString()));
        rootAst.put("definitions", defsAst);

        Schema compiled = compiler.compile(rootAst, uri);
        ObjectSchemaNode rootSchema = (ObjectSchemaNode) compiled.getRoot();

        SchemaNode result = localResolver.resolve("#/definitions/address", rootSchema);

        assertTrue(result instanceof ScalarSchemaNode, "Result should be the compiled scalar node");
        // Note: If you removed the name field entirely, you should assert on the type or a title instead.
        assertEquals(PrimitiveType.STRING, result.getType());
    }

    // TODO: This has been temporarily removed since CascaraSchemaResolver no longer
    // has a content loader that can be set.

    // @Test
    // void testRemoteToRemoteResolution() throws IOException {
    //     // 1. Constructor updated: Removed "anchor", added null for meta
    //     ObjectSchemaNode anchorNode = new ObjectSchemaNode(null);
    //     anchorNode.setOriginUri(URI.create("https://my-api.com/schemas/user.json"));

    //     String ref = "common/address.json";
    //     URI expectedUri = URI.create("https://my-api.com/schemas/common/address.json");

    //     when(mockLoader.getContent(eq(expectedUri)))
    //         .thenReturn(new ResourceContent("{}", null));

    //     // Execute
    //     resolver.resolve(ref, anchorNode);

    //     // Verify
    //     verify(mockLoader).getContent(expectedUri);
    // }

    // @Test
    // void testJsonSchemaOrgDraftResolution() throws Exception {
    //     // 1. Mocking the files
    //     mockRemoteFile("https://json-schema.org/draft/2020-12/schema",
    //         "{ \"$ref\": \"meta/core\" }");

    //     mockRemoteFile("https://json-schema.org/draft/2020-12/meta/core",
    //         "{ \"title\": \"Core Meta-Schema\" }");

    //     // 2. Constructor updated: Removed "root", added null for meta
    //     ObjectSchemaNode anchor = new ObjectSchemaNode(null);
    //     anchor.setOriginUri(URI.create("https://json-schema.org/draft/2020-12/schema"));

    //     // 3. Resolve (The return type is SchemaNode, not AstNode)
    //     SchemaNode result = resolver.resolve("meta/core", anchor);

    //     // 4. Assertions
    //     assertNotNull(result);
    //     assertEquals("Core vocabulary meta-schema", result.getTitle());
    // }

    // @Test
    // void verifyKeyPurity() throws Exception {
    //     mockRemoteFile("https://test.io/purity.json", "{ \"properties\": {} }");

    //     // 1. Constructor updated: Removed "anchor", added null for meta
    //     ObjectSchemaNode anchor = new ObjectSchemaNode(null);
    //     anchor.setOriginUri(URI.create("https://test.io/anchor.json"));

    //     // 2. Resolve
    //     SchemaNode result = resolver.resolve("purity.json", anchor);

    //     // 3. Extract AST for purity check
    //     AstNode rawAst = result.getOriginAst();

    //     if (rawAst instanceof MapAstNode map) {
    //         // Get the actual key object from the AST
    //         @SuppressWarnings("rawtypes")
    //         var entry = (MapEntryAstNode) map.getEntries().iterator().next();
    //         Object firstKey = entry.getKey();

    //         assertTrue(map.containsKey("properties"),
    //             "The AST map should contain the String 'properties'");

    //         // Verify the internal representation is a JsonScalarNode (from your JsonAstParser)
    //         assertTrue(firstKey instanceof JsonScalarNode,
    //             "Key should be a JsonScalarNode, but was: " + firstKey.getClass().getName());
    //     }
    // }
}