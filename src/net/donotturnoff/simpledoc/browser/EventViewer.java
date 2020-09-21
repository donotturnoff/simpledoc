package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class EventViewer {

    private final static Object[] COLUMNS = {"Timestamp", "Type", "Message"};

    private final Page page;
    private final JFrame gui;
    private final DefaultTableModel tm;

    public EventViewer(Page page) {
        this.page = page;

        gui = new JFrame("Event viewer");
        gui.setMinimumSize(new Dimension(800, 600));
        gui.setIconImage(SDTPBrowser.ICON);

        updateTitle();

        tm = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        JTable table = new JTable(tm);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(195);
        tcm.getColumn(1).setPreferredWidth(65);
        tcm.getColumn(2).setPreferredWidth(540);

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        gui.add(scrollPane);
    }

    public void addEvent(BrowserEvent event) {
        tm.addRow(event.toTableRow());

        if (gui.isVisible()) {
            gui.getContentPane().revalidate();
            gui.getContentPane().repaint();
        }
    }

    public void toggle() {
        gui.setVisible(!gui.isVisible());
    }

    public void hide() {
        gui.setVisible(false);
    }

    public void updateTitle() {
        gui.setTitle("Event viewer for " + page.getUrl());
    }
}
