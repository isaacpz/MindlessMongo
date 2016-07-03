package me.isaacpz;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import me.isaacpz.util.InvalidDatabaseFormatException;
import me.isaacpz.util.MongoUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MongoDocument {
    private transient MongoClient client;
    private transient String database;
    private transient String collection;
    private transient DBObject query;

    private transient ClassFields fields;
    private transient DBObject databaseObject;

    public MongoDocument(MongoClient client, String database, String collection, BasicDBObject query) {
        this.client = client;
        this.database = database;
        this.collection = collection;
        this.query = query;

        mapFields();
    }
    
    private void mapFields() {
        HashMap<String, String> fields = new HashMap();
        for (Field f : getClass().getDeclaredFields()) {
            if ((!Modifier.isTransient(f.getModifiers())) && (f.isAnnotationPresent(StoredField.class))) {
                fields.put(((StoredField) f.getAnnotation(StoredField.class)).value(), f.getName());
            }
        }
        this.fields = new ClassFields(fields, FieldAccess.get(getClass()));
    }


    public void load() throws InvalidDatabaseFormatException {
        setFieldValues(loadDatabaseObject());
    }

    private void setFieldValues(DBObject databaseObject)
            throws InvalidDatabaseFormatException {
        this.databaseObject = loadDatabaseObject();
        for (Map.Entry<String, String> fieldLocation : this.fields.getFields().entrySet()) {
            Object toSet = MongoUtil.getFieldValue(databaseObject, (String) fieldLocation.getKey());
            if (toSet != null) {
                this.fields.getAccess().set(this, (String) fieldLocation.getValue(), toSet);
            }
        }
    }

    protected DBObject loadDatabaseObject() {
        return this.client.getDB(this.database).getCollection(this.collection).findOne(this.query);
    }

    public void save() throws InvalidDatabaseFormatException {
        BasicDBObject toChange = new BasicDBObject();
        for (Map.Entry<String, String> fieldLocation : this.fields.getFields().entrySet()) {
            Object starting = MongoUtil.getFieldValue(this.databaseObject, (String) fieldLocation.getKey());
            Object now = this.fields.getAccess().get(this, (String) fieldLocation.getValue());
            if (((starting == null) && (now != null)) || (!starting.equals(now))) {
                toChange.put(fieldLocation.getKey(), now);
            }
        }
        if (toChange.size() > 0) {
            this.client.getDB(this.database).getCollection(this.collection).update(this.query, new BasicDBObject("$set", toChange));
        }
    }

    public void remove() {
        this.client.getDB(this.database).getCollection(this.collection).remove(this.query);
    }
}
