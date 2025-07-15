package com.auth;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
    private static final Gson gson = new Gson();
    private static Map<String, Map<String, String>> translations = new HashMap<>();

    public static void loadFromResource() {
        try (InputStream stream = LocalizationManager.class.getResourceAsStream("/lang/localizations.json")) {
            if (stream == null) return;
            Reader reader = new InputStreamReader(stream);
            Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
            translations = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key, String langCode) {
        return translations.getOrDefault(langCode, Collections.emptyMap()).getOrDefault(key, key);
    }
}
