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


package io.github.qishr.cascara.schema.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import io.github.qishr.cascara.schema.util.SchemaResolver;

public class ValidationContext {
    private final SchemaResolver resolver;
    private final Deque<String> pathStack = new ArrayDeque<>();
    private final Map<String, Object> metadata = new HashMap<>();

    public ValidationContext(SchemaResolver resolver) {
        this.resolver = resolver;
        this.pathStack.push(""); // Root path
    }

    // Path management for error reporting
    public void pushPath(String segment) {
        String current = pathStack.peek();
        pathStack.push(current.isEmpty() ? segment : current + "/" + segment);
    }

    public void popPath() {
        pathStack.pop();
    }

    public String getCurrentPath() {
        return pathStack.peek();
    }

    // Access to resolver for dynamic resolution during validation
    public SchemaResolver getResolver() {
        return resolver;
    }

    // Allows rules to share state (e.g., cross-field validation)
    public void setAttribute(String key, Object value) { metadata.put(key, value); }
    public Object getAttribute(String key) { return metadata.get(key); }
}