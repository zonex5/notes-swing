package xyz.toway.notes.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class EventBus {
    public static final EventBus INSTANCE = new EventBus();

    private final Map<String, List<Consumer<Object>>> listeners = new HashMap<>();

    private EventBus() {
    }

    private void on(String event, Consumer<Object> handler) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(handler);
    }

    private void off(String event, Consumer<Object> handler) {
        List<Consumer<Object>> list = listeners.get(event);
        if (list != null) list.remove(handler);
    }

    private void emit(String event, Object data) {
        List<Consumer<Object>> list = listeners.get(event);
        if (list != null) for (Consumer<Object> h : new ArrayList<>(list)) h.accept(data);
    }

    public static void reset() {
        INSTANCE.listeners.clear();
    }

    public static void emitEvent(String event, Object data) {
        INSTANCE.emit(event, data);
    }

    public static void onEvent(String event, Consumer<Object> handler) {
        INSTANCE.on(event, handler);
    }

    public static void offEvent(String event, Consumer<Object> handler) {
        INSTANCE.off(event, handler);
    }
}
