package com.podio.sdk.internal.utils;

import java.util.Collection;
import java.util.Map;

import android.net.Uri;

public final class Utils {

    private Utils() {
        // Hide constructor.
    }

    public static boolean isAnyEmpty(String... strings) {
        boolean isEmpty = isEmpty(strings);

        for (int i = 0; !isEmpty && i < strings.length; i++) {
            isEmpty = isEmpty(strings[i]);
        }

        return isEmpty;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null) || (collection.size() == 0);
    }

    public static boolean isEmpty(Uri uri) {
        return (uri == null || uri.equals(Uri.EMPTY));
    }

    public static boolean isEmpty(int[] array) {
        return (array == null) || (array.length == 0);
    }

    public static boolean isEmpty(long[] array) {
        return (array == null) || (array.length == 0);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null) || (map.size() == 0);
    }

    public static boolean isEmpty(Object[] array) {
        return (array == null) || (array.length == 0);
    }

    public static boolean isEmpty(String string) {
        return (string == null) || (string.length() == 0);
    }

    public static boolean notAnyEmpty(String... strings) {
        return !isAnyEmpty(strings);
    }

    public static boolean notEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean notEmpty(Uri uri) {
        return !isEmpty(uri);
    }

    public static boolean notEmpty(int[] array) {
        return !isEmpty(array);
    }

    public static boolean notEmpty(long[] array) {
        return !isEmpty(array);
    }

    public static boolean notEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean notEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static boolean notEmpty(String string) {
        return !isEmpty(string);
    }
}
