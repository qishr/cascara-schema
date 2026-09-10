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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.type.PrimitiveType;
import io.github.qishr.cascara.common.trackable.Trackable;
import io.github.qishr.cascara.common.trackable.TrackableObject;
import io.github.qishr.cascara.common.trackable.diagnostic.TrackingDiagnosticCode;
import io.github.qishr.cascara.common.trackable.diagnostic.TrackingException;
import io.github.qishr.cascara.common.trackable.property.TrackableProperty;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.structure.SchemaNode;

public class SchematicObject extends TrackableObject {
    private static Map<Class<? extends SchematicObject>,SchemaNode> objectSchemas = new HashMap<>();

    private final TrackableProperty<SchemaNode> objectSchema = new TrackableProperty<>();

    private final TrackableProperty<String> displayStringProperty;

    public SchematicObject() {
        super();
        displayStringProperty = new TrackableProperty<>(this, "displayString", "");
        createObjectSchema();

        //
        // TODO: set additionalPropertiesAllowed based on schema annotations
        //

        if (objectSchema.getValue() == null) {
            throw new TrackingException(TrackingDiagnosticCode.SCHEMA_GENERATION_ERROR, this.getClass().getSimpleName());
        }
        registerProperties();
    }

    //
    // Schema
    //

    public final SchemaNode getObjectSchema() { return objectSchema.getValue(); }

    public final void setObjectSchema(SchemaNode schema) {
        this.objectSchema.setValue(schema);
        getTrackablesMap().clear();
        registerProperties();
    }

    public String getContentType() {
        return null;
    }

    private void createObjectSchema() {
        SchemaNode schemaNode = objectSchemas.get(getClass());
        if (schemaNode == null) {


            // TODO:
            // We need to change how this works so it can be used
            // outwith cascara.ui.
            //
            // if (!TypeAnalyzers.isRegistered(UiTypeAnalyzer.class)) {
            //     TypeAnalyzers.register(new UiTypeAnalyzer());
            // }


            CascaraSchemaUri schemaUri = CascaraSchemaUri.of(getClass());
            SchemaGenerator generator = new SchemaGenerator();
            AstNode doc = generator.generate(this);
            SchemaCompiler compiler = new SchemaCompiler();
            Schema compiledSchema = compiler.compile(doc, schemaUri.toUri());
            schemaNode = compiledSchema.getRoot();
            if (schemaNode == null) {
                throw new TrackingException(TrackingDiagnosticCode.SCHEMA_COMPILATION_ERROR);
            }
            objectSchemas.put(getClass(), schemaNode);
        }
        objectSchema.setValue(schemaNode);
    }

    public Set<String> getPropertyNames() {
        return getTrackablesMap().keySet();
    }

    private void configureProperty(TrackableProperty<?> property, String propertyName, PrimitiveType schemaType, String mediaType, boolean isDeclaredProperty) {
        property.setName(propertyName);
        property.setPrimitiveType(schemaType);
        property.setMediaType(mediaType);
        property.setDeclaredProperty(isDeclaredProperty);
        getTrackablesMap().put(propertyName, property);
    }

    private void registerProperties() {
        Map<String,Field> fieldMap = createFieldMap();
        Map<String,SchemaNode> propertySchemas = objectSchema.getValue().getProperties();
        for (Entry<String,SchemaNode> entry : propertySchemas.entrySet()) {
            String propertyName = entry.getKey();
            SchemaNode propertySchema = entry.getValue();
            Field field = fieldMap.get(propertyName);
            if (field != null && field.getType().equals(TrackableProperty.class)) {
                // TODO: ObservableList fields
                registerDeclaredProperty(field, propertySchema);
            } else {
                createObservableProperty(propertyName, propertySchema);
            }

        }
    }

