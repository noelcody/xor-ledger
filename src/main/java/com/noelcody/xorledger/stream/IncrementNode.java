package com.noelcody.xorledger.stream;

import com.noelcody.xorledger.Acker;
import com.noelcody.xorledger.ByteId;
import com.noelcody.xorledger.Message;

public class IncrementNode implements Node<Integer> {

  private final Node<Integer> nextNode;
  private final Acker acker;

  public IncrementNode(Acker acker, Node<Integer> nextNode) {
    this.nextNode = nextNode;
    this.acker = acker;
  }

  /**
   * 1. Add this node to the ack ledger. If this node fails, ledger will contain this node's entry.
   * 2. Clear the upstream node from the ack ledger.
   * 3. Update message and pass to next node.
   */
  @Override
  public void process(Message<Integer> message) {
    ByteId sourceId = message.getSourceId();
    System.out.println(String.format("IncrementNode received message with value: %s", message.getValue()));

    ByteId nodeId = ByteId.generate();
    acker.updateEntry(sourceId, nodeId);
    acker.updateEntry(sourceId, message.getUpstreamId());

    Message<Integer> output = new Message<>(sourceId, nodeId, message.getValue() + 1);
    System.out.println(String.format("IncrementNode emitting new message with value: %s", message.getValue()));
    nextNode.process(output);
  }
}
