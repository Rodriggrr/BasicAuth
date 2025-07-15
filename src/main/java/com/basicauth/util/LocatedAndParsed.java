package com.basicauth.util;

import com.basicauth.exception.MalformedParsedString;

import net.minecraft.text.MutableText;

public class LocatedAndParsed {
    public static MutableText parseFromJSON(String field, Object... args) throws MalformedParsedString {
        try {
            return Colored.parse(LocalizationManager.get(field, LocalizationManager.LOCALE, args));
        } catch (MalformedParsedString e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String commandFromJSON(String field) {
        return LocalizationManager.get(field, LocalizationManager.LOCALE);
    }
}
