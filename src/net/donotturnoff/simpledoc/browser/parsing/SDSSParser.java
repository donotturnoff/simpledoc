package net.donotturnoff.simpledoc.browser.parsing;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;
import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.element.ElementState;

import java.util.*;
import java.util.stream.Collectors;

public class SDSSParser {
    private final static Parser p;
    private final static Grammar grammar;

    static {
        NonTerminal ntStart = new NonTerminal("start");
        NonTerminal ntElementList = new NonTerminal("element_list");
        NonTerminal ntBlock = new NonTerminal("block");
        NonTerminal ntElement = new NonTerminal("element");
        NonTerminal ntSelector = new NonTerminal("selector");
        NonTerminal ntElemsAndProps = new NonTerminal("elems_and_props");
        NonTerminal ntProperty = new NonTerminal("property");
        NonTerminal ntAttrs = new NonTerminal("attrs");
        NonTerminal ntAttrList = new NonTerminal("attr_list");
        NonTerminal ntAttr = new NonTerminal("attr");
        NonTerminal ntState = new NonTerminal("state");

        Terminal<Void> tLparen = new Terminal<>("LPAREN");
        Terminal<Void> tRparen = new Terminal<>("RPAREN");
        Terminal<Void> tLangle = new Terminal<>("LANGLE");
        Terminal<Void> tRangle = new Terminal<>("RANGLE");
        Terminal<Void> tLbrace = new Terminal<>("LBRACE");
        Terminal<Void> tRbrace = new Terminal<>("RBRACE");
        Terminal<Void> tEquals = new Terminal<>("EQUALS");
        Terminal<Void> tComma = new Terminal<>("COMMA");
        Terminal<Void> tQMark = new Terminal<>("QMARK");
        Terminal<Void> tAsterisk = new Terminal<>("ASTERISK");
        Terminal<Void> tIdent = new Terminal<>("IDENT");
        Terminal<Void> tString = new Terminal<>("STRING");

        Set<Symbol<?>> symbols = new HashSet<>();

        symbols.add(tLparen);
        symbols.add(tRparen);
        symbols.add(tLangle);
        symbols.add(tRangle);
        symbols.add(tLbrace);
        symbols.add(tRbrace);
        symbols.add(tEquals);
        symbols.add(tComma);
        symbols.add(tQMark);
        symbols.add(tAsterisk);
        symbols.add(tIdent);
        symbols.add(tString);

        symbols.add(ntStart);
        symbols.add(ntElementList);
        symbols.add(ntBlock);
        symbols.add(ntElement);
        symbols.add(ntSelector);
        symbols.add(ntElemsAndProps);
        symbols.add(ntProperty);
        symbols.add(ntAttrs);
        symbols.add(ntAttrList);
        symbols.add(ntAttr);
        symbols.add(ntState);

        Set<Production> productions = new HashSet<>();
        productions.add(new Production(ntStart, List.of(ntElementList)));
        productions.add(new Production(ntElementList, List.of(ntElement)));
        productions.add(new Production(ntElementList, List.of(ntElement, ntElementList)));
        productions.add(new Production(ntElement, List.of(ntSelector, ntBlock)));
        productions.add(new Production(ntElement, List.of(ntSelector, ntAttrs, ntBlock)));
        productions.add(new Production(ntElement, List.of(ntSelector, tAsterisk, ntBlock)));
        productions.add(new Production(ntElement, List.of(ntSelector, ntAttrs, tAsterisk, ntBlock)));
        productions.add(new Production(ntElement, List.of(ntSelector, ntState, ntBlock)));
        productions.add(new Production(ntElement, List.of(ntSelector, ntAttrs, ntState, ntBlock)));
        productions.add(new Production(ntElement, List.of(ntSelector, tAsterisk, ntState, ntBlock)));
        productions.add(new Production(ntElement, List.of(ntSelector, ntAttrs, tAsterisk, ntState, ntBlock)));
        productions.add(new Production(ntSelector, List.of(tIdent)));
        productions.add(new Production(ntSelector, List.of(tQMark)));
        productions.add(new Production(ntAttrs, List.of(tLparen, ntAttrList, tRparen)));
        productions.add(new Production(ntAttrs, List.of(tLparen, tRparen)));
        productions.add(new Production(ntAttrList, List.of(ntAttr)));
        productions.add(new Production(ntAttrList, List.of(ntAttr, tComma, ntAttrList)));
        productions.add(new Production(ntAttr, List.of(tIdent, tEquals, tString)));
        productions.add(new Production(ntState, List.of(tLangle, tIdent, tRangle)));
        productions.add(new Production(ntBlock, List.of(tLbrace, ntElemsAndProps, tRbrace)));
        productions.add(new Production(ntElemsAndProps, List.of(ntElement)));
        productions.add(new Production(ntElemsAndProps, List.of(ntProperty)));
        productions.add(new Production(ntElemsAndProps, List.of(ntElement, ntElemsAndProps)));
        productions.add(new Production(ntElemsAndProps, List.of(ntProperty, ntElemsAndProps)));
        productions.add(new Production(ntProperty, List.of(tIdent, tEquals, tString)));

        grammar = new Grammar(symbols, productions, ntStart);
        p = new Parser(grammar);
    }

