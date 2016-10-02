package com.noelcody.xorledger.stream;

import com.noelcody.xorledger.Acker;
import com.noelcody.xorledger.ByteId;
import com.noelcody.xorledger.Message;

import java.util.Random;

public class Source {

  private final Acker acker;
  private final Node<Integer> nextNode;

  private final Random random = new Random();

  public Source(Node<Integer> nextNode, Acker acker) {
    this.nextNode = nextNode;
    this.acker = acker;
  }

  public void emit() {
    int emitValue = random.nextInt();
    emit(emitValue);
  }

  private void emit(int value) {
    ByteId sourceId = ByteId.generate();
    Message<Integer> emitMessage = new Message<>(sourceId, sourceId, value);

    System.out.println(String.format("Source preparing to emit new message with value: %s", emitMessage.getValue()));
    Runnable retryWithSameValueFn = () -> this.emit(value);
    acker.createEntry(retryWithSameValueFn, sourceId);

    System.out.println(String.format("Source emitting message with value: %s", emitMessage.getValue()));
    nextNode.process(emitMessage);
  }
}
