package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
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

    private static final int RESULTS_PER_PAGE = 100; // TODO: make configurable
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy KK:mm:ss a");

    private final SDTPBrowser browser;
    private final JFrame gui;
    private final HistoryStorageHandler handler;
    private final JPanel infoPanel;
    private final JButton prevBtn, clearBtn, nextBtn;
    private final JLabel locationLabel;
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
        locationLabel = new JLabel();
        nextBtn = new JButton("\u2b62");
        clearBtn = new JButton("Clear");

        prevBtn.setToolTipText("More recent");
        nextBtn.setToolTipText("Further back");
        clearBtn.setToolTipText("Clear history");

        prevBtn.addActionListener(this);
        nextBtn.addActionListener(this);
        clearBtn.addActionListener(this);

        navPanel.add(prevBtn);
        navPanel.add(locationLabel);
        navPanel.add(nextBtn);
        navPanel.add(clearBtn);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        gui.add(navPanel, BorderLayout.NORTH);
        gui.add(infoPanel);

        refresh();
    }

    public void refresh() {

        if (gui.isVisible()) {

            int entries = handler.getHistoryLength();
            int lastPage = (entries-1)/RESULTS_PER_PAGE;
            pageNo = Math.max(pageNo, 0);
            pageNo = Math.min(lastPage, pageNo);
            prevBtn.setEnabled(pageNo > 0);
            nextBtn.setEnabled(pageNo < lastPage);
            clearBtn.setEnabled(entries > 0);

            infoPanel.removeAll();

            try {
                int start = RESULTS_PER_PAGE*pageNo;
                SortedMap<Date, URL> results = handler.get(start, RESULTS_PER_PAGE);
                int end = start + results.size();

                if (entries > 0) {
                    locationLabel.setText("Showing entries " + (start + 1) + " to " + end + " out of " + entries);
                } else {
                    locationLabel.setText("History empty");
                }

                String[][] tableValues = new String[results.size()][2];

                int i = 0;
                for (Date datetime : results.keySet()) {
                    String timestamp = DATE_FORMAT.format(datetime);

                    tableValues[i][0] = timestamp;
                    tableValues[i++][1] = results.get(datetime).toString();
                }

                TableModel tm = new DefaultTableModel(tableValues, new String[]{"Timestamp", "URL"}) {
                    @Override
                    public boolean isCellEditable(int row, int column){
                        return false;
                    }
                };

                historyTable = new JTable(tm);
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

    public void show() {
        gui.setVisible(true);
        refresh();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == prevBtn) {
            pageNo--;
            refresh();
        } else if (source == clearBtn) {
            int choice = JOptionPane.showConfirmDialog(gui, "Are you sure you wish to clear your history file (" + browser.getConfig().getProperty("history_file") + ")? This operation cannot be undone.", "Clear history?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == 0) {
                try {
                    handler.clear();
                    refresh();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(gui, "Failed to delete history file (" + browser.getConfig().getProperty("history_file") + ").", "Deletion failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (source == nextBtn) {
            pageNo++;
            refresh();
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        int row = historyTable.rowAtPoint(mouseEvent.getPoint());
        int col = historyTable.columnAtPoint(mouseEvent.getPoint());
        Object value = historyTable.getValueAt(row, col);
        if (value != null && col == 1) {
            browser.addPage(value.toString());
        }
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
