package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

import java.util.Collections;
import java.util.List;

public class EnumRule extends AbstractValidationRule implements ValidationRule {
    private final List<String> allowedValues;

    public EnumRule(List<String> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (node instanceof ScalarAstNode scalar) {
            return validateValue(scalar.getPrimitive(), path, reporter);
        }
        return true;
    }

    @Override
    public boolean validateValue(Object value, String path, Reporter reporter) {
        if (value == null) return true;
        String valStr = value.toString();
        if (!allowedValues.contains(valStr)) {
            error(path, LocatableException.UNKNOWN_COORD, LocatableException.UNKNOWN_COORD, reporter, SchemaDiagnosticCode.NOT_ALLOWED_IN_LIST, valStr, allowedValues);
            return false;
        }
        return true;
    }

    public List<String> getAllowedValues() {
        return Collections.unmodifiableList(allowedValues);
    }
}