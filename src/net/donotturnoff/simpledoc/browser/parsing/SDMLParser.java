package net.donotturnoff.simpledoc.browser.parsing;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.element.TextElement;
import net.donotturnoff.simpledoc.browser.lexing.Token;
import net.donotturnoff.simpledoc.browser.lexing.TokenType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SDMLParser {
    private final Page page;
    private final List<Token<?>> tokens;
    private int i;
    private Token<?> nextToken;
    private boolean end;

    public SDMLParser(Page page, List<Token<?>> tokens) {
        this.page = page;
        this.tokens = tokens;
        this.i = 0;
        getToken();
    }

    private void getToken() {
        if (i == tokens.size()) {
            end = true;
        } else {
            nextToken = tokens.get(i);
            i++;
            end = false;
        }
    }

    public Element parse() throws ParsingException {
        if (nextToken.getType() != TokenType.IDENT || !nextToken.getValue().equals("doc")) {
            throw new ParsingException("Expected element \"doc\" at token " + nextToken);
        }
        return parseElement();
    }

    private Element parseElement() throws ParsingException {
        if (nextToken.getType() != TokenType.STRING && !(nextToken.getType() == TokenType.IDENT && Element.isLegalTag((String) nextToken.getValue()))) {
            throw new ParsingException("Expected element or string at token " + nextToken);
        }

        if (nextToken.getType() == TokenType.IDENT) {
            String tag = (String) nextToken.getValue();
            getToken();
            Map<String, String> attrs = parseAttributes(tag);
            List<Element> children = parseChildren();
            Class<? extends Element> tagClass = Element.getTagClass(tag);

            if (tagClass == null) {
                page.displayWarning("Element " + tag + " not implemented");
                return null;
            }
            try {
                return tagClass.getConstructor(Page.class, Map.class, List.class).newInstance(page, attrs, children);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                page.displayWarning("Failed to construct " + tag + " object");
                return null;
            }
        } else {
            Element textElement = new TextElement(page, (String) nextToken.getValue());
            getToken();
            return textElement;
        }
    }

    private Map<String, String> parseAttributes(String tag) throws ParsingException {
        Map<String, String> attributes = new HashMap<>();
        if (nextToken.getType() == TokenType.LPAREN) {
            getToken();
            do {
                if (end) {
                    throw new ParsingException("Unexpected EOF in attribute list");
                }
                if (nextToken.getType() == TokenType.IDENT) {
                    if (Element.isLegalAttribute(tag, (String) nextToken.getValue())) {
                        Map.Entry<String, String> attr = parseAttribute();
                        attributes.put(attr.getKey(), attr.getValue());
                    } else {
                        page.displayWarning("Illegal attribute: \"" + nextToken.getValue() + "\"");
                    }
                } else {
                    throw new ParsingException("Expected attribute at token " + nextToken);
                }
            } while (nextToken.getType() != TokenType.RPAREN);
            getToken();
        }
        return attributes;
    }

    private Map.Entry<String, String> parseAttribute() throws ParsingException {
        Token<?> keyToken = nextToken;
        getToken();
        Token<?> equalsToken = nextToken;
        getToken();
        Token<?> valueToken = nextToken;
        getToken();
        if (keyToken.getType() == TokenType.IDENT && equalsToken.getType() == TokenType.EQUALS && valueToken.getType() == TokenType.STRING) {
            String key = (String) keyToken.getValue();
            String value = (String) valueToken.getValue();
            return new AbstractMap.SimpleEntry<>(key, value);
        } else {
            throw new ParsingException("Expected attribute format key=\"value\" at token " + nextToken);
        }
    }

    private List<Element> parseChildren() throws ParsingException {
        List<Element> children = new ArrayList<>();
        if (nextToken.getType() == TokenType.LBRACE) {
            getToken();
            do {
                if (end) {
                    throw new ParsingException("Unexpected EOF in element body");
                }
                Element child = parseElement();
                if (child != null) {
                    children.add(child);
                }
            } while (nextToken.getType() != TokenType.RBRACE);
            getToken();
        }
        return children;
    }
}
