package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class AudioElement extends Element {
    public AudioElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "audio", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {

    }
}
