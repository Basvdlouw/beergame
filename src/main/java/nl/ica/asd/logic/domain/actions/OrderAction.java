package nl.ica.asd.logic.domain.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;
import nl.ica.asd.logic.domain.Action;

@JsonPropertyOrder({"amount", "sender", "receiver"})
public class OrderAction extends Action {

  @JsonCreator
  public OrderAction(
      @JsonProperty("amount") Integer amount,
      @JsonProperty("sender") UUID sender,
      @JsonProperty("receiver") UUID receiver
  ) {
    super(amount, sender, receiver);
  }
}
