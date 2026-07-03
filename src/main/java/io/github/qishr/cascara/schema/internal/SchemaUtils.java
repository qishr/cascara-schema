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