    private final Page page;

    public SDSSParser(Page page) {
        this.page = page;
    }

    public void parse(Queue<Terminal<?>> tokens) throws ParsingException {
        Node t = p.parse(tokens);
        elementList(t.getChildren().get(0));
    }

    private void elementList(Node elemList) throws ParsingException {
        while (elemList.getChildren().size() == 2) {
            Node elem = elemList.getChildren().get(0);
            elemList = elemList.getChildren().get(1);
            element(elem, page.getAllElements(), 1);
        }
        Node elem = elemList.getChildren().get(0);
        element(elem, page.getAllElements(), 1);
    }

    private void element(Node node, Set<Element> selectedElements, int priority) throws ParsingException {
        List<Node> c = node.getChildren();
        Node selector = c.get(0);
        Terminal<?> tagType = ((Terminal<?>) selector.getChildren().get(0).getSymbol());
        String type = tagType.getName();
        String tag;
        if (!type.equals("QMARK") && !Element.isLegalTag(tag = (String) tagType.getToken().getValue())) {
            throw new ParsingException("Illegal tag: " + tag);
        }
        if (type.equals("QMARK")) {
            priority += 1;
        } else {
            priority += 2;
        }
        Map<String, String> attrs = attributes(node, tagType);
        priority += attrs.size()*2;
        Node block = c.get(c.size()-1);
        boolean star = c.get(c.size()-2).getSymbol().getName().equals("ASTERISK") || (c.size() >= 3 && c.get(c.size()-3).getSymbol().getName().equals("ASTERISK"));
        ElementState state = state(node);
        if (state != ElementState.BASE) {
            priority += 2;
        }
        Set<Element> filteredElements = filter(selectedElements, tagType, attrs, star);
        applyStyles(filteredElements, block, state, priority);
    }

    private ElementState state(Node node) throws ParsingException {
        ElementState state = ElementState.BASE;
        List<Node> c = node.getChildren();
        Node stateNode = c.get(c.size() - 2);
        boolean isState = stateNode.getSymbol().getName().equals("state");
        if (isState) {
            String stateName = (String) ((Terminal<?>) stateNode.getChildren().get(1).getSymbol()).getToken().getValue();
            switch (stateName) {
                case "base": break;
                case "hover": state = ElementState.HOVER; break;
                case "active": state = ElementState.ACTIVE; break;
                default: throw new ParsingException("Illegal state: " + stateName);
            }
        }
        return state;
    }

