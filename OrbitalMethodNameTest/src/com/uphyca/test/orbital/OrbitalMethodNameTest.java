package com.uphyca.test.orbital;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OrbitalMethodNameTest extends TestCase {

    public static junit.framework.Test suite() {
        return new OrbitalTestSuite(OrbitalMethodNameTest.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Orbital {
    }

    public static final class OrbitalTestSuite extends TestSuite {

        public OrbitalTestSuite(Class<?> theClass) {
            super();
            for (Method each : theClass.getMethods()) {
                int modifiers = each.getModifiers();

                if (!Modifier.isPublic(modifiers))
                    return;

                String name = each.getName();

                if (name.startsWith("test"))
                    addTest(createTest(theClass, name));

                if (each.isAnnotationPresent(Orbital.class))
                    addTest(createTest(theClass, name));
            }
        }
    }

    @Orbital
    public void assertPreconditions() {

    }

    public void testShouldBeTrue() {
        assertTrue(true);
    }
}
