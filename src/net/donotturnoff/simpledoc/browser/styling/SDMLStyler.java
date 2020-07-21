package net.donotturnoff.simpledoc.browser.styling;

import net.donotturnoff.simpledoc.browser.element.Element;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SDMLStyler {
    public void style(Element root) {
        style(root, Map.of());
    }
    
    private void style(Element element, Map<String, String> parentStyle) {
        Map<String, String> style = new HashMap<>(parentStyle);
        Map<String, String> defaultStyle = new HashMap<>();
        try {
            Field field = element.getClass().getField("defaultStyle");
            if (Map.class.isAssignableFrom(field.getType())) {
                defaultStyle = (Map<String, String>) field.get(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        }
        style.putAll(defaultStyle);
        element.setStyle(style);
        for (Element child: element.getChildren()) {
            style(child, style);
        }
    }
}
