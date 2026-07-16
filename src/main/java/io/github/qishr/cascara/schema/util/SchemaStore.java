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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.qishr.cascara.common.content.ResourceContent;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.util.ContentTypes;
import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.lang.json.processor.JsonConverter;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;
import io.github.qishr.cascara.schema.exception.SchemaException;
import io.github.qishr.cascara.schema.util.CascaraSchemaUri.Lifecycle;

public class SchemaStore {

    private static SchemaStore instance;
    private static final Path cascaraDir = Paths.get(System.getProperty("user.home")).resolve(".cascara");
    private static final Path schemasDir = cascaraDir.resolve("schemas");

    public static SchemaStore instance() {
        if (instance == null) {
            instance = new SchemaStore();
        }
        return instance;
    }

    public ResourceContent get(CascaraSchemaUri schemaUri) throws SchemaException {
        // TODO:
        // prevent ../../ being used to look outside the store

        if (schemaUri.getLifecycle() == Lifecycle.DYNAMIC) {
            throw illegalLifecycle(schemaUri);
        }

        Path schemaFile = getPath(schemaUri).resolve("schema.json");
        if (!Files.exists(schemaFile)) {
            throw notFound(schemaUri);
        }

        String schemaSource;
        try {
            schemaSource = Files.readString(schemaFile);
        } catch (IOException e) {
            throw new SchemaException(schemaUri.toUri(), GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }

        ContentType contentType = ContentTypes.find("application/schema+json");
        ResourceContent rc = new ResourceContent(schemaSource, contentType);
        return rc;
    }

    public void put(CascaraSchemaUri schemaUri, Schema compiled) {
        if (schemaUri.getLifecycle() == Lifecycle.DYNAMIC) {
            throw illegalLifecycle(schemaUri);
        }

        SchemaDecompiler decompiler = new SchemaDecompiler();
        AstNode doc = decompiler.decompile(compiled);

        String schemaString = new JsonConverter().toText(doc);

        Path schemaDir = getPath(schemaUri);
        try {
            if (!Files.exists(schemaDir)) {
                Files.createDirectories(schemaDir);
            }
            Path path = schemaDir.resolve("schema.json");
            Files.writeString(path, schemaString);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SchemaException(schemaUri.toUri(), e, SchemaDiagnosticCode.FAILED_TO_STORE, e.getMessage());
        }
    }

    private Path getPath(CascaraSchemaUri schemaUri) throws SchemaException {
        Path moduleDir = schemasDir.resolve(schemaUri.getModuleName());
        Path schemaDir = moduleDir.resolve(schemaUri.getSchemaName());

        Path versionDir;
        if (schemaUri.getLifecycle() == Lifecycle.RESOURCE) {
            // TODO: Find latest version
            throw new SchemaException(schemaUri.toUri(), SchemaDiagnosticCode.UNIMPLEMENTED, "Lifecycle.RESOURCE");
        } else {
            versionDir = schemaDir.resolve(schemaUri.getVersion());
            return versionDir;
        }
    }

    private SchemaException notFound(CascaraSchemaUri schemaUri) {
        return new SchemaException(schemaUri.toUri(), SchemaDiagnosticCode.NOT_FOUND, schemaUri);
    }

    private SchemaException illegalLifecycle(CascaraSchemaUri schemaUri) {
        return new SchemaException(schemaUri.toUri(), SchemaDiagnosticCode.DYNAMIC_NOT_ALLOWED);
    }
}
