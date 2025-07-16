package com.basicauth.exception;

import java.util.function.Supplier;

@FunctionalInterface
public interface Wrapper<T> {
    T get() throws Exception;

    static <T> Supplier<T> wrap(Wrapper<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

