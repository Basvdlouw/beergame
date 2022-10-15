package nl.ica.asd.network.communication.datatransferobjects.directlyserializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.ica.asd.network.communication.datatransferobjects.DataTransferObject;


public class SerializationExceptionResponse extends DataTransferObject {

  private final String errorMessage;

  @JsonCreator
  public SerializationExceptionResponse(
      @JsonProperty("errorMessage") String errorMessage
  ) {
    this.errorMessage = errorMessage;
  }

  @JsonGetter("errorMessage")
  public String getErrorMessage() {
    return errorMessage;
  }
}
