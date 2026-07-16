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

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;

public class SchemaException extends LocatableException {
    private static final int UNKNOWN_COORD = LocatableException.UNKNOWN_COORD;

    private final String schemaPath;
    private final Class<?> clazz;

    /// For errors in a schema for a class.
    public SchemaException(Class<?> clazz, DiagnosticCode code, Object... details) {
        this(
            null, null,  UNKNOWN_COORD, UNKNOWN_COORD, clazz, null, code, details
        );
    }

    /// For errors not inside a schema file.
    public SchemaException(DiagnosticCode code, Object... details) {
        this(
            null, null,  UNKNOWN_COORD, UNKNOWN_COORD, null, null, code, details
        );
    }

    /// For errors in a schema caused by an exception.
    public SchemaException(URI uri, Throwable cause, DiagnosticCode code, Object... details) {
        this(
            uri, null,  UNKNOWN_COORD, UNKNOWN_COORD, null, cause, code, details
        );
    }

    /// For errors in a schema.
    public SchemaException(URI uri, DiagnosticCode code, Object... details) {
        this(
            uri, null, UNKNOWN_COORD, UNKNOWN_COORD, null, null, code, details
        );
    }

    /// For errors in a schema where the line and column are known.
    public SchemaException(URI uri, int line, int column, DiagnosticCode code, Object... details) {
        this(
            uri, null, line, column, null, null, code, details
        );
    }

    /// For errors relating to a path in a compiled schema.
    public SchemaException(URI uri, String schemaPath, DiagnosticCode code, Object... details) {
        this(
            uri, schemaPath, UNKNOWN_COORD, UNKNOWN_COORD, null, null, code, details
        );
    }

    /// For errors relating to a path in a schema where the line and column are known.
    public SchemaException(URI uri, String schemaPath, int line, int column, DiagnosticCode code, Object... details) {
        this(
            uri, schemaPath, line, column, null, null, code, details
        );
    }

    public SchemaException(LocalizableException cause) {
        this(
            null, null,  UNKNOWN_COORD, UNKNOWN_COORD, null, cause.getCause(), cause.getCode(), cause.getDetails()
        );
    }


    private SchemaException(URI uri, String schemaPath, int line, int column, Class<?> clazz, Throwable cause, DiagnosticCode code, Object... details) {
        super(uri, line, column, cause, code, details);
        this.schemaPath = schemaPath;
        this.clazz = clazz;
    }

    public String getSchemaPath() { return schemaPath; }
    public Class<?> getType() { return clazz; }
}