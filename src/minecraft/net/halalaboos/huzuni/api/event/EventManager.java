package net.halalaboos.huzuni.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows for events to be fired and dispatched to multiple listeners at a given time.
 * */
public final class EventManager <T> {

	private final Map<Class<?>, List<EventData<T>>> listeners = new HashMap<Class<?>, List<EventManager<T>.EventData<T>>>();
	
	public EventManager() {
		
	}
	
	/**
	 * Registers the specified listener for all event types.
	 * */
	public void addListener(T listener) {
		for (Method method : listener.getClass().getMethods()) {
			if (method.isAnnotationPresent(EventMethod.class) && method.getParameterTypes().length == 1) {
				Class<?> event = method.getParameterTypes()[0];
				if (!containsListener(event, listener)) {
					if (listeners.containsKey(event)) {
						listeners.get(event).add(new EventData<T>(listener, method));
					} else {
						List<EventData<T>> listeners = new ArrayList<EventManager<T>.EventData<T>>();
						listeners.add(new EventData<T>(listener, method));
						this.listeners.put(event, listeners);
					}
				}
			}
		}
	}
	
	/**
	 * Unregisters the specified listener from all event types.
	 * */
	public void removeListener(T listener) {
		for (Class<?> eventType : this.listeners.keySet()) {
			List<EventData<T>> listeners = this.listeners.get(eventType);
			for (int i = 0; i < listeners.size(); i++) {
				EventData<T> eventData = listeners.get(i);
				if (eventData.listener == listener)
					listeners.remove(eventData);
			}
		}
	}
	
	/**
	 * @return True if the listener is already registered under the event type specified
	 * */
	public boolean containsListener(Class<?> event, T listener) {
		if (listeners.containsKey(event)) {
			for (EventData<T> eventData : listeners.get(event)) {
				if (eventData.listener == listener && event.isAssignableFrom(eventData.method.getParameterTypes()[0])) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return True if the listener is already registered.
	 * */
	public boolean containsListener(T listener) {
		for (Class<?> eventType : this.listeners.keySet()) {
			if (containsListener(eventType, listener)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Invokes all event listeners listening to the given event.
	 * @return The event object
	 * */
	public <E> E invoke(E event) {
		if (listeners.containsKey(event.getClass())) {
			List<EventData<T>> listeners = this.listeners.get(event.getClass());
			for (int i = 0; i < listeners.size(); i++) {
				EventData<T> eventData = listeners.get(i);
				try {
					eventData.method.invoke(eventData.listener, event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return event;
	}
	
	/**
	 * Assigns methods as event methods; allowing for the {@link EventManager} to recognize it as such.
	 * */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface EventMethod {
		
	}
	
	/**
	 * Contains an instance of the listener along with it's event {@link Method}
	 * */
	@SuppressWarnings("hiding")
	private class EventData <T> {
		T listener;
		Method method;
		
		EventData(T listener, Method method) {
			this.listener = listener;
			this.method = method;
		}
	}
}
