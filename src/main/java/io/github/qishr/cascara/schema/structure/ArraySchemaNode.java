package io.github.qishr.cascara.schema.structure;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.SequenceAstNode;
import io.github.qishr.cascara.common.lang.type.PrimitiveType;

import java.util.*;

public class ArraySchemaNode extends AbstractSchemaNode {
    private SchemaNode items; // This is our Item Template

    public ArraySchemaNode(SchemaNode metaSchema) {
        super(PrimitiveType.ARRAY, metaSchema);
    }

    public SchemaNode getItemSchema() {
        return items;
    }

    public void setItemTemplate(SchemaNode items) {
        this.items = items;
    }

    @Override
    public Map<String, SchemaNode> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        boolean valid = super.validate(node, path, reporter);

        if (node instanceof SequenceAstNode sequence && items != null) {
            int i = 0;
            for (AstNode item : sequence.getChildren()) {
                String itemPath = path + "[" + i + "]";
                valid &= items.validate(item, itemPath, reporter);
                i++;
            }
        }
        return valid;
    }
}