package net.donotturnoff.simpledoc.browser;

import java.util.List;
import java.util.Map;

abstract class Element {
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
