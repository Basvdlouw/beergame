package nl.ica.asd.util;

import org.mockito.internal.verification.Times;

public class MockitoHelpers {

  public static Times once() {
    return new Times(1);
  }

}
