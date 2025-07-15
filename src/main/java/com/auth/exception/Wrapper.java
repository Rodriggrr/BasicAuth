package com.auth.exception;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import java.util.List;
import java.util.Map;
import java.util.Collections;

import com.auth.exception.*;

@FunctionalInterface
public interface Wrapper<T> {
    T get() throws Exception;

    public static <T> Supplier<T> wrap(Wrapper<T> supplier) {
    return () -> {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}

}
