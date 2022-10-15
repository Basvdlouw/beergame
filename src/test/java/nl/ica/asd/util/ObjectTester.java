package nl.ica.asd.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import javax.validation.constraints.NotNull;

public class ObjectTester<T> {

  private final T baseObject;
  private final T equalObject;
  private final T differentObject;

  public ObjectTester(@NotNull T baseObject, @NotNull T equalObject, @NotNull T differentObject) {
    if (baseObject == null || equalObject == null || differentObject == null) {
      throw new NullPointerException("ObjectTester does not accept null values!");
    }
    this.baseObject = baseObject;
    this.equalObject = equalObject;
    this.differentObject = differentObject;
  }

  public ObjectTester<T> testEquals() {
    assertEquals(baseObject, baseObject);
    assertEquals(baseObject, equalObject);
    assertNotEquals(baseObject, differentObject);
    assertNotEquals(baseObject, null);
    assertNotEquals(baseObject, new Object());
    return this;
  }

  public ObjectTester<T> testToString() {
    assertEquals(baseObject.toString(), equalObject.toString());
    assertNotEquals(baseObject.toString(), differentObject.toString());
    return this;
  }

  public ObjectTester<T> testHashCode() {
    assertEquals(baseObject.hashCode(), equalObject.hashCode());
    // Note that it might be valid for the not-equals test to fail!
    assertNotEquals(baseObject.hashCode(), differentObject.hashCode());
    return this;
  }
}
