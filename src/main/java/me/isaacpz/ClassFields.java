package me.isaacpz;

import com.esotericsoftware.reflectasm.FieldAccess;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public class ClassFields {
    //Id represents database location (in dot notation), value represents the variable name.
    private HashMap<String, String> fields;

    //A cached version of the ReflectASM field accessor
    private FieldAccess access;
}
