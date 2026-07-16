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


package io.github.qishr.cascara.schema.util;

import io.github.qishr.cascara.common.diagnostic.GlobalReporter;
import io.github.qishr.cascara.common.diagnostic.NoOpReporter;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.MapAstNode;
import io.github.qishr.cascara.common.lang.ast.MapEntryAstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.common.lang.ast.SequenceAstNode;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.internal.SchemaUtils;
import io.github.qishr.cascara.schema.rule.ValidationRule;
import io.github.qishr.cascara.schema.structure.ArraySchemaNode;
import io.github.qishr.cascara.schema.structure.LazySchemaNode;
import io.github.qishr.cascara.schema.structure.ObjectSchemaNode;
import io.github.qishr.cascara.schema.structure.SchemaNode;

public class SchemaValidator {
    // private static final GlobalReporter REPORTER = GlobalReporter.forClass(SchemaValidator.class);
    private final SchemaResolver resolver;
    private Reporter reporter = new NoOpReporter();

    public SchemaValidator() {
        this(null, null);
    }

    public SchemaValidator(Reporter reporter) {
        this(null, reporter);
    }

    /// Sets the SchemaResolver that will be used to obtain the schema of the AST being validated.
    public SchemaValidator(SchemaResolver resolver) {
        this(resolver, null);
    }

    /// Sets the SchemaResolver that will be used to obtain the schema of the AST being validated.
    public SchemaValidator(SchemaResolver resolver, Reporter reporter) {
        this.resolver = resolver == null ? Schemas.getResolver() : resolver;
        this.reporter = reporter == null ? new NoOpReporter() : reporter;
    }

    /// Registers a reporter to collect or report problem-level diagnostics.
    /// If the reporter is not capable of collecting problems, a ValidationException
    //  will be thrown if a call to `validate` finds a problem.
    ///
    /// @param reporter The reporter that collects or reports problem `Diagnostic` objects.
    public SchemaValidator setReporter(Reporter reporter) {
        this.reporter = reporter == null ? new NoOpReporter() : reporter;
        return this;
    }

    public boolean validate(AstNode root) {
        Schema schema = SchemaUtils.scanForSchema(root, resolver);
        return validate(root, schema);
    }

    public boolean validate(AstNode root, Schema schema) {
        return validate(root, schema.getRoot(), "#");
    }

    public boolean validate(AstNode root, SchemaNode schema) {
        return validate(root, schema, "#");
    }

    //
    //
    //

    private boolean validate(AstNode data, SchemaNode schema, String path) {
        boolean valid = true;

        // 1. Resolve references if the current node is lazy
        if (schema instanceof LazySchemaNode lazy) {
            schema = lazy.getResolved();
        }

        // 2. Invoke the rules already attached to this node
        // (The same rules your GUI uses)
        for (ValidationRule rule : schema.getRules()) {
            valid &= rule.validate(data, path, reporter);
        }

        // 3. Recurse into children based on structure
        if (data instanceof MapAstNode<?, ? extends AstNode, ? extends MapEntryAstNode<?, ? extends AstNode>> map && schema instanceof ObjectSchemaNode obj) {
        // if (data instanceof MapAstNode map && schema instanceof ObjectSchemaNode obj) {
            // TODO: We can make this faster yet.
            // If this for is using an iterator, change it to a regular for loop.
            // The only problem is how do we get the entry set without using
            // the underlying linked hash map's iterator.
            for (MapEntryAstNode<?, ? extends AstNode> entry : map) {
                // for (Object oe : map) {
                //     MapEntryAstNode<? extends AstNode> entry = (MapEntryAstNode<? extends AstNode>)oe;
                String key = entry.getKeyString();
                AstNode valueNode = entry.getValue();
                SchemaNode propSchema = obj.getProperty(key);
                // If property is defined in schema, validate it
                if (propSchema != null) {
                    valid &= validate(valueNode, propSchema, path + "/" + key);
                }
            }

        } else if (data instanceof SequenceAstNode<? extends AstNode> seq && schema instanceof ArraySchemaNode arr) {
            int i = 0;
            for (AstNode element : seq) {
                valid &= validate(element, arr.getItemSchema(), path + "[" + i + "]");
                i++;
            }
        }
        return valid;
    }
}