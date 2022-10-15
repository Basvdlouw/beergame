package nl.ica.asd.logic.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Agent {

  private final String businessRules;

  @JsonCreator
  public Agent(@JsonProperty("businessRules") String businessRules) {
    this.businessRules = businessRules;
  }

  @JsonGetter("businessRules")
  public String getBusinessRules() {
    return businessRules;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Agent agent = (Agent) o;
    return Objects.equals(getBusinessRules(), agent.getBusinessRules());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getBusinessRules());
  }

  @Override
  public String toString() {
    return "Agent{" +
        "businessRules='" + businessRules + '\'' +
        '}';
  }
}
