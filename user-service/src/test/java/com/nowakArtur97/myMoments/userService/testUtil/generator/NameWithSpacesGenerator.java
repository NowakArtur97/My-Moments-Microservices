package com.nowakArtur97.myMoments.testUtil.generator;

import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import java.lang.reflect.Method;

public class NameWithSpacesGenerator extends ReplaceUnderscores {

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {

        String classMethodName = super.generateDisplayNameForClass(testClass);

        return addSpacesBetweenWords(classMethodName);
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {

        String nestedClassMethodName = nestedClass.getSimpleName();

        return addSpacesBetweenWords(nestedClassMethodName);
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {

        final String wordEndingTestName = "should";
        final String signReplacedInMethodName = "_";
        final String signReplacingInMethodName = " ";

        String testMethodName = testMethod.getName();

        int indexOfShouldWord = testMethodName.indexOf(wordEndingTestName);

        return testMethodName.substring(0, indexOfShouldWord - 1).replace(signReplacedInMethodName,
                signReplacingInMethodName);
    }

    private String addSpacesBetweenWords(String className) {

        int wordLength = className.length();

        StringBuilder result = new StringBuilder();
        result.append(className.charAt(0));

        for (int i = 1; i < wordLength; i++) {

            if (Character.isUpperCase(className.charAt(i))) {

                result.append(' ');
            }

            result.append(className.charAt(i));
        }

        return result.toString();
    }
}
