package net.donotturnoff.simpledoc.browser.element;

import javax.swing.*;
import java.util.*;

public abstract class Element {
    private static final Set<String> tags = Set.of("doc", "head", "body", "title", "res", "style", "base", "header",
            "nav", "main", "article", "section", "footer", "div", "blockquote", "h1", "h2", "h3", "h4", "h5", "h6", "p",
            "code", "cite", "span", "q", "link", "br", "hr", "math", "ul", "ol", "li", "dl", "dt", "dd", "table", "thead",
            "tbody", "tfoot", "tr", "th", "td", "img", "audio", "video"

    );
    private static final Set<String> generalAttrs = Set.of("id", "class", "description", "title", "accesskey", "lang",
            "dir", "hidden", "style", "tabindex");
    private static final Map<String, Set<String>> tagSpecificAttrs = new HashMap<>();
    private static final Set<String> allAttrs = new HashSet<>();

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
    }

    public static boolean isLegalTag(String tag) {
        return tags.contains(tag);
    }

    public static boolean isLegalAttribute(String attr) {
        return allAttrs.contains(attr);
    }

    public static boolean isLegalAttribute(String tag, String attr) {
        return tagSpecificAttrs.get(tag).contains(attr);
    }

    protected String name;
    protected Map<String, String> attributes;
    protected List<Element> children;

    public List<Element> getChildren() {
        return children;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getName() {
        return name;
    }

    protected abstract void draw(JPanel panel);

    public void render(JPanel panel) {
        draw(panel);
        for (Element c: children) {
            c.render(panel);
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    private String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append(name);
        if (attributes.size() > 0) {
            sb.append("(");
            for (String k: attributes.keySet()) {
                String v = attributes.get(k);
                sb.append(k);
                sb.append("=\"");
                sb.append(v);
                sb.append("\", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (children.size() > 0) {
            sb.append(" {\n");
            for (Element c: children) {
                sb.append(indent);
                sb.append(c.toString(indent+"\t"));
                sb.append("\n");
            }
            sb.append(indent);
            sb.append("}");
        }
        return sb.toString();
    }
}
