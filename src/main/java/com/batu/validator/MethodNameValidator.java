package com.batu.validator;

import java.beans.Introspector;
public final class MethodNameValidator {
    public static boolean accessorMatchesField(String methodName, String fieldName) {
        if (methodName == null || fieldName == null) {
            return false;
        }

        String propertyName;

        if (methodName.startsWith("get") && methodName.length() > 3) {
            propertyName = Introspector.decapitalize(methodName.substring(3));
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            propertyName = Introspector.decapitalize(methodName.substring(2));
        } else {
            propertyName = methodName;
        }

        return propertyName.equals(fieldName);
    }
}
