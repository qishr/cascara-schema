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

import io.github.qishr.cascara.common.diagnostic.Diagnostic;
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
        return validateValue(value, path, Diagnostic.UNKNOWN_COORD, Diagnostic.UNKNOWN_COORD, reporter);
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