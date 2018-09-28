package com.example.android.preciousplastic.utils;

import java.util.HashMap;
import java.util.Map;

public class SharedResources {

    private static HashMap<String, Object> sharedResource = new HashMap<>();

    public static Object get(String key) {
        return sharedResource.get(key);
    }

    public static Object put(String key, Object value) {
        return sharedResource.put(key, value);
    }

    public static void putAll(Map<? extends String, ?> m) {
        sharedResource.putAll(m);
    }

    public static Object remove(String key) {
        return sharedResource.remove(key);
    }
}
