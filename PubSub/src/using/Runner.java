package using;

import pubsub.Event;
import pubsub.Message;

public class Runner {

	public static void main(String[] args) {
		Subscriber<String> subscriber1 = new Subscriber<>(1);
		Subscriber<String> subscriber2 = new Subscriber<>(2);

		Subscriber<String> subscriber3 = new Subscriber<>(3);
		Subscriber<String> subscriber4 = new Subscriber<>(4);
		
		Event.operation.subscribe("action#create", subscriber1);
		Event.operation.subscribe("action#create", subscriber2);

		Event.operation.subscribe("action#update", subscriber3);
		Event.operation.subscribe("action#delete", subscriber4);

		Message<String> message1 = new Message<>("Create Action");
		Message<String> message2 = new Message<>("Update Action");
		
		Event.operation.publish("action#create", message1);
		Event.operation.publish("action#update", message2);
	}
}
