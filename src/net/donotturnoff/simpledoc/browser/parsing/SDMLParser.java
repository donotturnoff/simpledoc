package net.donotturnoff.simpledoc.browser.parsing;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.element.TextElement;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SDMLParser {
    private final static Parser p;
    private final static Grammar grammar;

    static {
        NonTerminal ntStart = new NonTerminal("start");
        NonTerminal ntBlock = new NonTerminal("block");
        NonTerminal ntElement = new NonTerminal("element");
        NonTerminal ntElementList = new NonTerminal("element_list");
        NonTerminal ntAttrs = new NonTerminal("attrs");
        NonTerminal ntAttrList = new NonTerminal("attr_list");
        NonTerminal ntAttr = new NonTerminal("attr");

        Terminal<Void> tLparen = new Terminal<>("LPAREN");
        Terminal<Void> tRparen = new Terminal<>("RPAREN");
        Terminal<Void> tLbrace = new Terminal<>("LBRACE");
        Terminal<Void> tRbrace = new Terminal<>("RBRACE");
        Terminal<Void> tComma = new Terminal<>("COMMA");
        Terminal<Void> tEquals = new Terminal<>("EQUALS");
        Terminal<Void> tIdent = new Terminal<>("IDENT");
        Terminal<Void> tString = new Terminal<>("STRING");

        Set<Symbol<?>> symbols = new HashSet<>();

        symbols.add(tLparen);
        symbols.add(tRparen);
        symbols.add(tLbrace);
        symbols.add(tRbrace);
        symbols.add(tComma);
        symbols.add(tEquals);
        symbols.add(tIdent);
        symbols.add(tString);

        symbols.add(ntStart);
        symbols.add(ntBlock);
        symbols.add(ntElement);
        symbols.add(ntElementList);
        symbols.add(ntAttrList);
        symbols.add(ntAttr);
        symbols.add(ntAttrs);

        Set<Production> productions = new HashSet<>();
        productions.add(new Production(ntStart, List.of(ntElement)));
        productions.add(new Production(ntBlock, List.of(tLbrace, ntElementList, tRbrace)));
        productions.add(new Production(ntBlock, List.of(tLbrace, tRbrace)));
        productions.add(new Production(ntElementList, List.of(ntElement)));
        productions.add(new Production(ntElementList, List.of(ntElement, ntElementList)));
        productions.add(new Production(ntElement, List.of(tString)));
        productions.add(new Production(ntElement, List.of(tIdent)));
        productions.add(new Production(ntElement, List.of(tIdent, ntBlock)));
        productions.add(new Production(ntElement, List.of(tIdent, ntAttrs)));
        productions.add(new Production(ntElement, List.of(tIdent, ntAttrs, ntBlock)));
        productions.add(new Production(ntAttrs, List.of(tLparen, ntAttrList, tRparen)));
        productions.add(new Production(ntAttrs, List.of(tLparen, tRparen)));
        productions.add(new Production(ntAttrList, List.of(ntAttr)));
        productions.add(new Production(ntAttrList, List.of(ntAttr, tComma, ntAttrList)));
        productions.add(new Production(ntAttr, List.of(tIdent, tEquals, tString)));

        grammar = new Grammar(symbols, productions, ntStart);

        p = new Parser(grammar);
    }
    
    private final Page page;
    private final Set<String> ids;
    
    public SDMLParser(Page page) {
        this.page = page;
        ids = new HashSet<>();
    }
    
    public Element parse(Queue<Terminal<?>> tokens) throws ParsingException {
        Node t = p.parse(tokens);
        return start(t);
    }

    private Element start(Node n) throws ParsingException {
        return element(n.getChildren().get(0));
    }

    private Element element(Node n) throws ParsingException {
        List<Node> c = n.getChildren();
        Symbol<?> fst = c.get(0).getSymbol();
        String tag = (String) ((Terminal<?>) fst).getToken().getValue();
        if (fst.getName().equals("STRING")) {
            return new TextElement(page, tag);
        }
        if (!Element.isLegalTag(tag)) {
            throw new ParsingException("Illegal tag: " + tag);
        }
        Class<? extends Element> tagClass = Element.getTagClass(tag);
        Map<String, String> attrs = attributes(n, tag);
        List<Element> children = children(n);
        try {
            Element e = tagClass.getConstructor(Page.class, Map.class, List.class).newInstance(page, attrs, children);
            page.addElement(e);
            return e;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            page.warning("Failed to construct " + tag + " object");
            return null;
        }
    }

    private Map<String, String> attributes(Node n, String tag) throws ParsingException {
        List<Node> c = n.getChildren();
        Node a;
        if (c.size() == 1 || !(a = c.get(1)).getSymbol().getName().equals("attrs") || a.getChildren().size() == 2) { //No attributes or children OR no attributes OR empty attributes
            return Map.of();
        } else {
            Map<String, String> attrs = new HashMap<>();
            Node attrList = a.getChildren().get(1);
            while (attrList.getChildren().size() == 3) {
                Node attr = attrList.getChildren().get(0);
                attrList = attrList.getChildren().get(2);
                addAttribute(attr, attrs, tag);
            }
            Node attr = attrList.getChildren().get(0);
            addAttribute(attr, attrs, tag);
            return attrs;
        }
    }

    private void addAttribute(Node attr, Map<String, String> attrs, String tag) throws ParsingException {
        String key = (String) ((Terminal<?>) attr.getChildren().get(0).getSymbol()).getToken().getValue();
        String value = (String) ((Terminal<?>) attr.getChildren().get(2).getSymbol()).getToken().getValue();
        if (!Element.isLegalAttribute(tag, key)) {
            throw new ParsingException("Illegal attribute for tag " + tag + ": " + key);
        } else {
            if (key.equals("id")) {
                if (ids.contains(value)) {
                    throw new ParsingException("Duplicate id detected: " + value);
                } else {
                    ids.add(value);
                }
            }
            attrs.put(key, value);
        }
    }

    private List<Element> children(Node n) throws ParsingException {
        List<Node> c = n.getChildren();
        Node b;
        //No attributes or children OR attributes and no children OR empty children
        if (c.size() == 1) {
            return List.of();
        } else if (c.size() == 2) {
            b = c.get(1);
            if (!b.getSymbol().getName().equals("block") || b.getChildren().size() == 2) {
                return List.of();
            }
        } else {
            b = c.get(2);
            if (b.getChildren().size() == 2) {
                return List.of();
            }
        }
        List<Element> children = new ArrayList<>();
        Node elementList = b.getChildren().get(1);
        while (elementList.getChildren().size() == 2) {
            Element element = element(elementList.getChildren().get(0));
            elementList = elementList.getChildren().get(1);
            children.add(element);
        }
        Element element = element(elementList.getChildren().get(0));
        children.add(element);
        return children;
    }
}