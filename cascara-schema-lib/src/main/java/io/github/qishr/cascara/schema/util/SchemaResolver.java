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

import java.net.URI;
import java.util.List;
import java.util.Map;

import io.github.qishr.cascara.common.service.ServiceProvider;
import io.github.qishr.cascara.schema.diagnostic.SchemaException;
import io.github.qishr.cascara.schema.structure.SchemaNode;
import io.github.qishr.cascara.schema.util.SchemaResolver;

public interface SchemaResolver extends ServiceProvider {

    /// Returns the `Schema` indicated by the given `URI`.
    /// If the `Schema` is cached, it will be retrieved from the cache,
    /// otherwise it will be compiled and returned.
    public Schema getSchema(URI uri) throws SchemaException;

    public SchemaNode resolve(String ref, SchemaNode relativeTo) throws SchemaException;

    public Schema getSchemaForClass(Class<?> clazz);

    public Schema getSchemaForClass(Class<?> clazz, List<TypeAnalyzer> typeAnalyzers);

    public SchemaNode resolve(String ref, SchemaNode relativeTo, DynamicScope scope) throws SchemaException;
    public DynamicScope getCurrentScope();

    //
    // Cache
    //

    public void registerSchema(URI uri, Schema compiled);
    public void registerSchemaNode(URI uri, SchemaNode node);
    public Map<URI, Schema> getCachedSchemas();
}