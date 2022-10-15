package nl.ica.asd.logic.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"business", "script"})
public class BusinessRules {

  private final Business business;
  private final String script;

  @JsonCreator
  public BusinessRules(
      @JsonProperty("business") Business business,
      @JsonProperty("script") String script
  ) {
    this.business = business;
    this.script = script;
  }

  @JsonGetter("business")
  public Business getBusiness() {
    return business;
  }

  @JsonGetter("script")
  public String getScript() {
    return script;
  }
}
