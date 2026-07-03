package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

public class MinValueRule extends AbstractValidationRule implements ValidationRule {
    private final double min;

    public MinValueRule(double min) {
        this.min = min;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (node instanceof ScalarAstNode scalar) {
            return validateValue(scalar.getPrimitive(), path, node.getStartLine(), node.getStartColumn(), reporter);
        }
        return true;
    }

    @Override
    public boolean validateValue(Object value, String path, Reporter reporter) {
        return validateValue(value, path, LocatableException.UNKNOWN_COORD, LocatableException.UNKNOWN_COORD, reporter);
    }

    private boolean validateValue(Object value, String path, int line, int column, Reporter reporter) {
        if (value instanceof Number num) {
            if (num.doubleValue() < min) {
                error(path, line, column, reporter, SchemaDiagnosticCode.LESS_THAN_MIN_VALUE, num, min);
                return false;
            }
        }
        return true;
    }

    public double getMin() {
        return min;
    }
}