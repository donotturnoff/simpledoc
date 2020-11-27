package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

public class HistoryViewer implements ActionListener, MouseListener {

    // TODO: add clear history button
    // TODO: make history storage optional

    private static final int RESULTS_PER_PAGE = 100; // TODO: make configurable
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy KK:mm:ss a");

    private final SDTPBrowser browser;
    private final JFrame gui;
    private final HistoryStorageHandler handler;
    private final JPanel infoPanel;
    private final JButton prevBtn, nextBtn;
    private JTable historyTable;
    private int pageNo;

    public HistoryViewer(SDTPBrowser browser, HistoryStorageHandler handler) {
        this.browser = browser;
        this.handler = handler;
        pageNo = 0;

        gui = new JFrame("History viewer");
        gui.setMinimumSize(new Dimension(800, 600));
        gui.setIconImage(SDTPBrowser.ICON);

        JPanel navPanel = new JPanel();
        prevBtn = new JButton("\u2b60");
        nextBtn = new JButton("\u2b62");

        prevBtn.setToolTipText("More recent");
        nextBtn.setToolTipText("Further back");

        prevBtn.addActionListener(this);
        nextBtn.addActionListener(this);

        navPanel.add(prevBtn);
        navPanel.add(nextBtn);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        gui.add(navPanel, BorderLayout.NORTH);
        gui.add(infoPanel);

        refresh();
    }

    public void refresh() {

        if (gui.isVisible()) {
            int lastPage = (handler.getHistoryLength()-1)/RESULTS_PER_PAGE;
            pageNo = Math.max(pageNo, 0);
            pageNo = Math.min(lastPage, pageNo);
            prevBtn.setEnabled(pageNo > 0);
            nextBtn.setEnabled(pageNo < lastPage);

            infoPanel.removeAll();

            try {
                SortedMap<Date, URL> results = handler.get(RESULTS_PER_PAGE*pageNo, RESULTS_PER_PAGE);

                String[][] tableValues = new String[RESULTS_PER_PAGE][2];

                int i = 0;
                for (Date datetime : results.keySet()) {
                    String timestamp = DATE_FORMAT.format(datetime);

                    tableValues[i][0] = timestamp;
                    tableValues[i++][1] = results.get(datetime).toString();
                }

                historyTable = new JTable(tableValues, new String[]{"Timestamp", "URL"});
                historyTable.getTableHeader().setReorderingAllowed(false);
                historyTable.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
                historyTable.addMouseListener(this);

                TableColumnModel tcm = historyTable.getColumnModel();
                tcm.getColumn(0).setPreferredWidth(200);
                tcm.getColumn(1).setPreferredWidth(600);

                JScrollPane scrollPane = new JScrollPane(historyTable);
                scrollPane.getVerticalScrollBar().setUnitIncrement(10);
                scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
                scrollPane.getVerticalScrollBar().setBlockIncrement(40);
                scrollPane.getHorizontalScrollBar().setUnitIncrement(40);

                infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                infoPanel.add(scrollPane);
            } catch (IOException e) {
                infoPanel.add(new JLabel("Failed to load history file: " + e.getMessage()));
            }
            gui.revalidate();
            gui.repaint();
        }
    }

    public void toggle() {
        gui.setVisible(!gui.isVisible());
    }

    public void show() {
        gui.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == prevBtn) {
            pageNo--;
            refresh();
        } else if (source == nextBtn) {
            pageNo++;
            refresh();
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        int row = historyTable.rowAtPoint(mouseEvent.getPoint());
        int col = historyTable.columnAtPoint(mouseEvent.getPoint());
        browser.addPage(historyTable.getValueAt(row, col).toString());
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
