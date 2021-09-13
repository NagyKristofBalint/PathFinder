package Window;

import java.util.EventObject;

public class CounterEvent extends EventObject {
    private final int value;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public CounterEvent(Object source, int value) {
        super(source);
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
