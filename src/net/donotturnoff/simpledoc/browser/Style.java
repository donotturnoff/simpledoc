package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Style {
    private static final Set<String> INHERITABLE = new HashSet<>();
    private static final Map<String, Color> COLOUR_MAP = new HashMap<>();

    static {
        INHERITABLE.add("font_family");
        INHERITABLE.add("font_size");
        INHERITABLE.add("font_style");
        INHERITABLE.add("underline");
        INHERITABLE.add("colour");
        INHERITABLE.add("background_colour");
        INHERITABLE.add("cursor");
        INHERITABLE.add("bullet_style");

        COLOUR_MAP.put("black", Color.BLACK);
        COLOUR_MAP.put("dark_grey", Color.DARK_GRAY);
        COLOUR_MAP.put("grey", Color.GRAY);
        COLOUR_MAP.put("light_grey", Color.LIGHT_GRAY);
        COLOUR_MAP.put("white", Color.WHITE);
        COLOUR_MAP.put("red", Color.RED);
        COLOUR_MAP.put("orange", Color.ORANGE);
        COLOUR_MAP.put("yellow", Color.YELLOW);
        COLOUR_MAP.put("green", Color.GREEN);
        COLOUR_MAP.put("cyan", Color.CYAN);
        COLOUR_MAP.put("blue", Color.BLUE);
        COLOUR_MAP.put("magenta", Color.MAGENTA);
        COLOUR_MAP.put("pink", Color.PINK);
    }

    private final Map<String, String> defaultProperties;
    private final Map<String, Integer> defaultPriorities;
    private final Map<String, String> properties;
    private final Map<String, Integer> priorities;

    public Style() {
        this.defaultProperties = new HashMap<>();
        this.defaultPriorities = new HashMap<>();
        this.properties = new HashMap<>();
        this.priorities = new HashMap<>();
    }

    public Style(Style existingStyle) {
        this.defaultProperties = new HashMap<>(existingStyle.defaultProperties);
        this.defaultPriorities = new HashMap<>(existingStyle.defaultPriorities);
        this.properties = new HashMap<>(existingStyle.properties);
        this.priorities = new HashMap<>(existingStyle.priorities);
    }

    public Style(Map<String, String> properties, Map<String, Integer> priorities, Map<String, String> defaultProperties, Map<String, Integer> defaultPriorities) {
        this.properties = properties;
        this.priorities = priorities;
        this.defaultProperties = defaultProperties;
        this.defaultPriorities = defaultPriorities;
    }

    public void set(String key, String value) {
        set(key, value, 0);
    }

    public void set(String key, String value, int priority) {
        if (priority >= priorities.getOrDefault(key, Integer.MIN_VALUE)) {
            properties.put(key, value);
            priorities.put(key, priority);
        }
    }

    public void setDefault(String key, String value) {
        setDefault(key, value, 0);
    }

    public void setDefault(String key, String value, int priority) {
        if (priority >= defaultPriorities.getOrDefault(key, Integer.MIN_VALUE)) {
            defaultProperties.put(key, value);
            defaultPriorities.put(key, priority);
        }
    }

    public void setAll(Style style) {
        for (Map.Entry<String, String> rule: style.properties.entrySet()) {
            String key = rule.getKey();
            String value = rule.getValue();
            int priority = style.priorities.get(key);
            set(key, value, priority);
        }
        for (Map.Entry<String, String> rule: style.defaultProperties.entrySet()) {
            String key = rule.getKey();
            String value = rule.getValue();
            setDefault(key, value);
        }
    }

    public void setAll(Style style, int priority) {
        for (Map.Entry<String, String> rule: style.properties.entrySet()) {
            String key = rule.getKey();
            String value = rule.getValue();
            set(key, value, priority);
        }
        for (Map.Entry<String, String> rule: style.defaultProperties.entrySet()) {
            String key = rule.getKey();
            String value = rule.getValue();
            setDefault(key, value, priority);
        }
    }

    public String get(String key) {
        String v = properties.get(key);
        if (v != null) {
            return v;
        } else {
            return defaultProperties.get(key);
        }
    }

    public String getOrDefault(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultProperties.getOrDefault(key, defaultValue));
    }

    private <T> Map<String, T> getInheritable(Map<String, T> map) {
        return map.entrySet().stream().filter(m -> INHERITABLE.contains(m.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Style getInheritable() {
        Map<String, String> inheritableProperties = getInheritable(properties);
        Map<String, Integer> inheritablePriorities = getInheritable(priorities);
        Map<String, String> inheritableDefaultProperties = getInheritable(defaultProperties);
        Map<String, Integer> inheritableDefaultPriorities = getInheritable(defaultPriorities);
        return new Style(inheritableProperties, inheritablePriorities, inheritableDefaultProperties, inheritableDefaultPriorities);
    }

    public boolean containsProperty(String key) {
        return properties.containsKey(key) || defaultProperties.containsKey(key);
    }

    @Override
    public String toString() {
        return "Style{properties=" + properties + ", priorities=" + priorities + ", defaultProperties=" + defaultProperties + ", defaultPriorities=" + defaultPriorities + "}";
    }

    public LayoutManager getLayoutManager(JComponent component) {
        LayoutManager layout;
        String layoutName = getOrDefault("layout", "flow");
        switch (layoutName) {
            case "vbox": layout = new BoxLayout(component, BoxLayout.Y_AXIS); break;
            case "hbox": layout = new BoxLayout(component, BoxLayout.X_AXIS); break;
            default: layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        }
        return layout;
    }

    public Cursor getCursor() {
        int type;
        switch (getOrDefault("cursor", "default")) {
            case "pointer": type = Cursor.HAND_CURSOR; break;
            case "text": type = Cursor.TEXT_CURSOR; break;
            case "crosshair": type = Cursor.CROSSHAIR_CURSOR; break;
            default: type = Cursor.DEFAULT_CURSOR; break;
        }
        return new Cursor(type);
    }

    public Color getBackgroundColour() {
        try {
            return Color.decode(getOrDefault("background_colour", "#FFFFFF"));
        } catch (NumberFormatException e) {
            return COLOUR_MAP.get(get("background_colour"));
        }
    }

    public int getPaddingTop() {
        try {
            return (int) Double.parseDouble(getOrDefault("padding_top", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getPaddingLeft() {
        try {
            return (int) Double.parseDouble(getOrDefault("padding_left", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getPaddingBottom() {
        try {
            return (int) Double.parseDouble(getOrDefault("padding_bottom", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getPaddingRight() {
        try {
            return (int) Double.parseDouble(getOrDefault("padding_right", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Border getPadding() {
        return BorderFactory.createEmptyBorder(getPaddingTop(), getPaddingLeft(), getPaddingBottom(), getPaddingRight());
    }

    public int getBorderTopWidth() {
        try {
            return (int) Double.parseDouble(getOrDefault("border_top_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getBorderLeftWidth() {
        try {
            return (int) Double.parseDouble(getOrDefault("border_left_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getBorderBottomWidth() {
        try {
            return (int) Double.parseDouble(getOrDefault("border_bottom_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getBorderRightWidth() {
        try {
            return (int) Double.parseDouble(getOrDefault("border_right_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Color getBorderColour() {
        try {
            return Color.decode(getOrDefault("border_colour", "#000000"));
        } catch (NumberFormatException e) {
            return COLOUR_MAP.get(get("border_colour"));
        }
    }

    public Border getBorder() {
        return BorderFactory.createMatteBorder(getBorderTopWidth(), getBorderLeftWidth(), getBorderBottomWidth(), getBorderRightWidth(), getBorderColour());
    }

    public int getMarginTop() {
        try {
            return (int) Double.parseDouble(getOrDefault("margin_top", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getMarginLeft() {
        try {
            return (int) Double.parseDouble(getOrDefault("margin_left", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getMarginBottom() {
        try {
            return (int) Double.parseDouble(getOrDefault("margin_bottom", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getMarginRight() {
        try {
            return (int) Double.parseDouble(getOrDefault("margin_right", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Border getMargin() {
        return BorderFactory.createEmptyBorder(getMarginTop(), getMarginLeft(), getMarginBottom(), getMarginRight());
    }

    public String getFontFamily() {
        return getOrDefault("font_family", Font.SERIF);
    }

    public int getFontStyle() {
        switch (getOrDefault("font_style", "plain")) {
            case "italic": return Font.ITALIC;
            case "bold": return Font.BOLD;
            default: return Font.PLAIN;
        }
    }

    public int getFontSize() {
        try {
            int fontSize = (int) Double.parseDouble(getOrDefault("font_size", "12"));
            if (fontSize < 0) {
                throw new NumberFormatException();
            }
            return fontSize;
        } catch (NumberFormatException e) {
            return 12;
        }
    }

    public Color getColour() {
        try {
            return Color.decode(getOrDefault("colour", "#000000"));
        } catch (NumberFormatException e) {
            return COLOUR_MAP.get(get("colour"));
        }
    }

    public int getUnderline() {
        if ("single".equals(getOrDefault("underline", "none"))) {
            return TextAttribute.UNDERLINE_ON;
        }
        return -1;
    }

    public Font getFont() {
        String fontFamily = getFontFamily();
        int fontStyle = getFontStyle();
        int fontSize = getFontSize();
        Font font = new Font(fontFamily, fontStyle, fontSize);
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        int underline = getUnderline();
        attributes.put(TextAttribute.UNDERLINE, underline);
        font = font.deriveFont(attributes);
        return font;
    }

    public String getBulletText() {
        switch (getOrDefault("bullet_style", "default")) {
            case "circle": return "\u25E6";
            case "triangle": return "\u2023";
            case "dash": return "\u2043";
            case "disc":
            default: return "\u2022";
        }
    }
}
