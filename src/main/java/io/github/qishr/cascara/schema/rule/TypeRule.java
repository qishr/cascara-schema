package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.schema.SchemaType;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

public class TypeRule extends AbstractValidationRule implements ValidationRule {
    private final SchemaType expectedType;

    public TypeRule(SchemaType expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (node instanceof ScalarAstNode scalar) {
            Object value = scalar.getPrimitive();
            if (value == null) return true; // Let RequiredRule handle "Missing" vs "Wrong Type"
            boolean valid = switch (expectedType) {
                case STRING -> value instanceof String;
                case INTEGER -> value instanceof Integer || value instanceof Long;
                case NUMBER -> value instanceof Number;
                case BOOLEAN -> value instanceof Boolean;
                default -> true;
            };

            if (!valid) {
                error(path, node, reporter, SchemaDiagnosticCode.EXPECTED_TYPE, expectedType, value.getClass().getSimpleName());
                return false;
            }
        }
        return true; // TODO: Is this handled elsewhere?
    }

    public boolean validateValue(Object value, String path, int line, int column, Reporter reporter) {
        if (value == null) return true;

        boolean valid = switch (expectedType) {
            case STRING -> value instanceof String;
            case INTEGER -> isInteger(value);
            case NUMBER -> value instanceof Number || isNumeric(value);
            case BOOLEAN -> value instanceof Boolean || isBooleanString(value);
            default -> true;
        };

        if (!valid) {
            error(path, line, column, reporter, SchemaDiagnosticCode.EXPECTED_TYPE, expectedType, value.getClass().getSimpleName());
        }
        return valid;
    }

    // Helper methods to handle String inputs from the UI TextFields
    private boolean isInteger(Object v) {
        if (v instanceof Integer || v instanceof Long) return true;
        try { Long.parseLong(v.toString()); return true; } catch (Exception e) { return false; }
    }

    private boolean isNumeric(Object v) {
        try { Double.parseDouble(v.toString()); return true; } catch (Exception e) { return false; }
    }

    private boolean isBooleanString(Object v) {
        String s = v.toString().toLowerCase();
        return s.equals("true") || s.equals("false");
    }
}