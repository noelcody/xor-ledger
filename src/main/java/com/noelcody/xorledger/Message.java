package com.noelcody.xorledger;

/**
 * Data to be passed between nodes.
 * Contains original sourceId and a link to the upstream node that generated the message.
 */
public class Message<T> {

  private ByteId sourceId;
  private ByteId upstreamId;
  private T value;

  public Message(ByteId sourceId, ByteId upstreamId, T value) {
    this.sourceId = sourceId;
    this.upstreamId = upstreamId;
    this.value = value;
  }

  public ByteId getSourceId() {
    return sourceId;
  }

  public ByteId getUpstreamId() {
    return upstreamId;
  }

  public T getValue() {
    return value;
  }
}
