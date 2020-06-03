package fi.vm.sade.koodisto.test.support;

import org.hamcrest.Matcher;

import static org.junit.Assert.assertTrue;

public class Assertions {

    public static void assertException(Runnable runnable, Matcher<Exception> matcher) {
        try {
            runnable.run();
        } catch (Exception e) {
            assertTrue(matcher.matches(e));
        }
    }

}
