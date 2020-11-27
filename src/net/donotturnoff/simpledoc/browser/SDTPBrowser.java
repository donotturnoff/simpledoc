package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SDTPBrowser implements ActionListener, KeyListener, ChangeListener {

    private static final String CONFIG_PATH = "sdtpbrowser.conf";
    private static final int FAVICON_WIDTH = 16;
    private static final int FAVICON_HEIGHT = 16;
    public static final Image ICON;
    public static final ImageIcon ICON_FAVICON, SPINNER_FAVICON, ERROR_FAVICON;

    static {
        Toolkit kit = Toolkit.getDefaultToolkit();
        URL iconUrl = ClassLoader.getSystemResource("net/donotturnoff/simpledoc/browser/icon.png");
        ICON = kit.createImage(iconUrl);

        URL iconFaviconUrl = ClassLoader.getSystemResource("net/donotturnoff/simpledoc/browser/icon.gif");
        ICON_FAVICON = new ImageIcon(iconFaviconUrl);

        URL spinnerFaviconUrl = ClassLoader.getSystemResource("net/donotturnoff/simpledoc/browser/spinner.gif");
        SPINNER_FAVICON = new ImageIcon(spinnerFaviconUrl);

        URL errorFaviconUrl = ClassLoader.getSystemResource("net/donotturnoff/simpledoc/browser/error.gif");
        ERROR_FAVICON = new ImageIcon(errorFaviconUrl);
    }

    public static ImageIcon scaleFavicon(ImageIcon favicon, boolean gif) {
        if (favicon == null) {
            return null;
        }

        int scaling = gif ? Image.SCALE_DEFAULT : Image.SCALE_SMOOTH;
        Image scaled = favicon.getImage().getScaledInstance(FAVICON_WIDTH, FAVICON_HEIGHT, scaling);
        return new ImageIcon(scaled);
    }

    // Containers
    private JFrame gui;
    private JPanel navBar, urlBarContainer, statusBar;
    private JTabbedPane tabbedPane;

    // Menus
    private JMenuBar menuBar;
    private JMenu fileMenu, bookmarksMenu, devMenu;
    private JMenuItem openFileMenuItem, reloadMenuItem, newTabMenuItem, historyMenuItem, savePageMenuItem, settingsMenuItem, exitMenuItem, viewAllBookmarksMenuItem, evMenuItem, rvMenuItem, networkMenuItem;

    // Inputs
    private JButton backBtn, forwardBtn, reloadBtn, goBtn, evBtn;
    private JTextField urlBar;

    // Labels
    private JLabel statusLabel;

    // Dialogs
    private JFileChooser fc;

    // Non-GUI
    private final List<Page> pages;
    private Page currentPage;
    private boolean keyDown;
    private final Properties config;
    private SettingsEditor settingsEditor;
    private HistoryStorageHandler historyHandler;
    private HistoryViewer historyViewer;

    public static void main(String[] args) {
        URL.setURLStreamHandlerFactory(new SDTPURLStreamHandlerFactory());
        SDTPBrowser browser = new SDTPBrowser();
        browser.run();
    }

    private SDTPBrowser() {
        pages = new ArrayList<>();
        keyDown = false;

        config = new Properties();

        config.setProperty("homepage", "sdtp://localhost");
        config.setProperty("default_mime", "text/sdml");
        config.setProperty("history_file", "sdtpbrowser.hist");
        config.setProperty("bookmarks_file", "sdtpbrowser.bkmk");
        config.setProperty("plain_text_font_family", "monospaced");
        config.setProperty("plain_text_font_size", "12");

        loadConfig();
    }

    private void run() {
        SwingUtilities.invokeLater(this::init);
    }

    private void init() {
        UIManager.put("swing.boldMetal", false);

        settingsEditor = new SettingsEditor(this);
        historyHandler = new HistoryStorageHandler(this);
        historyViewer = new HistoryViewer(this, historyHandler);
        
        createWidgets();
        configureWidgets();
        constructGUI();
        showGUI();
        addPage(config.getProperty("homepage"));
    }

    private void createWidgets() {
        gui = new JFrame("Simpledoc browser v0.1");
        navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.X_AXIS));
        statusBar = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        urlBarContainer = new JPanel();
        urlBarContainer.setLayout(new BoxLayout(urlBarContainer, BoxLayout.X_AXIS));

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        bookmarksMenu = new JMenu("Bookmarks");
        devMenu = new JMenu("Developer");

        openFileMenuItem = new JMenuItem("Open file");
        reloadMenuItem = new JMenuItem("Reload page");
        newTabMenuItem = new JMenuItem("New tab");
        historyMenuItem = new JMenuItem("History");
        savePageMenuItem = new JMenuItem("Save page");
        settingsMenuItem = new JMenuItem("Settings");
        exitMenuItem = new JMenuItem("Exit");
        viewAllBookmarksMenuItem = new JMenuItem("View all bookmarks");
        evMenuItem = new JMenuItem("Event viewer");
        rvMenuItem = new JMenuItem("Resources");
        networkMenuItem = new JMenuItem("Network");

        backBtn = new JButton("\u2b60");
        forwardBtn = new JButton("\u2b62");
        reloadBtn = new JButton("\u27f3");
        goBtn = new JButton("Go");
        evBtn = new JButton("Log");
        urlBar = new JTextField();

        fc = new JFileChooser();

        tabbedPane.add(new JPanel());

        statusLabel = new JLabel("Welcome", JLabel.LEFT);
    }

    private void configureWidgets() {
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setMinimumSize(new Dimension(800, 600));
        gui.setIconImage(ICON);

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    int id = e.getID();
                    if (id == KeyEvent.KEY_PRESSED) {
                        keyPressed(e);
                    } else if (id == KeyEvent.KEY_RELEASED) {
                        keyReleased(e);
                    }
                    return false;
                });

        openFileMenuItem.addActionListener(this);
        reloadMenuItem.addActionListener(this);
        newTabMenuItem.addActionListener(this);
        historyMenuItem.addActionListener(this);
        savePageMenuItem.addActionListener(this);
        settingsMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);
        viewAllBookmarksMenuItem.addActionListener(this);
        evMenuItem.addActionListener(this);
        rvMenuItem.addActionListener(this);
        networkMenuItem.addActionListener(this);

        navBar.setBorder(new CompoundBorder(navBar.getBorder(), new EmptyBorder(5, 5, 5, 5)));
        urlBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        urlBarContainer.setBorder(new CompoundBorder(BorderFactory.createLoweredBevelBorder(), new EmptyBorder(2, 2, 2, 2)));

        statusBar.setBorder(new EmptyBorder(2, 2, 2, 2));

        urlBarContainer.setBackground(Color.WHITE);

        tabbedPane.setEnabledAt(0, false);
        tabbedPane.setTabComponentAt(0, new AddTabComponent(this));

        tabbedPane.addChangeListener(this);

        backBtn.addActionListener(this);
        forwardBtn.addActionListener(this);
        reloadBtn.addActionListener(this);
        goBtn.addActionListener(this);
        evBtn.addActionListener(this);

        urlBar.addKeyListener(this);

        navBar.setToolTipText("Enter a URL to visit");
        backBtn.setToolTipText("Visit previous page");
        forwardBtn.setToolTipText("Visit next page");
        reloadBtn.setToolTipText("Reload current page");
        evBtn.setToolTipText("Display page event viewer");
    }

    private void constructGUI() {
        urlBarContainer.add(urlBar);
        urlBarContainer.add(goBtn);

        navBar.add(backBtn);
        navBar.add(Box.createRigidArea(new Dimension(5, 0)));
        navBar.add(forwardBtn);
        navBar.add(Box.createRigidArea(new Dimension(5, 0)));
        navBar.add(reloadBtn);
        navBar.add(Box.createRigidArea(new Dimension(5, 0)));
        navBar.add(urlBarContainer);

        fileMenu.add(reloadMenuItem);
        fileMenu.add(openFileMenuItem);
        fileMenu.add(newTabMenuItem);
        fileMenu.add(historyMenuItem);
        fileMenu.add(savePageMenuItem);
        fileMenu.add(settingsMenuItem);
        fileMenu.add(exitMenuItem);

        bookmarksMenu.add(viewAllBookmarksMenuItem);

        devMenu.add(rvMenuItem);
        devMenu.add(networkMenuItem);
        devMenu.add(evMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(bookmarksMenu);
        menuBar.add(devMenu);

        statusBar.add(statusLabel, BorderLayout.LINE_START);
        statusBar.add(evBtn, BorderLayout.LINE_END);

        Container pane = gui.getContentPane();
        gui.setJMenuBar(menuBar);
        pane.add("North", navBar);
        pane.add("Center", tabbedPane);
        pane.add("South", statusBar);
    }

    private void showGUI() {
        gui.setVisible(true);
        gui.setExtendedState(gui.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public void setUrlBar(URL url) {
        if (url == null) {
            urlBar.setText("");
        } else {
            urlBar.setText(url.toString());
        }
    }

    void addPage(String urlString) {
        currentPage = new Page(this);
        pages.add(currentPage);
        tabbedPane.insertTab("New tab", null, currentPage.getScrollPane(), "New tab", tabbedPane.getTabCount()-1); // Minus 1 because although the last tab is actually the add tab button, this tab hasn't been added yet
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-2); // Minus 2 because the last tab is actually the add tab button
        tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), new PageTabComponent(this, tabbedPane, currentPage));
        currentPage.navigate(urlString, true);
    }

    public void removePage(int index) {
        pages.get(index).close();
        pages.remove(index);
        tabbedPane.remove(index);

        if (pages.size() > 0) {
            int newIndex = tabbedPane.getSelectedIndex() % (pages.size());
            tabbedPane.setSelectedIndex(newIndex);
            currentPage = pages.get(newIndex);
        } else {
            System.exit(0);
        }
    }

    private void saveCurrentPage() {
        Page p = currentPage;
        fc.setDialogTitle("Save " + p.getFilename());
        fc.setSelectedFile(new File(p.getFilename()));
        int choice = fc.showSaveDialog(gui);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                OutputStream out = new FileOutputStream(f, false);
                out.write(p.getData().getBody());
                out.close();
            } catch (IOException e) {
                p.error("Failed to save file: " + e);
            }
        }
    }

    private void openFile() {
        fc.setDialogTitle("Open file");
        int choice = fc.showOpenDialog(gui);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            addPage("file://" + f.getPath());
        }
    }

    public void setTitle(int index, String title) {
        gui.setTitle(title + " - simpledoc browser v0.1");
        tabbedPane.setTitleAt(index, title);
        tabbedPane.setToolTipTextAt(index, title);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void setBackButtonState(boolean enabled) {
        backBtn.setEnabled(enabled);
    }

    public void setForwardButtonState(boolean enabled) {
        forwardBtn.setEnabled(enabled);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public Properties getConfig() {
        return config;
    }

    void loadConfig() {
        try {
            FileInputStream in = new FileInputStream(CONFIG_PATH);
            config.load(in);
            in.close();
        } catch (IOException e) {
            System.out.println("Failed to read config file: " + e.getMessage());
        }
    }

    public void saveConfig() {
        try {
            FileOutputStream out = new FileOutputStream(CONFIG_PATH);
            config.store(out, "Configuration for SDTPBrowser");
            out.close();
        } catch (IOException e) {
            System.out.println("Failed to save config file: " + e.getMessage());
        }
    }

    public void addToHistory(Page page, URL url) {
        try {
            historyHandler.add(url);
            historyViewer.refresh();
        } catch (IOException e) {
            page.info("Failed to write " + url + " to history file: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == backBtn) {
            currentPage.back();
        } else if (source == forwardBtn) {
            currentPage.forward();
        } else if (source == reloadBtn || source == reloadMenuItem) {
            currentPage.reload();
        } else if (source == goBtn) {
            currentPage.navigate(urlBar.getText(), true);
        } else if (source == exitMenuItem) {
            System.exit(0);
        } else if (source == newTabMenuItem) {
            addPage(config.getProperty("homepage"));
        } else if (source == savePageMenuItem) {
            saveCurrentPage();
        } else if (source == openFileMenuItem) {
            openFile();
        } else if (source == evBtn) {
            currentPage.toggleEventViewer();
        } else if (source == evMenuItem) {
            currentPage.showEventViewer();
        } else if (source == rvMenuItem) {
            currentPage.showResourceViewer();
        } else if (source == settingsMenuItem) {
            settingsEditor.show();
        } else if (source == historyMenuItem) {
            historyViewer.show();
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER && keyEvent.getSource() == urlBar) {
            if (!keyDown) {
                keyDown = true;
                currentPage.navigate(urlBar.getText(), true);
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_R) {
            if (keyEvent.isControlDown()) {
                if (!keyDown) {
                    keyDown = true;
                    currentPage.reload();
                }
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_TAB) {
            if (keyEvent.isControlDown()) {
                int index;
                if (keyEvent.isShiftDown()) {
                    index = (tabbedPane.getSelectedIndex()-1+pages.size())%pages.size(); // Add pages.size() to keep index positive
                } else {
                    index = (tabbedPane.getSelectedIndex()+1)%pages.size();
                }
                if (!keyDown) {
                    keyDown = true;
                    tabbedPane.setSelectedIndex(index);
                    currentPage = pages.get(index);
                    setUrlBar(currentPage.getUrl());
                }
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
            if (keyEvent.isControlDown()) {
                if (!keyDown) {
                    keyDown = true;
                    saveCurrentPage();
                }
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_O) {
            if (keyEvent.isControlDown()) {
                if (!keyDown) {
                    keyDown = true;
                    openFile();
                }
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_W) {
            if (keyEvent.isControlDown()) {
                if (!keyDown) {
                    keyDown = true;
                    removePage(tabbedPane.getSelectedIndex());
                }
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_T) {
            if (keyEvent.isControlDown()) {
                if (!keyDown) {
                    keyDown = true;
                    addPage(config.getProperty("homepage"));
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        keyDown = false;
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource() == tabbedPane) {
            int index = tabbedPane.getSelectedIndex();
            if (index >= 0 && index < pages.size()) {
                currentPage = pages.get(index);
                setUrlBar(currentPage.getUrl());
            }
        }
    }
}
