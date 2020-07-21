package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TextElement extends Element {
    private final static Map<String, Integer> fontStyleMap = Map.of("plain", Font.PLAIN, "bold", Font.BOLD, "italic", Font.ITALIC);

    private final String text;

    public TextElement(String text) {
        super("text", Map.of(), List.of());
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        Map<String, String> style = getStyle();
        String fontFamily = style.getOrDefault("font_family", Font.SERIF);
        int fontStyle = fontStyleMap.getOrDefault(style.getOrDefault("font_style", "plain"), Font.PLAIN);
        int fontSize = Integer.parseInt(style.getOrDefault("font_size", "12"));

        Font font = new Font(fontFamily, fontStyle, fontSize);
        JLabel label = new JLabel(text);
        label.setFont(font);
        parentPanel.add(label);
    }

    @Override
    protected String toString(String indent) {
        return indent + text;
    }
}
