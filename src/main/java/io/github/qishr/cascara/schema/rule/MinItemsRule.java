package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.SequenceAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

import java.util.Collection;

public class MinItemsRule extends AbstractValidationRule implements ValidationRule {
    private final int minItems;

    public MinItemsRule(int minItems) {
        this.minItems = minItems;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (node instanceof SequenceAstNode sequence) {
            return validateValue(sequence.getChildren(), path, node.getStartLine(), node.getStartColumn(), reporter);
        } else if (minItems > 0) {
            error(path, node, reporter, SchemaDiagnosticCode.LESS_THAN_MIN_ITEMS, minItems);
            return false;
        }
        return true;
    }

    @Override
    public boolean validateValue(Object value, String path, Reporter reporter) {
        return validateValue(value, path, LocatableException.UNKNOWN_COORD, LocatableException.UNKNOWN_COORD, reporter);
    }

    private boolean validateValue(Object value, String path, int line, int column, Reporter reporter) {
        int currentSize = 0;

        if (value instanceof Collection<?> collection) {
            currentSize = collection.size();
        } else if (value instanceof Iterable<?> iterable) {
            for (Object _ : iterable) currentSize++;
        } else if (value != null) {
            // It's not a collection/iterable, so it's not got array items
            return false;
        }

        if (currentSize < minItems) {
            error(path, line, column, reporter, SchemaDiagnosticCode.LESS_THAN_MIN_ITEMS_2, currentSize, minItems);
            return false;
        }
        return true;
    }

    public int getMinItems() {
        return minItems;
    }
}