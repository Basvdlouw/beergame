package nl.ica.asd.logic.domain.builders;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import nl.ica.asd.logic.domain.Action;

public final class ActionBuilder {

  private int amount;
  private UUID sender;
  private UUID receiver;

  private Class<? extends Action> instance;

  private ActionBuilder() {
  }

  public static ActionBuilder anAction() {
    return new ActionBuilder();
  }

  public static <T extends Action> ActionBuilder fromBaseAction(T action) {
    return new ActionBuilder()
        .setInstance(action.getClass())
        .withAmount(action.getAmount())
        .withReceiver(action.getReceiver())
        .withSender(action.getSender());
  }

  public <T extends Action> ActionBuilder setInstance(Class<T> instance) {
    this.instance = instance;
    return this;
  }

  public ActionBuilder withAmount(int amount) {
    this.amount = amount;
    return this;
  }

  public ActionBuilder withSender(UUID sender) {
    this.sender = sender;
    return this;
  }

  public ActionBuilder withReceiver(UUID receiver) {
    this.receiver = receiver;
    return this;
  }

  public <T extends Action> T build() {
    try {
      return (T) Class.forName(instance.getName())
          .getConstructor(Integer.class, UUID.class, UUID.class)
          .newInstance(amount, sender, receiver);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
      return null;
    }
  }
}
