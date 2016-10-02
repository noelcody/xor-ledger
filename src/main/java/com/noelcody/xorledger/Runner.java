package com.noelcody.xorledger;

import com.noelcody.xorledger.stream.FailNode;
import com.noelcody.xorledger.stream.IncrementNode;
import com.noelcody.xorledger.stream.Sink;
import com.noelcody.xorledger.stream.Source;

public class Runner {

  public static void main(String[] args) {
    Acker acker = new Acker();

    Source successfulSource = buildSuccessfulStream(acker);
    successfulSource.emit();

    Source unsuccessfulSource = buildUnsuccessfulStream(acker);
    unsuccessfulSource.emit();
  }

  /**
   * Stream that runs successfully without retries.
   */
  private static Source buildSuccessfulStream(Acker acker) {
    Sink sink = new Sink(acker);
    IncrementNode secondNode = new IncrementNode(acker, sink);
    IncrementNode firstNode = new IncrementNode(acker, secondNode);
    return new Source(firstNode, acker);
  }

  /**
   * Stream that will fail and retry repeatedly.
   */
  private static Source buildUnsuccessfulStream(Acker acker) {
    FailNode failNode = new FailNode();
    return new Source(failNode, acker);
  }
}
