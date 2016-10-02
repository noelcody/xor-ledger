package com.noelcody.xorledger.stream;

import com.noelcody.xorledger.Message;

public class FailNode implements Node<Integer> {

  /**
   * Do nothing, causing Acker to never ack message as complete and trigger retry from source.
   */
  public void process(Message<Integer> message) {
    System.out.println(String.format("FailNode received message with sourceId: %s", message.getSourceId()));
  }
}
