package com.noelcody.xorledger.stream;

import com.noelcody.xorledger.Message;

public interface Node<T> {

  void process(Message<T> message);
}
