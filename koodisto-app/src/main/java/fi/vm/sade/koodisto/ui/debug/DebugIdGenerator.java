package fi.vm.sade.koodisto.ui.debug;

import java.util.Collection;

import com.vaadin.ui.Component;

public abstract class DebugIdGenerator {
    public static void ensureDebugId(Component component) {
        String debugId = component.getDebugId();
        if (debugId == null) {
            debugId = component.getClass().getName() + "@" + Integer.toHexString(component.hashCode());
            component.setDebugId(debugId);
        }
    }

    public static void ensureDebugId(Collection<Component> components) {
        for (Component component : components) {
            ensureDebugId(component);
        }
    }

    public static void ensureDebugId(Component... components) {
        for (Component component : components) {
            ensureDebugId(component);
        }
    }
}
