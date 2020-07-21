package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class AudioElement extends Element {
    public AudioElement(Map<String, String> attributes, List<Element> children) {
        super("audio", attributes, children);
    }

    @Override
    protected void draw(Page page) {

    }
}
