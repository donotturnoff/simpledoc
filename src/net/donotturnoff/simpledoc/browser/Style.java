package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.parsing.StyleSource;
import net.donotturnoff.simpledoc.browser.parsing.StyleValue;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    // key -> source -> index -> priority -> value
    private final ConcurrentMap<String, StyleValue> properties;

    public Style() {
        this.properties = new ConcurrentHashMap<>();
    }

    public Style(Style existingStyle) {
        this.properties = new ConcurrentHashMap<>(existingStyle.properties);
    }

    public Style(ConcurrentMap<String, StyleValue> properties) {
        this.properties = properties;
    }

    public void set(String key, String value) {
        set(key, value, StyleSource.INLINE, 0, 0);
    }

    public void set(String key, String value, StyleSource source, int index, int priority) {
        StyleValue sv = new StyleValue(value, source, index, priority);
        set(key, sv);
    }

    public void set(String key, StyleValue sv) {
        if (sv.compareTo(properties.get(key)) > 0) {
            properties.put(key, sv);
        }
    }

    public void setDefault(String key, String value) {
        setDefault(key, value, 0);
    }

    public void setDefault(String key, String value, int priority) {
        set(key, value, StyleSource.DEFAULT, 0, priority);
    }

    public void setAll(Style style) {
        for (Map.Entry<String, StyleValue> rule: style.properties.entrySet()) {
            String key = rule.getKey();
            StyleValue sv = rule.getValue();
            set(key, sv);
        }
    }

    public void setAll(Style style, int priority) {
        for (Map.Entry<String, StyleValue> rule: style.properties.entrySet()) {
            String key = rule.getKey();
            StyleValue sv = rule.getValue();
            sv.setPriority(priority);
            set(key, sv);
        }
    }

    public String get(String key) {
        StyleValue v = properties.get(key);
        return v.getValue();
    }

    public String getOrDefault(String key, String defaultValue) {
        StyleValue sv = properties.get(key);
        if (sv != null) {
            return sv.getValue();
        } else {
            return defaultValue;
        }
    }

    private ConcurrentMap<String, StyleValue> getInheritable(Map<String, StyleValue> map) {
        return map.entrySet().stream().filter(m -> INHERITABLE.contains(m.getKey())).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Style getInheritable() {
        ConcurrentMap<String, StyleValue> inheritableProperties = getInheritable(properties);
        return new Style(inheritableProperties);
    }

    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Style{");
        for (String key: properties.keySet()) {
            s.append(key).append("=\"").append(properties.get(key).getValue()).append("\"");
        }
        s.append("}");
        return s.toString();
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
