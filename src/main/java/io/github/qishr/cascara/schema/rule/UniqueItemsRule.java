package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.common.lang.ast.SequenceAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UniqueItemsRule extends AbstractValidationRule implements ValidationRule {
    private final boolean active;

    public UniqueItemsRule(boolean active) {
        this.active = active;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (!active || !(node instanceof SequenceAstNode sequence)) return true;
        boolean valid = true;

        // Bridge to the common logic, but maintaining individual item reporting
        // which requires a loop here to capture specific line/column info per duplicate.
        Set<Object> seen = new HashSet<>();
        int i = 0;
        for (AstNode item : sequence.getChildren()) {
            if (item instanceof ScalarAstNode scalar) {
                Object val = scalar.getPrimitive();
                if (!seen.add(val)) {
                    error(path + "[" + i + "]", item, reporter, SchemaDiagnosticCode.DUPLICATE_ITEM, val);
                    valid = false;
                }
            }
            i++;
        }
        return valid;
    }

    @Override
    public boolean validateValue(Object value, String path, Reporter reporter) {
       return validateValue(value, path, LocatableException.UNKNOWN_COORD, LocatableException.UNKNOWN_COORD, reporter);
    }

    private boolean validateValue(Object value, String path, int line, int col, Reporter reporter) {
        if (!active || value == null) return true;
        boolean valid = true;

        if (value instanceof Collection<?> collection) {
            Set<Object> seen = new HashSet<>();
            for (Object item : collection) {
                if (!seen.add(item)) {
                    error(path, line, col, reporter, SchemaDiagnosticCode.DUPLICATE_ITEM, item);
                    valid = false;
                }
            }
        }
        return valid;
    }

    public boolean isActive() {
        return active;
    }
}