package using;

import pubsub.Event;
import pubsub.Envelop;

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

		Envelop<String> envelop1 = new Envelop<>("Create Action");
		Envelop<String> envelop2 = new Envelop<>("Update Action");
		Envelop<String> envelop3 = new Envelop<>("Delete Action");
		
		Event.operation.publish("action#create", envelop1);
		Event.operation.publish("action#update", envelop2);
		Event.operation.publish("action#delete", envelop3);
	}
}
