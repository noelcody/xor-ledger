# Summary

Toy implementation of Storm's XOR-based [reliability algorithm](http://storm.apache.org/releases/current/Guaranteeing-message-processing.html).

Uses a small id (8 bytes) to guarantee at-least-once message processing through a pipeline with many steps, each of which might fail at any time.

# How it works

Messages are processed by a "stream" with the structure: [Source -> Chain of Worker Nodes -> Sink].

At-least-once processing is guaranteed as follows:

* Source creates a new message with a unique 64-bit ID. The Source ID is stored as the "ledger" value for this message.
* Retry timer started. If the ledger isn't cleared within a given timeout, the Source creates a new message with same value and a new ID.
* Node A receives the message and XORs the ledger with its own randomly generated ID. Node A's ID is now in the ledger.
* Node A XORs the ledger with the Source ID. The Source ID is now removed from the ledger.
* Node A passes the message to Node B, which XORs the ledger with its own ID (enters Node B's ID into the ledger) and then XORs the ledger with Node A's ID (removes Node A's ID from the ledger).
* Repeat through a chain of Nodes. Message finally hits a Sink, which removes the last Node's ID from the ledger.
* The ledger's bytes are now all 0 and the ledger is cleared.

There's a very, very small chance that any given Node's randomly generated ID will be the exact value needed to prematurely clear the ledger. The chance of this happening is so small that it's considered a non-issue.