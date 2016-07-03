package me.isaacpz.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoUtil {
    /*
     * Finds the value of a object in a DBObject from dot notation.
     */
    public static Object getFieldValue(DBObject object, String location) throws InvalidDatabaseFormatException {
        String[] layers = location.split("\\.");
        Object current = object;
        for (String s : layers) {
            if ((current instanceof DBObject)) {
                current = ((BasicDBObject) current).get(s);
            } else {
                throw new InvalidDatabaseFormatException("Attempted to find " + location + ", an invalid object.");
            }
        }
        return current;
    }
}
