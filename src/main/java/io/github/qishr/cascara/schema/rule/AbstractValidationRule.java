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

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;
import io.github.qishr.cascara.schema.exception.ValidationException;

public abstract class AbstractValidationRule {

    protected AbstractValidationRule() {
        // Nothing to see here
    }

    protected void error(String fragmentPath, AstNode node, Reporter reporter, SchemaDiagnosticCode code, Object... details) {
        if (reporter == null || !reporter.collectsProblems()) {
            throw new ValidationException(fragmentPath, node, code, details);
        }
        URI uri = URI.create(formatFragment(fragmentPath));
        reporter.errorAt(uri, node.getStartLine(), node.getStartColumn(), code, details);
    }

    protected void error(String fragmentPath, int line, int column, Reporter reporter, SchemaDiagnosticCode code, Object... details) {
        if (reporter == null || !reporter.collectsProblems()) {
            throw new ValidationException(fragmentPath, line, column, code, details);
        }
        URI uri = URI.create(formatFragment(fragmentPath));
        reporter.errorAt(uri, line, column, code, details);
    }

    private String formatFragment(String path) {
        String fragment = path == null ? "" : path.startsWith("#/") ? path : path.startsWith("/") ? "#" + path : "#/" + path;
        return fragment;
    }
}
