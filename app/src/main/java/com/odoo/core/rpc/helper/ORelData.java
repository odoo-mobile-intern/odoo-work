package com.odoo.core.rpc.helper;

import java.util.HashMap;

public class ORelData extends HashMap<String, Object> {

    public ORelData add(String key, Object value) {
        put(key, value);
        return this;
    }
}
