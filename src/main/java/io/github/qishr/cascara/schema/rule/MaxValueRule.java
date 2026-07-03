package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

public class MaxValueRule extends AbstractValidationRule implements ValidationRule {
    private final double max;

    public MaxValueRule(double max) {
        this.max = max;
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
            if (num.doubleValue() > max) {
                error(path, line, column, reporter, SchemaDiagnosticCode.MORE_THAN_MAX_VALUE, num, max);
                return false;
            }
        }
        return true;
    }

    public double getMax() {
        return max;
    }
}