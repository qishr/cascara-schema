package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;

public interface ValidationRule {
    /// Validates a node and adds errors to the result if necessary.
    boolean validate(AstNode node, String path, Reporter reporter);

    default boolean validateValue(Object value, String path, Reporter reporter) {
        // Default implementation can be empty or a bridge
        return true;
    }
}
