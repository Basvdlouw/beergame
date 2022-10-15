package nl.ica.asd.util;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GenericObjectMapper {

  private static GenericObjectMapper genericObjectMapper;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private GenericObjectMapper() {
    this.enableDefaultTyping();
    this.setSerializationInclusion();
  }

  public static GenericObjectMapper getInstance() {
    if (genericObjectMapper == null) {
      genericObjectMapper = new GenericObjectMapper();
    }
    return genericObjectMapper;
  }

  public <T> void writeValue(File file, T value) throws IOException {
    OBJECT_MAPPER.writeValue(file, value);
  }

  public <T> T readValue(InputStream stream, Class<T> value) throws IOException {
    return OBJECT_MAPPER.readValue(stream, value);
  }

  public <T> T readValue(File file, Class<T> value) throws IOException {
    return OBJECT_MAPPER.readValue(file, value);
  }

  private void setSerializationInclusion() {
    OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
  }

  private void enableDefaultTyping() {
    OBJECT_MAPPER.enableDefaultTyping();
  }
}
