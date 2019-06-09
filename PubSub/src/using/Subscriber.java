package using;

import pubsub.OnMessage;
import pubsub.Message;

class Subscriber<T> {
    int id;
    public Subscriber(int id) {
        this.id = id;
    }

    @OnMessage
    private void onMessage(Message<T> message) {
        System.out.printf("%d:%s%n", id, message.get());
    }
}