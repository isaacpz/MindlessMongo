package me.isaacpz;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StoredField
{
    /*
     * Represents the location of a field in the database
     * Dot notation may be used in order to access data in sub documents
     */
    String value();
}
