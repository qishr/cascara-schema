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

import java.util.Map;
import java.util.Objects;

import io.github.qishr.cascara.schema.structure.SchemaNode;

public final class SchemaComparator {

    public static boolean equals(SchemaNode a, SchemaNode b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        if (a.getType() != b.getType()) return false;
        if (!Objects.equals(a.getTitle(), b.getTitle())) return false;
        if (!Objects.equals(a.getDescription(), b.getDescription())) return false;
        if (!Objects.equals(a.getDefaultValue(), b.getDefaultValue())) return false;
        if (a.isReadOnly() != b.isReadOnly()) return false;
        // if (a.isHidden() != b.isHidden()) return false;

        // TODO: compare custom hints

        // Compare validation rules (optional)
        if (!Objects.equals(a.getRules(), b.getRules())) return false;

        // Compare refs
        if (a.isRef() != b.isRef()) return false;
        if (a.isRef() && !Objects.equals(a.getRef(), b.getRef())) return false;

        return switch (a.getType()) {
            case OBJECT -> compareObjects(a, b);
            case ARRAY -> compareArrays(a, b);
            default -> true;
        };
    }

    private static boolean compareObjects(SchemaNode a, SchemaNode b) {
        Map<String, SchemaNode> pa = a.getProperties();
        Map<String, SchemaNode> pb = b.getProperties();

        if (!pa.keySet().equals(pb.keySet())) return false;

        for (String key : pa.keySet()) {
            if (!equals(pa.get(key), pb.get(key))) return false;
        }

        // Compare definitions
        Map<String, SchemaNode> da = a.getDefinitions();
        Map<String, SchemaNode> db = b.getDefinitions();

        if (!da.keySet().equals(db.keySet())) return false;

        for (String key : da.keySet()) {
            if (!equals(da.get(key), db.get(key))) return false;
        }

        return true;
    }

    private static boolean compareArrays(SchemaNode a, SchemaNode b) {
        return equals(a.getItemSchema(), b.getItemSchema());
    }
}
