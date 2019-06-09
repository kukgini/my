package pubsub;

public class Envelop<T> {
    private T message;

    public Envelop(T message) {
        this.message = message;
    }
    
    public T open() {
    	return message;
    }
}