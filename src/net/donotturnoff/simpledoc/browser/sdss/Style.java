package net.donotturnoff.simpledoc.browser.sdss;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

// TODO: consistent naming scheme (properties, rules, styles, etc.)
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

        // Named colours conform to CSS (https://developer.mozilla.org/en-US/docs/Web/CSS/color_value#Color_keywords)
        // Level 1
        COLOUR_MAP.put("black", Color.decode("#000000"));
        COLOUR_MAP.put("silver", Color.decode("#c0c0c0"));
        COLOUR_MAP.put("gray", Color.decode("#808080"));
        COLOUR_MAP.put("white", Color.decode("#ffffff"));
        COLOUR_MAP.put("maroon", Color.decode("#800000"));
        COLOUR_MAP.put("red", Color.decode("#ff0000"));
        COLOUR_MAP.put("purple", Color.decode("#800080"));
        COLOUR_MAP.put("fuchsia", Color.decode("#ff00ff"));
        COLOUR_MAP.put("green", Color.decode("#008000"));
        COLOUR_MAP.put("lime", Color.decode("#00ff00"));
        COLOUR_MAP.put("olive", Color.decode("#808000"));
        COLOUR_MAP.put("yellow", Color.decode("#ffff00"));
        COLOUR_MAP.put("navy", Color.decode("#000080"));
        COLOUR_MAP.put("blue", Color.decode("#0000ff"));
        COLOUR_MAP.put("teal", Color.decode("#008080"));
        COLOUR_MAP.put("aqua", Color.decode("#00ffff"));
        // Level 2
        COLOUR_MAP.put("orange", Color.decode("#ffa500"));
        // Level 3
        COLOUR_MAP.put("aliceblue", Color.decode("#f0f8ff"));
        COLOUR_MAP.put("antiquewhite", Color.decode("#faebd7"));
        COLOUR_MAP.put("aquamarine", Color.decode("#7fffd4"));
        COLOUR_MAP.put("azure", Color.decode("#f0ffff"));
        COLOUR_MAP.put("beige", Color.decode("#f5f5dc"));
        COLOUR_MAP.put("bisque", Color.decode("#ffe4c4"));
        COLOUR_MAP.put("blanchedalmond", Color.decode("#ffebcd"));
        COLOUR_MAP.put("blueviolet", Color.decode("#8a2be2"));
        COLOUR_MAP.put("brown", Color.decode("#a52a2a"));
        COLOUR_MAP.put("burlywood", Color.decode("#deb887"));
        COLOUR_MAP.put("cadetblue", Color.decode("#5f9ea0"));
        COLOUR_MAP.put("chartreuse", Color.decode("#7fff00"));
        COLOUR_MAP.put("chocolate", Color.decode("#d2691e"));
        COLOUR_MAP.put("coral", Color.decode("#ff7f50"));
        COLOUR_MAP.put("cornflowerblue", Color.decode("#6495ed"));
        COLOUR_MAP.put("cornsilk", Color.decode("#fff8dc"));
        COLOUR_MAP.put("crimson", Color.decode("#dc143c"));
        COLOUR_MAP.put("cyan", Color.decode("#00ffff"));
        COLOUR_MAP.put("darkblue", Color.decode("#00008b"));
        COLOUR_MAP.put("darkcyan", Color.decode("#008b8b"));
        COLOUR_MAP.put("darkgoldenrod", Color.decode("#b8860b"));
        COLOUR_MAP.put("darkgray", Color.decode("#a9a9a9"));
        COLOUR_MAP.put("darkgreen", Color.decode("#006400"));
        COLOUR_MAP.put("darkgrey", Color.decode("#a9a9a9"));
        COLOUR_MAP.put("darkkhaki", Color.decode("#bdb76b"));
        COLOUR_MAP.put("darkmagenta", Color.decode("#8b008b"));
        COLOUR_MAP.put("darkolivegreen", Color.decode("#556b2f"));
        COLOUR_MAP.put("darkorange", Color.decode("#ff8c00"));
        COLOUR_MAP.put("darkorchid", Color.decode("#9932cc"));
        COLOUR_MAP.put("darkred", Color.decode("#8b0000"));
        COLOUR_MAP.put("darksalmon", Color.decode("#e9967a"));
        COLOUR_MAP.put("darkseagreen", Color.decode("#8fbc8f"));
        COLOUR_MAP.put("darkslateblue", Color.decode("#483d8b"));
        COLOUR_MAP.put("darkslategray", Color.decode("#2f4f4f"));
        COLOUR_MAP.put("darkslategrey", Color.decode("#2f4f4f"));
        COLOUR_MAP.put("darkturquoise", Color.decode("#00ced1"));
        COLOUR_MAP.put("darkviolet", Color.decode("#9400d3"));
        COLOUR_MAP.put("deeppink", Color.decode("#ff1493"));
        COLOUR_MAP.put("deepskyblue", Color.decode("#00bfff"));
        COLOUR_MAP.put("dimgray", Color.decode("#696969"));
        COLOUR_MAP.put("dimgrey", Color.decode("#696969"));
        COLOUR_MAP.put("dodgerblue", Color.decode("#1e90ff"));
        COLOUR_MAP.put("firebrick", Color.decode("#b22222"));
        COLOUR_MAP.put("floralwhite", Color.decode("#fffaf0"));
        COLOUR_MAP.put("forestgreen", Color.decode("#228b22"));
        COLOUR_MAP.put("gainsboro", Color.decode("#dcdcdc"));
        COLOUR_MAP.put("ghostwhite", Color.decode("#f8f8ff"));
        COLOUR_MAP.put("gold", Color.decode("#ffd700"));
        COLOUR_MAP.put("goldenrod", Color.decode("#daa520"));
        COLOUR_MAP.put("greenyellow", Color.decode("#adff2f"));
        COLOUR_MAP.put("grey", Color.decode("#808080"));
        COLOUR_MAP.put("honeydew", Color.decode("#f0fff0"));
        COLOUR_MAP.put("hotpink", Color.decode("#ff69b4"));
        COLOUR_MAP.put("indianred", Color.decode("#cd5c5c"));
        COLOUR_MAP.put("indigo", Color.decode("#4b0082"));
        COLOUR_MAP.put("ivory", Color.decode("#fffff0"));
        COLOUR_MAP.put("khaki", Color.decode("#f0e68c"));
        COLOUR_MAP.put("lavender", Color.decode("#e6e6fa"));
        COLOUR_MAP.put("lavenderblush", Color.decode("#fff0f5"));
        COLOUR_MAP.put("lawngreen", Color.decode("#7cfc00"));
        COLOUR_MAP.put("lemonchiffon", Color.decode("#fffacd"));
        COLOUR_MAP.put("lightblue", Color.decode("#add8e6"));
        COLOUR_MAP.put("lightcoral", Color.decode("#f08080"));
        COLOUR_MAP.put("lightcyan", Color.decode("#e0ffff"));
        COLOUR_MAP.put("lightgoldenrodyellow", Color.decode("#fafad2"));
        COLOUR_MAP.put("lightgray", Color.decode("#d3d3d3"));
        COLOUR_MAP.put("lightgreen", Color.decode("#90ee90"));
        COLOUR_MAP.put("lightgrey", Color.decode("#d3d3d3"));
        COLOUR_MAP.put("lightpink", Color.decode("#ffb6c1"));
        COLOUR_MAP.put("lightsalmon", Color.decode("#ffa07a"));
        COLOUR_MAP.put("lightseagreen", Color.decode("#20b2aa"));
        COLOUR_MAP.put("lightskyblue", Color.decode("#87cefa"));
        COLOUR_MAP.put("lightslategray", Color.decode("#778899"));
        COLOUR_MAP.put("lightslategrey", Color.decode("#778899"));
        COLOUR_MAP.put("lightsteelblue", Color.decode("#b0c4de"));
        COLOUR_MAP.put("lightyellow", Color.decode("#ffffe0"));
        COLOUR_MAP.put("limegreen", Color.decode("#32cd32"));
        COLOUR_MAP.put("linen", Color.decode("#faf0e6"));
        COLOUR_MAP.put("magenta", Color.decode("#ff00ff"));
        COLOUR_MAP.put("mediumaquamarine", Color.decode("#66cdaa"));
        COLOUR_MAP.put("mediumblue", Color.decode("#0000cd"));
        COLOUR_MAP.put("mediumorchid", Color.decode("#ba55d3"));
        COLOUR_MAP.put("mediumpurple", Color.decode("#9370db"));
        COLOUR_MAP.put("mediumseagreen", Color.decode("#3cb371"));
        COLOUR_MAP.put("mediumslateblue", Color.decode("#7b68ee"));
        COLOUR_MAP.put("mediumspringgreen", Color.decode("#00fa9a"));
        COLOUR_MAP.put("mediumturquoise", Color.decode("#48d1cc"));
        COLOUR_MAP.put("mediumvioletred", Color.decode("#c71585"));
        COLOUR_MAP.put("midnightblue", Color.decode("#191970"));
        COLOUR_MAP.put("mintcream", Color.decode("#f5fffa"));
        COLOUR_MAP.put("mistyrose", Color.decode("#ffe4e1"));
        COLOUR_MAP.put("moccasin", Color.decode("#ffe4b5"));
        COLOUR_MAP.put("navajowhite", Color.decode("#ffdead"));
        COLOUR_MAP.put("oldlace", Color.decode("#fdf5e6"));
        COLOUR_MAP.put("olivedrab", Color.decode("#6b8e23"));
        COLOUR_MAP.put("orangered", Color.decode("#ff4500"));
        COLOUR_MAP.put("orchid", Color.decode("#da70d6"));
        COLOUR_MAP.put("palegoldenrod", Color.decode("#eee8aa"));
        COLOUR_MAP.put("palegreen", Color.decode("#98fb98"));
        COLOUR_MAP.put("paleturquoise", Color.decode("#afeeee"));
        COLOUR_MAP.put("palevioletred", Color.decode("#db7093"));
        COLOUR_MAP.put("papayawhip", Color.decode("#ffefd5"));
        COLOUR_MAP.put("peachpuff", Color.decode("#ffdab9"));
        COLOUR_MAP.put("peru", Color.decode("#cd853f"));
        COLOUR_MAP.put("pink", Color.decode("#ffc0cb"));
        COLOUR_MAP.put("plum", Color.decode("#dda0dd"));
        COLOUR_MAP.put("powderblue", Color.decode("#b0e0e6"));
        COLOUR_MAP.put("rosybrown", Color.decode("#bc8f8f"));
        COLOUR_MAP.put("royalblue", Color.decode("#4169e1"));
        COLOUR_MAP.put("saddlebrown", Color.decode("#8b4513"));
        COLOUR_MAP.put("salmon", Color.decode("#fa8072"));
        COLOUR_MAP.put("sandybrown", Color.decode("#f4a460"));
        COLOUR_MAP.put("seagreen", Color.decode("#2e8b57"));
        COLOUR_MAP.put("seashell", Color.decode("#fff5ee"));
        COLOUR_MAP.put("sienna", Color.decode("#a0522d"));
        COLOUR_MAP.put("skyblue", Color.decode("#87ceeb"));
        COLOUR_MAP.put("slateblue", Color.decode("#6a5acd"));
        COLOUR_MAP.put("slategray", Color.decode("#708090"));
        COLOUR_MAP.put("slategrey", Color.decode("#708090"));
        COLOUR_MAP.put("snow", Color.decode("#fffafa"));
        COLOUR_MAP.put("springgreen", Color.decode("#00ff7f"));
        COLOUR_MAP.put("steelblue", Color.decode("#4682b4"));
        COLOUR_MAP.put("tan", Color.decode("#d2b48c"));
        COLOUR_MAP.put("thistle", Color.decode("#d8bfd8"));
        COLOUR_MAP.put("tomato", Color.decode("#ff6347"));
        COLOUR_MAP.put("turquoise", Color.decode("#40e0d0"));
        COLOUR_MAP.put("violet", Color.decode("#ee82ee"));
        COLOUR_MAP.put("wheat", Color.decode("#f5deb3"));
        COLOUR_MAP.put("whitesmoke", Color.decode("#f5f5f5"));
        COLOUR_MAP.put("yellowgreen", Color.decode("#9acd32"));
        // Level 4
        COLOUR_MAP.put("rebeccapurple", Color.decode("#663399"));
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
        // Override styles with lower precedence
        if (sv.compareTo(properties.get(key)) > 0) {
            properties.put(key, sv);
        }
    }

    // Convenience method
    public void setDefault(String key, String value) {
        set(key, value, StyleSource.DEFAULT, 0, 0);
    }

    // Add all rules from style to this one, obeying precedence
    public void setAll(Style style) {
        for (Map.Entry<String, StyleValue> rule: style.properties.entrySet()) {
            String key = rule.getKey();
            StyleValue sv = rule.getValue();
            set(key, sv);
        }
    }

    // Add all rules from style to this one, obeying precedence but with a fixed priority assigned to each one
    // Used when cascading styles, as pre-existing priorities are to be ignored in favour of distance back in the tree
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
        return "Style" + properties;
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
            return COLOUR_MAP.getOrDefault(get("background_colour"), Color.WHITE);
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
            return COLOUR_MAP.getOrDefault(get("border_colour"), Color.BLACK);
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
            return COLOUR_MAP.getOrDefault(get("colour"), Color.BLACK);
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
