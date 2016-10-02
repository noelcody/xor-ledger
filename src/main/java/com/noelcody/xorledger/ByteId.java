package com.noelcody.xorledger;

import java.util.Arrays;
import java.util.Random;

/**
 * Immutable wrapper around array of bytes.
 */
public class ByteId {

  private static final int ID_SIZE_BYTES = 8;

  private final byte[] id;
  private final boolean isZero;

  public ByteId(byte[] id) {
    this.id = id;
    this.isZero = allBytesZero(id);
  }

  public byte[] getId() {
    return id;
  }

  public boolean isZero() {
    return isZero;
  }

  /**
   * Generate a new ByteId with a random id array.
   */
  public static ByteId generate() {
    byte[] generatedId = new byte[ID_SIZE_BYTES];
    new Random().nextBytes(generatedId);
    return new ByteId(generatedId);
  }

  private static boolean allBytesZero(byte[] bytes) {
    for (byte b : bytes) {
      if (b != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * XOR with another id. Return new ByteId to preserve immutability.
   */
  public ByteId xor(ByteId xorWith) {
    byte[] newId = new byte[ID_SIZE_BYTES];
    byte[] xorWithBytes = xorWith.getId();

    for (int i = 0; i < id.length; i++) {
      newId[i] = (byte) (id[i] ^ xorWithBytes[i]);
    }

    return new ByteId(newId);
  }

  @Override
  public String toString() {
    return "ByteId{" +
           "id=" + Arrays.toString(id) +
           '}';
  }
}
