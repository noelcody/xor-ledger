package com.noelcody.xorledger.stream;

import com.noelcody.xorledger.Acker;
import com.noelcody.xorledger.Message;

public class Sink implements Node<Integer> {

  private final Acker acker;

  public Sink(Acker acker) {
    this.acker = acker;
  }

  /**
   * "Sink" this message by removing upstream node from the ledger.
   */
  @Override
  public void process(Message<Integer> message) {
    System.out.println(String.format("Sink received message with final value: %d", message.getValue()));
    acker.updateEntry(message.getSourceId(), message.getUpstreamId());
  }
}
