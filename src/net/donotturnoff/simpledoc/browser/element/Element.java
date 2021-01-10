package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;
import net.donotturnoff.simpledoc.browser.sdss.Style;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public abstract class Element implements MouseListener {
    private static final Set<String> elements = Set.of("doc", "head", "body", "title", "res", "style", "base", "header",
            "nav", "main", "article", "section", "footer", "div", "blockquote", "h1", "h2", "h3", "h4", "h5", "h6", "p",
            "code", "cite", "span", "q", "link", "br", "hr", "math", "ul", "ol", "li", "dl", "dt", "dd", "table", "thead",
            "tbody", "tfoot", "tr", "th", "td", "img", "audio", "video"

    );
    private static final Set<String> baseAttributes = Set.of("id", "class", "description", "title", "accesskey", "lang", "dir", "hidden", "style", "tabindex");
    private static final Set<String> allAttributes = Set.of("id", "class", "description", "title", "accesskey", "lang", "dir", "hidden", "style", "tabindex", "version", "charset", "author", "keywords", "src", "href", "rel", "type");
    private static final Map<String, Set<String>> tagSpecificAttributes = new HashMap<>();

    public static Element createElement(String tag, Page page, Map<String, String> attrs, List<Element> children, int index) throws SDMLException {
        switch (tag) {
            case "audio": return new AudioElement(page, attrs, children); 
            case "body": return new BodyElement(page, attrs, children); 
            case "code": return new CodeElement(page, attrs, children); 
            case "div": return new DivElement(page, attrs, children); 
            case "doc": return new DocElement(page, attrs, children); 
            case "footer": return new FooterElement(page, attrs, children); 
            case "h1": return new H1Element(page, attrs, children); 
            case "h2": return new H2Element(page, attrs, children); 
            case "h3": return new H3Element(page, attrs, children); 
            case "h4": return new H4Element(page, attrs, children); 
            case "h5": return new H5Element(page, attrs, children); 
            case "h6": return new H6Element(page, attrs, children); 
            case "head": return new HeadElement(page, attrs, children); 
            case "header": return new HeaderElement(page, attrs, children); 
            case "img": return new ImgElement(page, attrs, children); 
            case "li": return new LiElement(page, attrs, children); 
            case "link": return new LinkElement(page, attrs, children); 
            case "main": return new MainElement(page, attrs, children); 
            case "nav": return new NavElement(page, attrs, children); 
            case "p": return new PElement(page, attrs, children); 
            case "res": return new ResElement(page, attrs, children, index); 
            case "section": return new SectionElement(page, attrs, children); 
            case "style": return new StyleElement(page, attrs, children, index); 
            case "title": return new TitleElement(page, attrs, children); 
            case "ul": return new UlElement(page, attrs, children); 
            default: throw new SDMLException("Illegal element: " + tag);
        }
    }

    static {
        tagSpecificAttributes.put("doc", Set.of("version", "charset", "author", "keywords"));
        tagSpecificAttributes.put("img", Set.of("src"));
        tagSpecificAttributes.put("audio", Set.of("src"));
        tagSpecificAttributes.put("video", Set.of("src"));
        tagSpecificAttributes.put("base", Set.of("href"));
        tagSpecificAttributes.put("link", Set.of("href"));
        tagSpecificAttributes.put("res", Set.of("src", "type", "rel"));
    }

    public static boolean isLegalElement(String name) {
        return elements.contains(name);
    }

    public static boolean isLegalAttribute(String attr) {
        return allAttributes.contains(attr);
    }

    public static boolean isLegalAttribute(String tag, String attr) {
        return baseAttributes.contains(attr) || (tagSpecificAttributes.containsKey(tag) && tagSpecificAttributes.get(tag).contains(attr));
    }

    protected Page page;
    protected String name;
    protected Map<String, String> attributes;
    protected Element parent;
    protected List<Element> children;
    protected ElementState state;
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
    }

    // Called by SDSSParser to apply styles
    public void addStyles(ElementState e, Style toAdd) {
        // If styles apply to base state, they apply to all other states too
        if (e == ElementState.BASE) {
            for (Style s: styles.values()) {
                s.setAll(toAdd);
            }
        } else {
            styles.get(e).setAll(toAdd);
        }
    }

    public void setStyle(ElementState state, Style style) {
        styles.put(state, style);
    }

    public void setDefault(String key, String value) {
        // Apply default style to all states
        for (Style s: styles.values()) {
            s.setDefault(key, value);
        }
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
        return getStyle(state);
    }

    public Style getStyle(ElementState state) {
        return styles.get(state);
    }

    public Map<ElementState, Style> getStyles() {
        return styles;
    }

    public abstract void render(JPanel parentPanel);

    // Called on any update that may alter the page styling (initial page render, style application, element state change)
    public void refresh() {
        for (Element c: children) {
            c.refresh();
        }
    }

    // Priority starts at -1 to not interfere with directly-applied styles
    public void cascadeStyles() {
        cascadeStyles(-1);
    }

    // Makes children inherit inheritable styles, with priority decreasing for each level of inheritance
    public void cascadeStyles(int priority) {
        for (Element child: children) {
            child.getStyle().setAll(getStyle().getInheritable(), priority);
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
        refresh();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setState(ElementState.BASE);
        cascadeStyles();
        refresh();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setState(ElementState.HOVER);
        cascadeStyles();
        refresh();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setState(ElementState.BASE);
        cascadeStyles();
        refresh();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
}
