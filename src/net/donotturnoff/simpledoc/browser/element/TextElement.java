package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextElement extends Element {
    private final String text;

    public TextElement(Page page, String text) {
        super(page, "text", Map.of(), List.of());
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        Map<String, String> style = getStyle();
        String fontFamily = style.getOrDefault("font_family", Font.SERIF);
        int fontStyle = Element.fontStyleMap.getOrDefault(style.getOrDefault("font_style", "plain"), Font.PLAIN);
        int fontSize = Integer.parseInt(style.getOrDefault("font_size", "12"));
        Font font = new Font(fontFamily, fontStyle, fontSize);
        Color colour = Color.decode(style.getOrDefault("colour", "#000000"));
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        boolean underline = style.getOrDefault("text_decoration", "none").equals("underline");
        if (underline) {
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }
        font = font.deriveFont(attributes);

        int paddingTop = Integer.parseInt(style.getOrDefault("padding_top", "0"));
        int paddingLeft = Integer.parseInt(style.getOrDefault("padding_left", "0"));
        int paddingBottom = Integer.parseInt(style.getOrDefault("padding_bottom", "0"));
        int paddingRight = Integer.parseInt(style.getOrDefault("padding_right", "0"));
        Border padding = BorderFactory.createEmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight);

        int borderTopWidth = Integer.parseInt(style.getOrDefault("border_top_width", "0"));
        int borderLeftWidth = Integer.parseInt(style.getOrDefault("border_left_width", "0"));
        int borderBottomWidth = Integer.parseInt(style.getOrDefault("border_bottom_width", "0"));
        int borderRightWidth = Integer.parseInt(style.getOrDefault("border_right_width", "0"));
        Color borderColour = Color.decode(style.getOrDefault("border_colour", "#000000"));
        Border border = BorderFactory.createMatteBorder(borderTopWidth, borderLeftWidth, borderBottomWidth, borderRightWidth, borderColour);

        int marginTop = Integer.parseInt(style.getOrDefault("margin_top", "0"));
        int marginLeft = Integer.parseInt(style.getOrDefault("margin_left", "0"));
        int marginBottom = Integer.parseInt(style.getOrDefault("margin_bottom", "0"));
        int marginRight = Integer.parseInt(style.getOrDefault("margin_right", "0"));
        Border margin = BorderFactory.createEmptyBorder(marginTop, marginLeft, marginBottom, marginRight);

        CompoundBorder surroundings = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(margin, border), padding);

        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(colour);
        label.setBorder(surroundings);
        parentPanel.add(label);
    }

    @Override
    protected String toString(String indent) {
        return indent + text;
    }
}
