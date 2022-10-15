package nl.ica.asd.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.Collection;

public class assertionsHelpers {

  public static void assertEmpty(Collection value) {
    assertEquals(0, value.size(), "Value was not empty!");
  }

  public static void assertNotEmpty(Collection value) {
    assertNotEquals(0, value.size(), "Value was empty!");
  }

  public static <T> void assertPresent(T needle, Collection<T> haystack) {
    for (T straw : haystack) {
      if (straw.equals(needle)) {
        return;
      }
    }
    assertNotSame(needle, haystack);
  }

  public static <T> void assertNotPresent(T needle, Collection<T> haystack) {
    for (T straw : haystack) {
      if (straw.equals(needle)) {
        assertNotSame(needle, haystack);
      }
    }
  }
}
