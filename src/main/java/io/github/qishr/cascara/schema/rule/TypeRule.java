// # License & Terms
//
// This file is part of **Cascara**.
//
// **Cascara** is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
//
// ---
//
// ## Special Runtime Exception
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules,
// and to copy and distribute the resulting executable under terms of your
// choice, provided that you also meet, for each linked independent module,
// the terms and conditions of the license of that module.
//
// An independent module is a module which is not derived from or based on
// this library. If you modify this library, you may extend this exception
// to your version of the library, but you are not obligated to do so. If
// you do not wish to do so, delete this exception statement from your
// version.


package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.common.lang.type.PrimitiveType;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

public class TypeRule extends AbstractValidationRule implements ValidationRule {
    private final PrimitiveType expectedType;

    public TypeRule(PrimitiveType expectedType) {
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