package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingsEditor implements ActionListener {

    private final Map<JComponent, String> componentPropertyMap = new HashMap<>();
    private final SDTPBrowser browser;
    private final JFrame gui;
    private final JButton saveButton, cancelButton;

    public SettingsEditor(SDTPBrowser browser) {
        this.browser = browser;
        this.gui = new JFrame("Settings editor");
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel generalPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        generalPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        generalPanel.add(new JLabel("Homepage"));
        JTextField homepageEntry = new JTextField();
        componentPropertyMap.put(homepageEntry, "homepage");
        generalPanel.add(homepageEntry);

        generalPanel.add(new JLabel("Default MIME type"));
        JTextField defaultMimeTypeEntry = new JTextField();
        componentPropertyMap.put(defaultMimeTypeEntry, "default_mime");
        generalPanel.add(defaultMimeTypeEntry);

        generalPanel.add(new JLabel("History file"));
        JTextField historyFileEntry = new JTextField();
        componentPropertyMap.put(historyFileEntry, "history_file");
        generalPanel.add(historyFileEntry);

        generalPanel.add(new JLabel("History entries per page"));
        SpinnerModel historyEntriesPerPageEntryModel = new SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 1);
        JSpinner historyEntriesPerPageEntry = new JSpinner(historyEntriesPerPageEntryModel);
        componentPropertyMap.put(historyEntriesPerPageEntry, "history_entries_per_page");
        generalPanel.add(historyEntriesPerPageEntry);

        generalPanel.add(new JLabel("Store history?"));
        JCheckBox storeHistoryCheckbox = new JCheckBox();
        componentPropertyMap.put(storeHistoryCheckbox, "store_history");
        generalPanel.add(storeHistoryCheckbox);

        generalPanel.add(new JLabel("Bookmarks file"));
        JTextField bookmarksFileEntry = new JTextField();
        componentPropertyMap.put(bookmarksFileEntry, "bookmarks_file");
        generalPanel.add(bookmarksFileEntry);

        generalPanel.add(new JLabel("Bookmark entries per page"));
        SpinnerModel bookmarkEntriesPerPageEntryModel = new SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 1);
        JSpinner bookmarkEntriesPerPageEntry = new JSpinner(bookmarkEntriesPerPageEntryModel);
        componentPropertyMap.put(bookmarkEntriesPerPageEntry, "bookmark_entries_per_page");
        generalPanel.add(bookmarkEntriesPerPageEntry);

        JPanel networkPanel = new JPanel();

        JPanel stylePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        stylePanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        stylePanel.add(new JLabel("Plain text font family"));
        JTextField plainTextFontFamilyEntry = new JTextField();
        componentPropertyMap.put(plainTextFontFamilyEntry, "plain_text_font_family");
        stylePanel.add(plainTextFontFamilyEntry);

        stylePanel.add(new JLabel("Plain text font size"));
        SpinnerModel plainTextFontSizeEntryModel = new SpinnerNumberModel(12, 1, Integer.MAX_VALUE, 1);
        JSpinner plainTextFontSizeEntry = new JSpinner(plainTextFontSizeEntryModel);
        componentPropertyMap.put(plainTextFontSizeEntry, "plain_text_font_size");
        stylePanel.add(plainTextFontSizeEntry);

        cancelButton = new JButton("Cancel");
        saveButton = new JButton("Save");
        cancelButton.addActionListener(this);
        saveButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        tabbedPane.addTab("General", generalPanel);
        tabbedPane.addTab("Network", networkPanel);
        tabbedPane.addTab("Style", stylePanel);
        gui.add(tabbedPane, BorderLayout.CENTER);
        gui.add(buttonPanel, BorderLayout.SOUTH);

        updateValues();

        gui.setResizable(false);
        gui.pack();
    }

    private void updateValues() {
        browser.loadConfig();
        Properties config = browser.getConfig();
        for (JComponent c: componentPropertyMap.keySet()) {
            if (c instanceof JTextField) {
                ((JTextField) c).setText(config.getProperty(componentPropertyMap.get(c)));
            } else if (c instanceof JSpinner) {
                try {
                    ((JSpinner) c).setValue(Integer.parseInt(config.getProperty(componentPropertyMap.get(c))));
                } catch (NumberFormatException ignored) {

                }
            } else if (c instanceof JCheckBox) {
                ((JCheckBox) c).setSelected(config.getProperty(componentPropertyMap.get(c)).equals("true"));
            }
        }
    }

    private void saveValues() {
        Properties config = browser.getConfig();
        for (JComponent c: componentPropertyMap.keySet()) {
            String property = componentPropertyMap.get(c);
            if (c instanceof JTextField) {
                config.setProperty(property, ((JTextField) c).getText());
            } else if (c instanceof JSpinner) {
                config.setProperty(property, ((JSpinner) c).getValue().toString());
            } else if (c instanceof JCheckBox) {
                config.setProperty(property, ((JCheckBox) c).isSelected() ? "true" : "false");
            }
        }
        browser.saveConfig();
    }

    public void toggle() {
        boolean notVis = !gui.isVisible();
        if (notVis) {
            updateValues();
        }
        gui.setVisible(notVis);
    }

    public void show() {
        gui.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == saveButton) {
            saveValues();
            toggle();
        } else if (source == cancelButton) {
            toggle();
        }
    }
}
