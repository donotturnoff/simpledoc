package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public abstract class Element implements MouseListener {
    private static final Set<String> tags = Set.of("doc", "head", "body", "title", "res", "style", "base", "header",
            "nav", "main", "article", "section", "footer", "div", "blockquote", "h1", "h2", "h3", "h4", "h5", "h6", "p",
            "code", "cite", "span", "q", "link", "br", "hr", "math", "ul", "ol", "li", "dl", "dt", "dd", "table", "thead",
            "tbody", "tfoot", "tr", "th", "td", "img", "audio", "video"

    );
    private static final Set<String> generalAttrs = Set.of("id", "class", "description", "title", "accesskey", "lang",
            "dir", "hidden", "style", "tabindex");
    private static final Map<String, Set<String>> tagSpecificAttrs = new HashMap<>();
    private static final Set<String> allAttrs = new HashSet<>();
    private static final Map<String, Class<? extends Element>> tagClasses = new HashMap<>();

    static {
        tagSpecificAttrs.put("doc", Set.of("version", "charset", "author", "description", "keywords"));
        tagSpecificAttrs.put("img", Set.of("src"));
        tagSpecificAttrs.put("audio", Set.of("src"));
        tagSpecificAttrs.put("video", Set.of("src"));
        tagSpecificAttrs.put("base", Set.of("href"));
        tagSpecificAttrs.put("link", Set.of("href"));
        tagSpecificAttrs.put("res", Set.of("src", "type", "rel"));

        allAttrs.addAll(generalAttrs);
        tagSpecificAttrs.values().forEach(allAttrs::addAll);

        String packageName = "net.donotturnoff.simpledoc.browser.element.";
        tags.forEach(t -> {
            String name = t.substring(0, 1).toUpperCase() + t.substring(1) + "Element";
            try {
                tagClasses.put(t, Class.forName(packageName + name).asSubclass(Element.class));
            } catch (ClassNotFoundException ignored) {
                //System.out.println("Class not found for tag: " + name);
            }
        });
    }

    public static boolean isLegalTag(String tag) {
        return tags.contains(tag);
    }

    public static boolean isLegalAttribute(String attr) {
        return allAttrs.contains(attr);
    }

    public static boolean isLegalAttribute(String tag, String attr) {
        return allAttrs.contains(attr) || (tagSpecificAttrs.containsKey(tag) && tagSpecificAttrs.get(tag).contains(attr));
    }

    public static Class<? extends Element> getTagClass(String tag) {
        return tagClasses.get(tag);
    }

    protected Page page;
    protected String name;
    protected Map<String, String> attributes;
    protected Element parent;
    protected List<Element> children;
    protected ElementState state;
    protected Style style;
    protected Map<ElementState, Style> styles;

    public Element(Page page, String name, Map<String, String> attributes, List<Element> children) {
        this.page = page;
        this.name = name;
        this.attributes = attributes;
        this.children = children;
        this.styles = new HashMap<>();
        this.parent = null;

        for (Element child: children) {
            child.setParent(this);
        }

        styles.put(ElementState.BASE, new Style());
        styles.put(ElementState.HOVER, new Style());
        styles.put(ElementState.ACTIVE, new Style());
        setState(ElementState.BASE);
    }

    private void setParent(Element parent) {
        this.parent = parent;
    }

    private void setState(ElementState state) {
        this.state = state;
        refreshCurrentStyle();
    }

    public void addStyles(ElementState e, Style toAdd) {
        if (e == ElementState.BASE) {
            for (Style s: styles.values()) {
                s.setAll(toAdd);
            }
        } else {
            styles.get(e).setAll(toAdd);
        }
        refreshCurrentStyle();
    }

    private void refreshCurrentStyle() {
        this.style = new Style(styles.get(state));
    }

    public void setStyle(ElementState state, Style style) {
        styles.put(state, style);
        if (state == this.state) {
            refreshCurrentStyle();
        }
    }

    public void setDefault(String key, String value) {
        for (ElementState s: ElementState.values()) {
            styles.get(s).setDefault(key, value);
        }
        refreshCurrentStyle();
    }

    public List<Element> getChildren() {
        return children;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getName() {
        return name;
    }

    public Style getStyle() {
        return style;
    }

    public Style getStyle(ElementState state) {
        return styles.get(state);
    }

    public Map<ElementState, Style> getStyles() {
        return styles;
    }

    public abstract void render(Page page, JPanel parentPanel);

    public void refresh(Page page) {
        for (Element c: children) {
            c.refresh(page);
        }
    }

    public void cascadeStyles() {
        cascadeStyles(-1);
    }

    public void cascadeStyles(int priority) {
        if (parent != null) {
            refreshCurrentStyle();
            style.setAll(parent.getStyle().getInheritable(), priority);
        }
        for (Element child: children) {
            child.cascadeStyles(priority - 1);
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    protected String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append(name);
        if (attributes.size() > 0) {
            sb.append("(");
            for (String k : attributes.keySet()) {
                String v = attributes.get(k);
                sb.append(k);
                sb.append("=\"");
                sb.append(v);
                sb.append("\" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (children.size() > 0) {
            sb.append(" {\n");
            for (Element c : children) {
                sb.append(c.toString(indent + " "));
                sb.append("\n");
            }
            sb.append(indent);
            sb.append("}");
        }
        return sb.toString();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setState(ElementState.ACTIVE);
        cascadeStyles();
        refresh(page);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setState(ElementState.BASE);
        cascadeStyles();
        refresh(page);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setState(ElementState.HOVER);
        cascadeStyles();
        refresh(page);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setState(ElementState.BASE);
        cascadeStyles();
        refresh(page);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
}
