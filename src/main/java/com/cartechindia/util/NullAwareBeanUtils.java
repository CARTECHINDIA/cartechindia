package com.cartechindia.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;

 public class NullAwareBeanUtils {

    public static void copyNonNullProperties(Object source, Object target) {
        // Get null property names
        final var srcFields = source.getClass().getDeclaredFields();
        String[] nullPropertyNames = java.util.Arrays.stream(srcFields)
                .filter(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(source) == null;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .map(Field::getName)
                .toArray(String[]::new);

        // Copy ignoring nulls
        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }
}
