package com.noelcody.xorledger;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import java.time.Duration;
import java.util.Map;

public class Acker {

  private static final Duration RETRY_INTERVAL = Duration.ofSeconds(5);

  private Map<ByteId, LedgerEntry> ledgerMap = Maps.newHashMap();

  public void createEntry(Runnable retryFn, ByteId sourceId) {
    System.out.println(String.format("Acker created entry in ledger with id: %s", sourceId));
    startRetryTimer(sourceId);
    ledgerMap.put(sourceId, new LedgerEntry(retryFn, sourceId));
  }

  public void updateEntry(ByteId sourceId, ByteId newId) {
    LedgerEntry ledgerEntry = ledgerMap.get(sourceId);
    System.out.println(String.format("Acker adding new id to ledger: sourceId %s, newId %s", sourceId, newId));
    ledgerEntry.addToLedger(newId);

    if (ledgerEntry.isComplete()) {
      clearFromLedger(sourceId);
      System.out.println(String.format("Acker removed message from ledger: sourceId %s", sourceId));
    }
  }

  private void clearFromLedger(ByteId sourceId) {
    ledgerMap.remove(sourceId);
  }

  /**
   * At set interval, check if message has completed (has been removed from ledger).
   * If message is still in ledger, clear current entry and trigger retry (new emit with new id).
   */
  private void startRetryTimer(ByteId sourceId) {
    new Thread(() -> {
      try {
        Thread.sleep(RETRY_INTERVAL.toMillis());
      } catch (InterruptedException e) {
        Throwables.propagate(e);
      }

      System.out.println(String.format("Checking status of message with sourceId: %s", sourceId));
      boolean messageNotComplete = ledgerMap.containsKey(sourceId);
      if (messageNotComplete) {
        System.out.println(String.format(
            "Found message processing not complete (╯°□°）╯︵ ┻━┻. "
            + "Clearing from register and triggering retry for sourceId: %s",
            sourceId));
        Runnable retryFn = ledgerMap.get(sourceId).getRetryFn();
        clearFromLedger(sourceId);
        retryFn.run();
      } else {
        System.out.println(String.format("Found message processing complete \\( ＾∇＾)/ for sourceId: %s", sourceId));
      }
    }).start();
  }

  private class LedgerEntry {

    private final Runnable retryFn;
    private ByteId ledger;

    LedgerEntry(Runnable retryFn, ByteId ledger) {
      this.retryFn = retryFn;
      this.ledger = ledger;
    }

    /**
     * XOR to insert into or (if already inserted) remove from ledgerMap.
     */
    void addToLedger(ByteId id) {
      ledger = ledger.xor(id);
      System.out.println(String.format("Ledger is now: %s", ledger));
    }

    private boolean isComplete() {
      return ledger.isZero();
    }

    Runnable getRetryFn() {
      return retryFn;
    }
  }
}
