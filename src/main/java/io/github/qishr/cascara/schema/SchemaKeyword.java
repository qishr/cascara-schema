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

import java.util.Collections;
import java.util.List;

import io.github.qishr.cascara.common.lang.type.PrimitiveType;

public enum SchemaKeyword {
    // Core Identifiers
    ID("$id", PrimitiveType.STRING),
    SCHEMA("$schema", PrimitiveType.STRING),
    ANCHOR("$anchor", PrimitiveType.STRING),
    DYNAMIC_REF("$dynamicRef", PrimitiveType.STRING),
    DYNAMIC_ANCHOR("$dynamicAnchor", PrimitiveType.STRING),
    VOCABULARY("$vocabulary", PrimitiveType.OBJECT),

    // Sub-schema Containers
    DEFS("$defs", PrimitiveType.OBJECT),
    DEFINITIONS("definitions", PrimitiveType.OBJECT), // Legacy support
    REF("$ref", PrimitiveType.STRING),

    // Logic and Conditional
    ALL_OF("allOf", PrimitiveType.ARRAY),
    ANY_OF("anyOf", PrimitiveType.ARRAY),
    ONE_OF("oneOf", PrimitiveType.ARRAY),
    NOT("not", PrimitiveType.OBJECT),
    IF("if", PrimitiveType.OBJECT),
    THEN("then", PrimitiveType.OBJECT),
    ELSE("else", PrimitiveType.OBJECT),
    DEPENDENT_SCHEMAS("dependentSchemas", PrimitiveType.OBJECT),
    DEPENDENT_REQUIRED("dependentRequired", PrimitiveType.OBJECT),

    // Object Validation
    PROPERTIES("properties", PrimitiveType.OBJECT),
    PATTERN_PROPERTIES("patternProperties", PrimitiveType.OBJECT),
    ADDITIONAL_PROPERTIES("additionalProperties", PrimitiveType.OBJECT),
    UNEVALUATED_PROPERTIES("unevaluatedProperties", PrimitiveType.OBJECT),
    REQUIRED("required", PrimitiveType.ARRAY),
    PROPERTY_NAMES("propertyNames", PrimitiveType.OBJECT),
    MIN_PROPERTIES("minProperties", PrimitiveType.INTEGER),
    MAX_PROPERTIES("maxProperties", PrimitiveType.INTEGER),

    // Array Validation
    ITEMS("items", PrimitiveType.ANY), // Can be Object or Array (Draft 7)
    PREFIX_ITEMS("prefixItems", PrimitiveType.ARRAY),
    UNEVALUATED_ITEMS("unevaluatedItems", PrimitiveType.OBJECT),
    CONTAINS("contains", PrimitiveType.OBJECT),
    MIN_CONTAINS("minContains", PrimitiveType.INTEGER),
    MAX_CONTAINS("maxContains", PrimitiveType.INTEGER),
    MIN_ITEMS("minItems", PrimitiveType.INTEGER),
    MAX_ITEMS("maxItems", PrimitiveType.INTEGER),
    UNIQUE_ITEMS("uniqueItems", PrimitiveType.BOOLEAN),

    // Scalar Validation
    // TYPE("type", PrimitiveType.STRING),
    TYPE("type", PrimitiveType.STRING, List.of(
        "string", "number", "integer", "boolean", "object", "array", "null"
    )),
    ENUM("enum", PrimitiveType.ARRAY),
    CONST("const", PrimitiveType.ANY),
    MULTIPLE_OF("multipleOf", PrimitiveType.NUMBER),
    MAXIMUM("maximum", PrimitiveType.NUMBER),
    EXCLUSIVE_MAXIMUM("exclusiveMaximum", PrimitiveType.NUMBER),
    MINIMUM("minimum", PrimitiveType.NUMBER),
    EXCLUSIVE_MINIMUM("exclusiveMinimum", PrimitiveType.NUMBER),
    MAX_LENGTH("maxLength", PrimitiveType.INTEGER),
    MIN_LENGTH("minLength", PrimitiveType.INTEGER),
    PATTERN("pattern", PrimitiveType.STRING),

    // Metadata & Documentation
    TITLE("title", PrimitiveType.STRING),
    DESCRIPTION("description", PrimitiveType.STRING),
    DEFAULT("default", PrimitiveType.ANY),
    DEPRECATED("deprecated", PrimitiveType.BOOLEAN),

    READ_ONLY("readOnly", PrimitiveType.BOOLEAN),


    WRITE_ONLY("writeOnly", PrimitiveType.BOOLEAN),
    FORMAT("format", PrimitiveType.STRING),
    CONTENT_MEDIA_TYPE("contentMediaType", PrimitiveType.STRING),
    CONTENT_ENCODING("contentEncoding", PrimitiveType.STRING);

    private final String string;
    private final PrimitiveType type;
    private final List<String> suggestions;

    SchemaKeyword(String string, PrimitiveType type) {
        this(string, type, Collections.emptyList());
    }

    SchemaKeyword(String string, PrimitiveType type, List<String> suggestions) {
        this.string = string;
        this.type = type;
        this.suggestions = suggestions;
    }

    /// Returns the JSON Schema keyword name
    public String asString() { return string; }

    public PrimitiveType type() { return type; }
    public List<String> suggestions() { return suggestions; }
    public boolean hasSuggestions() { return !suggestions.isEmpty(); }

    public static SchemaKeyword fromString(String s) {
        SchemaKeyword keyword = get(s);
        if (keyword == null) {
            throw new IllegalArgumentException("No enum constant with value " + s);
        }
        return keyword;
    }

    public static boolean exists(String s) {
        return get(s) != null;
    }

    public static SchemaKeyword get(String s) {
        if (s == null) return null;
        for (SchemaKeyword keyword : values()) {
            if (keyword.string.equalsIgnoreCase(s)) {
                return keyword;
            }
        }
        return null;
    }
}
