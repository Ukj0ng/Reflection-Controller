package com.uk.reflection.filter;

import org.junit.jupiter.api.Test;

public class DispatcherTests {
    @Test
    public void 키값을세터로바꾸기() {
        String key = "username";

        String firstKey = "set";
        String upperKey = key.substring(0, 1).toUpperCase() + key.substring(1);

        System.out.println(firstKey + upperKey);
    }
}
