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


package io.github.qishr.cascara.schema.structure;

import java.net.URI;
import java.util.List;
import java.util.Map;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.type.PrimitiveType;
import io.github.qishr.cascara.schema.rule.ValidationRule;

public interface SchemaNode extends AstNode {
    PrimitiveType getType();

    /// The human-readable title
    String getTitle();

    String getTitleKey();

    /// The human-readable description
    String getDescription();

    String getDescriptionKey();

    // Structural Access
    SchemaNode getProperty(String key);
    Map<String, SchemaNode> getProperties();

    // For ARRAY types
    SchemaNode getItemSchema();

    // Definitions & Refs
    boolean isRef();
    String getRef();
    SchemaNode getDefinition(String name);
    Map<String, SchemaNode> getDefinitions();
    URI getOriginUri();
    String getDynamicAnchor();

    // Data Defaults & Validation
    Object getDefaultValue();
    List<ValidationRule> getRules();

    boolean validate(AstNode node, String path, Reporter reporter);
    AstNode getOriginAst();

    String getContentMediaType();
    void setContentMediaType(String contentMediaType);

    void setExtension(String key, Object value);
    Object getExtension(String key);
    Map<String,Object> getExtensions();

    boolean isReadOnly();
    void setReadOnly(boolean readOnly);


    default String getFormat() {
        return "";
    }

    default String getFormatOption(String key) {
        return "";
    }

    default boolean getBooleanOption(String key, boolean defaultValue) {
        String val = getFormatOption(key);
        if (val == null || val.isEmpty()) return defaultValue;
        return Boolean.parseBoolean(val);
    }

    void addAllOf(SchemaNode node);
    List<SchemaNode> getAllOf();
    SchemaNode getPropertySchema(String key);

    default boolean areAdditionalPropertiesAllowed() {
        return true;
    }

    default SchemaNode getAdditionalPropertiesSchema() {
        return null;
    }


    /// Returns the schema that defines the structure of THIS node.
    /// For a standard property, this returns the JSON Schema Meta-Schema.
    /// For a CEMA property, this might return the CEMA Meta-Schema.
    SchemaNode getMetaSchema();
}