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


package io.github.qishr.cascara.schema.exception;

import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;

public enum SchemaDiagnosticCode implements DiagnosticCode {
    ERROR("SCHEMA-101", "Error: {0}"),

    // Schema URI
    UNIMPLEMENTED("SCHEMA-102", "Unimplemented: {0}"),
    INVALID_SCHEMA_URI("SCHEMA-103", "Not a valid schema URI: {0}."),
    UNRECOGNIZED_LIFECYCLE("SCHEMA-104", "Unrecognized schema lifecycle: {0}."),
    MISSING_MODULE_NAME("SCHEMA-105", "Missing module name"),
    MISSING_SCHEMA_NAME("SCHEMA-106", "Missing schema name"),
    MISSING_VERSION("SCHEMA-107", "Missing version"),

    // Compiler
    COMPILER("SCHEMA-201", "Error: {0}"),
    ROOT_MUST_BE_MAP("SCHEMA-202", "Document root must be a map"),
    NO_ID("SCHEMA-203", "Document must contain $id or origin URI must be given to compiler"),

    // Decompiler
    DECOMPILER("SCHEMA-301", "Error: {0}"),
    MISSING_REF("SCHEMA-302", "Missing $ref: {0}"),

    // Store
    STORE("SCHEMA-401", "Error: {0}"),
    FAILED_TO_STORE("SCHEMA-402", "Failed to store schema: {0}"),
    DYNAMIC_NOT_ALLOWED("SCHEMA-403", "Dynamic Lifecycle not allowed in SchemaStore"),
    NOT_FOUND("SCHEMA-404", "Schema not found: {0}"),

    // Resolver
    RESOLVER("SCHEMA-501", "Error: {0}"),
    RESOLUTION_FAILED("SCHEMA-502", "Resolution failed"),
    LOCAL_RESOLUTION_FAILED("SCHEMA-503", "Could not resolve local $schema: {0}"),
    NODE_NOT_FOUND("SCHEMA-504", "Could not find node for fragment {0}"),
    META_INITIALIZATION_FAILURE("SCHEMA-505", "Failed to initialize built-in meta-schemas."),

    // Generator
    GENERATOR("SCHEMA-601", "Error: {0}"),
    NOT_OBJECT("SCHEMA-602", "Path does not resolve to an object: {0}."),

    // Validator
    VALIDATION_WARNING("SCHEMA-601", "Validation warning: {0}"),
    VALIDATION_ERROR("SCHEMA-602", "Validation error: {0}"),
    MISSING_REQUIRED_PROPERTY("SCHEMA-603", "Missing required property: {0}"),
    EXPECTED_TYPE("SCHEMA-604", "Expected {0} but found {1}"),
    DUPLICATE_ITEM("SCHEMA-605","Duplicate item found: {0}. Array must have unique items."),
    DOES_NOT_MATCH_PATTERN("SCHEMA-606","Value does not match the required pattern: {0}"),
    LESS_THAN_MIN_VALUE("SCHEMA-607", "Value {0} is less than the minimum allowed ({1})"),
    LESS_THAN_MIN_LENGTH("SCHEMA-608","Length {0} is less than the minimum allowed ({1})"),
    LESS_THAN_MIN_ITEMS("SCHEMA-609", "Expected an array with at least {0} items."),
    LESS_THAN_MIN_ITEMS_2("SCHEMA-610", "Array has too few items ({0}). Minimum required is {1}."),
    MORE_THAN_MAX_VALUE("SCHEMA-611", "Value {0} is more than the maximum allowed ({1})"),
    MORE_THAN_MAX_LENGTH("SCHEMA-612", "Length {0} is more than the maximum allowed ({1})"),
    MORE_THAN_MAX_ITEMS("SCHEMA-613", "Array has too many items ({0}). Maximum allowed is {1}."),
    NOT_ALLOWED_IN_LIST("SCHEMA-614", "Value '{0}' is not in allowed list. Allowed values: {1}"),
    BROKEN_SCHEMA_REF("SCHEMA-615", "Broken schema reference: {0} ({1})"),
    TARGET_IS_NULL("SCHEMA-616", "Target of reference '{0}' is null");




    private final String code;
    private final String message;

    SchemaDiagnosticCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
}