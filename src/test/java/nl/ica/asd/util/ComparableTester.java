package nl.ica.asd.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.validation.constraints.NotNull;

public class ComparableTester<T extends Comparable<T>> {

  private final T baseObject;
  private final T equalObject;

  public ComparableTester(@NotNull T baseObject, @NotNull T equalObject) {
    if (baseObject == null || equalObject == null) {
      throw new NullPointerException("ComparableTester does not accept null values!");
    }
    this.baseObject = baseObject;
    this.equalObject = equalObject;
  }

  public ComparableTester<T> testCompareTo(T lessObject, T moreObject) {
    assertEquals(-1, baseObject.compareTo(moreObject));
    assertEquals(1, baseObject.compareTo(lessObject));
    assertEquals(-1, lessObject.compareTo(baseObject));
    assertEquals(1, moreObject.compareTo(baseObject));
    assertEquals(0, baseObject.compareTo(equalObject));
    return this;
  }
}
