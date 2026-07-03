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
    /// @param reporter The reporter that collects or reports problem [Diagnostic] objects.
    public SchemaValidator setReporter(Reporter reporter) {
        this.reporter = reporter == null ? new NoOpReporter() : reporter;
        return this;
    }

    public boolean validate(AstNode root) {
        Schema schema = SchemaUtils.scanForSchema(root, resolver);
        return validate(root, schema);
    }

    public boolean validate(AstNode root, Schema schema) {
        return validate(root, schema.getRoot(), "");
    }

    public boolean validate(AstNode root, SchemaNode schema) {
        return validate(root, schema, "#");
    }

    //
    //
    //

    private boolean validate(AstNode data, SchemaNode schema, String path) {
        // REPORTER.debug("validate " + path);

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
        if (data instanceof MapAstNode<? extends AstNode, ? extends MapEntryAstNode<? extends AstNode>> map && schema instanceof ObjectSchemaNode obj) {

            // Actually faster...
            for (MapEntryAstNode<? extends AstNode> entry : map) {

            // }

            // // This should be faster, but it's not...
            // for (MapEntryAstNode<? extends AstNode> entry : map.entrySet()) {
                AstNode keyNode = entry.getKey();
                AstNode valueNode = entry.getValue();
                String key;
                if (keyNode instanceof ScalarAstNode scalar) {
                    key = scalar.asString();
                } else {
                    key = String.valueOf(keyNode);
                }
                SchemaNode propSchema = obj.getProperty(key);
                // If property is defined in schema, validate it
                if (propSchema != null) {
                    valid &= validate(valueNode, propSchema, path + "/" + key);
                }
            }

            // // Original...
            // for (Object objKey : map.keySet()) {
            //     String key;
            //     if (objKey instanceof ScalarAstNode scalar) {
            //         key = scalar.asString();
            //     } else {
            //         key = String.valueOf(objKey);
            //     }


            //     SchemaNode propSchema = obj.getProperty(key);
            //     // If property is defined in schema, validate it
            //     if (propSchema != null) {
            //         valid &= validate(map.get(key), propSchema, path + "/" + key);
            //     }
            // }
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