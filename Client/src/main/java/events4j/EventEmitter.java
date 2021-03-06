package events4j;


import events4j.argument.Arguments;
import events4j.listener.EventListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * The Event Emitter is the basic class to register and emit events.
 * All class that need to emit events should extend this class.
 * This class is thread-safe.
 */
public class EventEmitter {

    private ConcurrentMap<String, ConcurrentLinkedQueue<EventListener>> listeners = new ConcurrentHashMap<>();

    /**
     * Register a listener for the event
     * @param event    event name
     * @param listener function to be executed on emit event
     */
    public EventEmitter on(String event, EventListener listener) {
        ConcurrentLinkedQueue<EventListener> eventListeners = listeners.get(event);
        if(eventListeners == null){
            eventListeners = new ConcurrentLinkedQueue<>();
            listeners.put(event, eventListeners);
        }

        if(!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
        return this;
    }

    /**
     * Register one time listener for an event name.
     * @param event    event name
     * @param listener function to be executed on emit event
     */
    public EventEmitter once(String event, EventListener listener) {
        return on(event, new EventListener() {
            @Override
            public void call(Arguments args) {
                listener.call(args);
                removeListener(event,this);
            }
        });
    }

    /**
     * Execute listener associated with event name.
     * @param event the event name to execute listener
     */
    public EventEmitter emit(String event,Arguments args) {
        ConcurrentLinkedQueue<EventListener> eventListeners = listeners.get(event);
        if(eventListeners != null){
            eventListeners.forEach(l -> l.call(args));
        } return this;
    }

    /**
     * Execute listener associated with event name.
     * @param event the event name to execute listener
     */
    public EventEmitter emit(String event) {
        return emit(event,null);
    }

    /**
     * Removes listener from the listener for the event
     * @param event name of event
     * @param listener listener to remove
     * @return EventEmitter
     */
    public EventEmitter removeListener(String event, EventListener listener) {
        ConcurrentLinkedQueue<EventListener> eventListeners = listeners.get(event);
        if(eventListeners != null){
            eventListeners.remove(listener);
        }
        return this;
    }

    /**
     * Return the number of listener for the events
     * @param event event name
     */
    public int listenerCount(String event) {
        ConcurrentLinkedQueue<EventListener> eventListeners = listeners.get(event);
        if(eventListeners == null) {
            return 0;
        }
        return eventListeners.size();
    }

    public void removeListeners(String event) {
        this.listeners.entrySet().removeIf(entry -> entry.getKey().equals(event));
    }

}
