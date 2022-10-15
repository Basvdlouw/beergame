package nl.ica.asd.logic.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.Objects;
import java.util.UUID;
import nl.ica.asd.logic.domain.actions.DeliveryAction;
import nl.ica.asd.logic.domain.actions.OrderAction;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@actionType")
@JsonSubTypes({
    @Type(value = DeliveryAction.class, name = "DeliveryAction"),
    @Type(value = OrderAction.class, name = "OrderAction")
})
public abstract class Action implements Comparable<Action> {

  private final int amount;

  private final UUID sender;

  private final UUID receiver;

  @JsonCreator
  public Action(
      @JsonProperty("amount") Integer amount,
      @JsonProperty("sender") UUID sender,
      @JsonProperty("receiver") UUID receiver) {
    this.amount = amount;
    this.sender = sender;
    this.receiver = receiver;
  }

  @JsonGetter("amount")
  public int getAmount() {
    return amount;
  }

  @JsonGetter("sender")
  public UUID getSender() {
    return sender;
  }

  @JsonGetter("receiver")
  public UUID getReceiver() {
    return receiver;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }

    final Action action = (Action) obj;
    return sender == action.sender && receiver == action.receiver && amount == action.amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(String.format("%s-%s", sender, receiver));
  }

  public int compareTo(Action action) {
    return this.equals(action) ? 0 : -1;
  }

}
