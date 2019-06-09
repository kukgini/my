package using;

import pubsub.OnReceived;
import pubsub.Envelop;

class Subscriber<T> {
    int id;
    public Subscriber(int id) {
        this.id = id;
    }

    @OnReceived
    private void onReceived(Envelop<T> envelop) {
        System.out.printf("Subscriber[%d] received: %s%n", id, envelop.open());
    }
}