    private void registerDeclaredProperty(Field field, SchemaNode propertySchema) throws TrackingException {
        String propertyName = field.getName();
        boolean accessible = field.canAccess(this);
        field.setAccessible(true);

        // Get the field's existing value, if any.
        Object fieldValue = null;
        try {
            fieldValue = field.get(this);
        } catch (Throwable e) {
            // DO NOT use GlobalReporter here. Since this class is used in the logger,
            // it would get into an infinite loop of failing to report itself.
            System.err.println("Error setting value of " + propertyName + " in " + getClass().getSimpleName());
            // This is possibly okay.
        }

        // If the field has no value, set it.
        TrackableProperty<?> property;
        if (fieldValue instanceof TrackableProperty prop) {
            property = prop;
        } else {
            property = new TrackableProperty<>(this, propertyName);
            try {
                field.set(this, property);
            } catch (Throwable e) {
                // The field value was unset and we can't set it.
                throw new TrackingException(e, TrackingDiagnosticCode.CANNOT_SET_VALUE, propertyName, e.getMessage());
            }
        }

        configureProperty(property, propertyName, propertySchema.getType(), propertySchema.getContentMediaType(), true);
        property.addTracker((p) -> invalidate());
        field.setAccessible(accessible);
    }

    @SuppressWarnings({ "rawtypes"})
    public void createObservableProperty(String key, SchemaNode propertySchema) {
        switch (propertySchema.getType()) {
            case OBJECT: {
                TrackableProperty prop = new TrackableProperty<>(this, key);
                configureProperty(prop, key, propertySchema.getType(), propertySchema.getContentMediaType(), false);
                prop.addTracker((p) -> invalidate());
                return;
            }
            case ARRAY: {
                TrackableProperty prop = new TrackableProperty<>(this, key);
                configureProperty(prop, key, propertySchema.getType(), propertySchema.getContentMediaType(), false);
                prop.addTracker((p) -> invalidate());



                // TODO: We used to do this when it was in ObservableObject.
                // I don't think we need to now - it should be handle by
                // TrackableProperty. This needs tested.

                // TrackableArray prop = new TrackableArray();
                // map.put(key, prop);

                // prop.addListener((ListChangeListener.Change c) -> {
                //     invalidate();
                // });

                //

                // prop.addArrayListener((a,c) -> {
                //     invalidate();
                // });



                return;
            }
            case STRING: {
                TrackableProperty<String> prop = new TrackableProperty<>(this, key);
                configureProperty(prop, key, propertySchema.getType(), propertySchema.getContentMediaType(), false);
                prop.addTracker((p) -> invalidate());
                return;
            }
            case NUMBER: {
                TrackableProperty<Double> prop = new TrackableProperty<>(this, key);
                configureProperty(prop, key, propertySchema.getType(), propertySchema.getContentMediaType(), false);
                prop.addTracker((p) -> invalidate());
                return;
            }
            case INTEGER: {
                TrackableProperty<Long> prop = new TrackableProperty<>(this, key);
                configureProperty(prop, key, propertySchema.getType(), propertySchema.getContentMediaType(), false);
                prop.addTracker((p) -> invalidate());
                return;
            }
            case BOOLEAN: {
                TrackableProperty<Boolean> prop = new TrackableProperty<>(this, key);
                configureProperty(prop, key, propertySchema.getType(), propertySchema.getContentMediaType(), false);
                prop.addTracker((p) -> invalidate());
                return;
            }
            default: {
                System.out.println("Unhandled schema type for " + key + ": " + propertySchema.getType());
                return;
            }
        }
    }

    @Override
    public void set(String key, Object value) {
        Trackable observable = getTrackablesMap().get(key);

        if (observable == null && additionalPropertiesAllowed()) {
            System.err.println("TODO: create and add to map: " + observable);
            // TODO: create and add to map
        }
        super.set(key, value);
    }


    //
    // Properties
    //

    public final TrackableProperty<String> displayStringProperty() { return displayStringProperty; }
    public final TrackableProperty<SchemaNode> objectSchemaProperty() { return objectSchema; }

    //
    //
    //

    private Map<String,Field> createFieldMap() {
        Map<String,Field> fields = new HashMap<>();
        Class<?> current = this.getClass();

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                fields.put(field.getName(), field);
            }
            current = current.getSuperclass();
        }

        return fields;
    }
}
