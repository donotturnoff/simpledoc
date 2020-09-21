package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.FileUtils;
import net.donotturnoff.simpledoc.util.Response;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class ResourceViewer {

    private final Page page;
    private final JFrame gui;
    private final JTabbedPane tabbedPane;

    public ResourceViewer(Page page) {
        this.page = page;

        gui = new JFrame("Resource viewer");
        gui.setMinimumSize(new Dimension(800, 600));
        gui.setIconImage(SDTPBrowser.ICON);

        updateTitle();

        tabbedPane = new JTabbedPane();

        gui.add(tabbedPane);
    }

    public void addResource(URL url, Response r) {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(Color.WHITE);
        String type = r.getHeaders().get("type");
        String genericType = type.split("/")[0];
        JComponent sourceComponent;
        if (genericType.equals("text")) {
            sourceComponent = Page.getTextPanel(r.getBody());
        } else if (genericType.equals("image")) {
            try {
                sourceComponent = Page.getImagePanel(r.getBody());
            } catch (IOException e) {
                sourceComponent = new JLabel("Failed to display image: " + e.getMessage());
            }
        } else {
            sourceComponent = new JLabel("Resource of type " + type);
        }
        infoPanel.add(sourceComponent);

        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(40);
        tabbedPane.addTab(FileUtils.getFilename(url), scrollPane);
    }

    public void toggle() {
        gui.setVisible(!gui.isVisible());
    }

    public void hide() {
        gui.setVisible(false);
    }

    public void updateTitle() {
        gui.setTitle("Resource viewer for " + page.getUrl());
    }

    public void clear() {
        tabbedPane.removeAll();
    }
}
