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


package io.github.qishr.cascara.schema.internal;

import java.net.URI;

import io.github.qishr.cascara.common.lang.annotation.Nullable;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.MapAstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.SchemaKeyword;
import io.github.qishr.cascara.schema.exception.SchemaException;
import io.github.qishr.cascara.schema.util.SchemaResolver;
import io.github.qishr.cascara.schema.util.Schemas;

public class SchemaUtils {
    /// Checks for top-level $schema key
    @Nullable
    public static Schema scanForSchema(AstNode root) {
        return scanForSchema(root, null);
    }

    /// Checks for top-level $schema key
    @Nullable
    public static Schema scanForSchema(AstNode root, SchemaResolver resolver) {
        if (resolver == null) {
            resolver = Schemas.getResolver();
        }
        if (root instanceof MapAstNode map) {
            AstNode schemaValue = map.get(SchemaKeyword.SCHEMA.asString());
            if (schemaValue instanceof ScalarAstNode scalar) {
                URI uri = URI.create(scalar.asString());
                return resolver.getSchema(uri);
            }
        }
        return null;
    }

    public static AstNode resolveFragment(AstNode root, String fragment) throws SchemaException {
        if (fragment == null || fragment.isEmpty() || fragment.equals("#") || fragment.equals("/")) {
            return root;
        }

        // Strip leading '#' if present
        String path = fragment.startsWith("#") ? fragment.substring(1) : fragment;

        // Split by '/', then filter out empty segments (like the leading one in /definitions)
        String[] parts = path.split("/");
        AstNode currentNode = root;

        for (String part : parts) {
            if (part.isEmpty()) continue;

            if (currentNode instanceof MapAstNode map) {
                // Using the key exactly as it appears in the segment (e.g., "$defs")
                currentNode = (AstNode) map.get(part);
            } else {
                return null;
            }

            if (currentNode == null) return null;
        }
        return currentNode;
    }
}
