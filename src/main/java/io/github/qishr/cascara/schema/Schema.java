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


package io.github.qishr.cascara.schema;

import java.net.URI;
import java.util.Collection;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.schema.exception.ValidationException;
import io.github.qishr.cascara.schema.structure.SchemaNode;
import io.github.qishr.cascara.schema.util.SchemaResolver;

public interface Schema {

    Collection<SchemaNode> getProperties();
    Collection<SchemaNode> getDefinitions();
    SchemaNode getRoot();
    URI getOriginUri();
    URI getId();
    URI getSchemaUri();
    SchemaNode getDefinition(String name);
    SchemaNode getProperty(String name);

    /// Validates an AST against this schema.
    /// @param data an `AstNode` representing a document or part of a
    ///             structured document (such as JSON or YAML) to validate.
    /// @throws ValidationException if the AST is not valid.
    void validate(AstNode root);

    /// Validates an AST against this schema.
    /// @param data an `AstNode` representing a document or part of a
    ///             structured document (such as JSON or YAML) to validate.
    /// @return true on success, false on failure.
    /// @param reporter a reporter for collecting problem diagnostics.
    boolean validate(AstNode root, Reporter reporter);

    Schema setResolver(SchemaResolver resolver);

}
