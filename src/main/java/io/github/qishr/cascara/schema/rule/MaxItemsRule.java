package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.SequenceAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

import java.util.Collection;

public class MaxItemsRule extends AbstractValidationRule implements ValidationRule {
    private final int maxItems;

    public MaxItemsRule(int maxItems) {
        this.maxItems = maxItems;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (node instanceof SequenceAstNode sequence) {
            return validateValue(sequence.getChildren(), path, node.getStartLine(), node.getStartColumn(), reporter);
        }
        return true;
    }

    @Override
    public boolean validateValue(Object value, String path, Reporter reporter) {
        return validateValue(value, path, LocatableException.UNKNOWN_COORD, LocatableException.UNKNOWN_COORD, reporter);
    }

    private boolean validateValue(Object value, String path, int line, int column, Reporter reporter) {
        int currentSize = -1;

        if (value instanceof Collection<?> collection) {
            currentSize = collection.size();
        } else if (value instanceof Iterable<?> iterable) {
            // Fallback for custom iterables if necessary
            int count = 0;
            for (Object _ : iterable) count++;
            currentSize = count;
        }

        if (currentSize > maxItems) {
            error(path, line, column, reporter, SchemaDiagnosticCode.MORE_THAN_MAX_ITEMS, currentSize, maxItems);
            return false;
        }
        return true;
    }

    public int getMaxItems() {
        return maxItems;
    }
}