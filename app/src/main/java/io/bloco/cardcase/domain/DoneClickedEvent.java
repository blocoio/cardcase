package io.bloco.cardcase.domain;

/**
 * Created by Mtrs on 20.11.2016.
 */

public class DoneClickedEvent {

  public final String message;

  public DoneClickedEvent(String message) {
    this.message = message;
  }
}