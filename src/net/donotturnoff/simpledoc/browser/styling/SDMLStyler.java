package net.donotturnoff.simpledoc.browser.styling;

import net.donotturnoff.simpledoc.browser.element.Element;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SDMLStyler {
    private static final Set<String> inheritableStyles = new HashSet<>();

    static {
        inheritableStyles.add("font_family");
        inheritableStyles.add("font_size");
        inheritableStyles.add("font_style");
        inheritableStyles.add("text_decoration");
        inheritableStyles.add("colour");
        inheritableStyles.add("background_colour");
        inheritableStyles.add("cursor");
    }

    public void style(Element root) {
        style(root, Map.of());
    }
    
    private void style(Element element, Map<String, String> parentStyle) {
        Map<String, String> style = new HashMap<>();
        for (String s: inheritableStyles) {
            if (parentStyle.containsKey(s)) {
                style.put(s, parentStyle.get(s));
            }
        }
        Map<String, String> defaultStyle = new HashMap<>();
        try {
            Field field = element.getClass().getField("defaultStyle");
            if (Map.class.isAssignableFrom(field.getType())) {
                Object defaultStyleObject = field.get(null);
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
