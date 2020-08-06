package net.donotturnoff.simpledoc.browser.styling;

import net.donotturnoff.simpledoc.browser.element.Element;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class SDMLStyler {
    private static final Set<String> inheritableStyles = new HashSet<>();

    static {
        inheritableStyles.add("font_family");
        inheritableStyles.add("font_size");
        inheritableStyles.add("font_style");
        inheritableStyles.add("underline");
        inheritableStyles.add("colour");
        inheritableStyles.add("background_colour");
        inheritableStyles.add("cursor");
        inheritableStyles.add("bullet_style");
    }

    public void style(Element root) {
        style(root, new Style());
    }
    
    public void style(Element element, Style parentStyle) {
        Style style = new Style();
        try {
            Field field = element.getClass().getField("defaultStyle");
            if (Style.class.isAssignableFrom(field.getType())) {
                style = new Style((Style) field.get(null));
            } else {
                throw new NoSuchFieldException();
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        for (String s: inheritableStyles) {
            if (parentStyle.containsRule(s)) {
                style.set(s, parentStyle.get(s), parentStyle.isCustomRule(s));
            }
        }
        element.setStyle(style);
        for (Element child: element.getChildren()) {
            style(child, style);
        }
    }
}
