package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.FileUtils;
import net.donotturnoff.simpledoc.util.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

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
        JPanel infoPanel = new JPanel();

        if (r == null) {
            infoPanel.add(new JLabel("Loading failed"));
        } else {
            Map<String, String> headers = r.getHeaders();

            infoPanel.setBackground(Color.WHITE);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

            String[][] headersForTable = new String[headers.size() + 3][2];

            headersForTable[0][0] = "URL";
            headersForTable[0][1] = url.toString();
            headersForTable[1][0] = "Protocol";
            headersForTable[1][1] = r.getProtocol();
            headersForTable[2][0] = "Status";
            headersForTable[2][1] = r.getStatus().toString();
            int i = 3;
            for (String key : headers.keySet()) {
                headersForTable[i][0] = key;
                headersForTable[i++][1] = headers.get(key);
            }

            JTable headersTable = new JTable(headersForTable, new String[]{"", ""});
            headersTable.getTableHeader().setReorderingAllowed(false);
            headersTable.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));

            TableColumnModel tcm = headersTable.getColumnModel();
            tcm.getColumn(0).setPreferredWidth(100);
            tcm.getColumn(1).setPreferredWidth(700);

            String type = r.getHeaders().get("type");
            String genericType = type.split("/")[0];
            JComponent sourceComponent;
            if (genericType.equals("text")) {
                sourceComponent = Page.getTextPanel(r.getBody(), page.getBrowser().getConfig());
            } else if (genericType.equals("image")) {
                try {
                    sourceComponent = Page.getImagePanel(r.getBody());
                } catch (IOException e) {
                    sourceComponent = new JLabel("Failed to display image: " + e.getMessage());
                }
            } else {
                sourceComponent = new JLabel("Resource of type " + type);
            }
            sourceComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
            sourceComponent.setBorder(new EmptyBorder(5, 5, 5, 5));

            JScrollPane scrollPane = new JScrollPane(sourceComponent);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
            scrollPane.getVerticalScrollBar().setBlockIncrement(40);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(40);

            infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            infoPanel.add(headersTable);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(scrollPane);
        }

        tabbedPane.addTab(FileUtils.getFilename(url), infoPanel);
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