    private Map<String, String> attributes(Node n, Terminal<?> tagType) throws ParsingException {
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
                addAttribute(attr, attrs, tagType);
            }
            Node attr = attrList.getChildren().get(0);
            addAttribute(attr, attrs, tagType);
            return attrs;
        }
    }

    private void addAttribute(Node attr, Map<String, String> attrs, Terminal<?> tagType) throws ParsingException {
        String key = (String) ((Terminal<?>) attr.getChildren().get(0).getSymbol()).getToken().getValue();
        String value = (String) ((Terminal<?>) attr.getChildren().get(2).getSymbol()).getToken().getValue();
        String tag;
        if (!tagType.getName().equals("QMARK") && !Element.isLegalAttribute(tag = (String) tagType.getToken().getValue(), key)) {
            throw new ParsingException("Illegal attribute for tag " + tag + ": " + key);
        } else {
            attrs.put(key, value);
        }
    }

    public Set<Element> filter(Set<Element> selectedElements, Terminal<?> tagType, Map<String, String> attrs, boolean star) {
        Set<Element> filteredElements;
        String tag = "?";
        if (!tagType.getName().equals("QMARK")) {
            tag = (String) tagType.getToken().getValue();
        }
        if (star) {
            filteredElements = allDescendentsOf(selectedElements, tag, attrs);
        } else {
            filteredElements = childrenOf(selectedElements, tag, attrs);
        }
        return filteredElements;
    }

    public void applyStyles(Set<Element> selectedElements, Node block, ElementState state, int priority) throws ParsingException {
        Style s = new Style();
        Node elemsAndProps = block.getChildren().get(1);
        while (elemsAndProps.getChildren().size() == 2) {
            Node elemOrProp = elemsAndProps.getChildren().get(0);
            if (elemOrProp.getSymbol().getName().equals("element")) {
                element(elemOrProp, selectedElements, priority);
            } else { // Handle property
                String key = (String) ((Terminal<?>) elemOrProp.getChildren().get(0).getSymbol()).getToken().getValue();
                String value = (String) ((Terminal<?>) elemOrProp.getChildren().get(2).getSymbol()).getToken().getValue();
                s.set(key, value, priority);
            }
            elemsAndProps = elemsAndProps.getChildren().get(1);
        }
        Node elemOrProp = elemsAndProps.getChildren().get(0);
        if (elemOrProp.getSymbol().getName().equals("element")) {
            element(elemOrProp, selectedElements, priority);
        } else { // Handle property
            String key = (String) ((Terminal<?>) elemOrProp.getChildren().get(0).getSymbol()).getToken().getValue();
            String value = (String) ((Terminal<?>) elemOrProp.getChildren().get(2).getSymbol()).getToken().getValue();
            s.set(key, value, priority);
        }
        for (Element e: selectedElements) {
            e.addStyles(state, s);
        }
    }

    private Set<Element> childrenOf(Set<Element> filteredElements, String tag, Map<String, String> attrs) {
        Set<Element> childrenOf = new HashSet<>();
        for (Element e: filteredElements) {
            childrenOf.addAll(e.getChildren().stream().filter(c -> !c.getName().equals("text")).collect(Collectors.toSet()));
        }
        if (!tag.equals("?")) {
            childrenOf = childrenOf.stream().filter(e -> e.getName().equals(tag)).collect(Collectors.toSet());
        }
        childrenOf = childrenOf.stream().filter(e -> e.getAttributes().entrySet().containsAll(attrs.entrySet())).collect(Collectors.toSet());
        return childrenOf;
    }

    private Set<Element> allDescendentsOf(Set<Element> filteredElements, String tag, Map<String, String> attrs) {
        Set<Element> thisAndAllDescendentsOf = new HashSet<>(childrenOf(filteredElements, tag, attrs));
        boolean altered;
        do {
            altered = thisAndAllDescendentsOf.addAll(childrenOf(thisAndAllDescendentsOf, tag, attrs));
        } while (altered);
        return thisAndAllDescendentsOf;
    }
}