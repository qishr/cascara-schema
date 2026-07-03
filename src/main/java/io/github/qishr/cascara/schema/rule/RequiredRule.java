package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.MapAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

import java.util.List;
import java.util.Map;

public class RequiredRule extends AbstractValidationRule implements ValidationRule {
    private final List<String> requiredKeys;

    public RequiredRule(List<String> requiredKeys) {
        this.requiredKeys = requiredKeys;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (node instanceof MapAstNode mapNode) {
            return validateValue(mapNode, path, node.getStartLine(), node.getStartColumn(), reporter);
        }
        return true;
    }

    @Override
    public boolean validateValue(Object value, String path, Reporter reporter) {
        return validateValue(value, path, LocatableException.UNKNOWN_COORD, LocatableException.UNKNOWN_COORD, reporter);
    }

    private boolean validateValue(Object value, String path, int line, int column, Reporter reporter) {
        boolean valid = true;
        // In the editor, 'value' is expected to be the Map/Object containing the keys
        if (value instanceof Map<?, ?> map) {
            for (String key : requiredKeys) {
                if (!map.containsKey(key)) {
                    error(path, line, column, reporter, SchemaDiagnosticCode.MISSING_REQUIRED_PROPERTY, key);
                    valid = false;
                }
            }
        } else if (value instanceof MapAstNode mapNode) {
            // Helper for the bridge
            for (String key : requiredKeys) {
                if (mapNode.get(key) == null) {
                    error(path, line, column, reporter, SchemaDiagnosticCode.MISSING_REQUIRED_PROPERTY, key);
                    valid = false;
                }
            }
        }
        return valid;
    }

    public List<String> getRequiredKeys() {
        return requiredKeys;
    }
}