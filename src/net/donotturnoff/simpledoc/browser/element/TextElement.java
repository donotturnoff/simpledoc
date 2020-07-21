package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
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

        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(colour);
        parentPanel.add(label);
    }

    @Override
    protected String toString(String indent) {
        return indent + text;
    }
}
