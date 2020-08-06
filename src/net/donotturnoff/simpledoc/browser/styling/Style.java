package net.donotturnoff.simpledoc.browser.styling;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Style {
    private final Set<String> customRules;
    private final Map<String, String> rules;

    public Style() {
        this.customRules = new HashSet<>();
        this.rules = new HashMap<>();
    }

    public Style(Style existingStyle) {
        this(existingStyle, new HashSet<>());
    }

    public Style(Style existingStyle, Set<String> existingCustomStyles) {
        this.rules = new HashMap<>(existingStyle.rules);
        this.customRules = new HashSet<>(existingCustomStyles);
    }

    public void set(String key, String value) {
        set(key, value, true);
    }

    public void set(String key, String value, boolean isCustomRule) {
        if (isCustomRule || !isCustomRule(key))
            rules.put(key, value);{
            customRules.add(key);
        }
    }

    public void setAll(Style style) {
        for (Map.Entry<String, String> rule: style.rules.entrySet()) {
            String key = rule.getKey();
            String value = rule.getValue();
            set(key, value, style.isCustomRule(key));
        }
    }

    public String get(String key) {
        return rules.get(key);
    }

    public String getOrDefault(String key, String defaultValue) {
        return rules.getOrDefault(key, defaultValue);
    }

    public boolean containsRule(String key) {
        return rules.containsKey(key);
    }

    public boolean isCustomRule(String key) {
        return customRules.contains(key);
    }

    @Override
    public String toString() {
        return "Style{rules=" + rules + ", customRules=" + customRules + "}";
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
        switch (rules.getOrDefault("cursor", "default")) {
            case "pointer": type = Cursor.HAND_CURSOR; break;
            case "text": type = Cursor.TEXT_CURSOR; break;
            case "crosshair": type = Cursor.CROSSHAIR_CURSOR; break;
            default: type = Cursor.DEFAULT_CURSOR; break;
        }
        return new Cursor(type);
    }

    public Color getBackgroundColour() {
        try {
            return Color.decode(rules.getOrDefault("background_colour", "#FFFFFF"));
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }

    public int getPaddingTop() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("padding_top", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getPaddingLeft() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("padding_left", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getPaddingBottom() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("padding_bottom", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getPaddingRight() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("padding_right", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Border getPadding() {
        return BorderFactory.createEmptyBorder(getPaddingTop(), getPaddingLeft(), getPaddingBottom(), getPaddingRight());
    }

    public int getBorderTopWidth() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("border_top_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getBorderLeftWidth() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("border_left_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getBorderBottomWidth() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("border_bottom_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getBorderRightWidth() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("border_right_width", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Color getBorderColour() {
        try {
            return Color.decode(rules.getOrDefault("border_colour", "#000000"));
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }

    public Border getBorder() {
        return BorderFactory.createMatteBorder(getBorderTopWidth(), getBorderLeftWidth(), getBorderBottomWidth(), getBorderRightWidth(), getBorderColour());
    }

    public int getMarginTop() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("margin_top", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getMarginLeft() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("margin_left", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getMarginBottom() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("margin_bottom", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getMarginRight() {
        try {
            return (int) Double.parseDouble(rules.getOrDefault("margin_right", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Border getMargin() {
        return BorderFactory.createEmptyBorder(getMarginTop(), getMarginLeft(), getMarginBottom(), getMarginRight());
    }

    public String getFontFamily() {
        return rules.getOrDefault("font_family", Font.SERIF);
    }

    public int getFontStyle() {
        switch (rules.getOrDefault("font_style", "plain")) {
            case "italic": return Font.ITALIC;
            case "bold": return Font.BOLD;
            default: return Font.PLAIN;
        }
    }

    public int getFontSize() {
        try {
            int fontSize = (int) Double.parseDouble(rules.getOrDefault("font_size", "12"));
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
            return Color.decode(rules.getOrDefault("colour", "#000000"));
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }

    public int getUnderline() {
        if ("single".equals(rules.getOrDefault("underline", "none"))) {
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
        switch (rules.getOrDefault("bullet_style", "default")) {
            case "circle": return "\u25E6";
            case "triangle": return "\u2023";
            case "dash": return "\u2043";
            case "disc":
            default: return "\u2022";
        }
    }
}
