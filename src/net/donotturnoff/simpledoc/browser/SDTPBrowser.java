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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SDTPBrowser implements ActionListener, KeyListener, ChangeListener {

    //TODO: add as configuration option
    private static final String HOMEPAGE = "sdtp://localhost";
    public static final Image ICON;

    static {
        URL url = ClassLoader.getSystemResource("net/donotturnoff/simpledoc/browser/icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        ICON = kit.createImage(url);
    }

    // Containers
    private JFrame gui;
    private JPanel navBar, urlBarContainer, statusBar;
    private JTabbedPane tabbedPane;

    // Menus
    private JMenuBar menuBar;
    private JMenu fileMenu, bookmarksMenu, devMenu;
    private JMenuItem openFileMenuItem, reloadMenuItem, newTabMenuItem, historyMenuItem, savePageMenuItem, settingsMenuItem, exitMenuItem, viewAllBookmarksMenuItem, evMenuItem, sourcesMenuItem, networkMenuItem;

    // Inputs
    private JButton backBtn, forwardBtn, reloadBtn, goBtn, evBtn;
    private JTextField urlBar;

    // Labels
    private JLabel statusLabel;

    // Non-GUI
    private final List<Page> pages;
    private Page currentPage;
    private boolean keyDown;

    public static void main(String[] args) {
        URL.setURLStreamHandlerFactory(new SDTPURLStreamHandlerFactory());
        SDTPBrowser browser = new SDTPBrowser();
        browser.run();
    }

    private SDTPBrowser() {
        pages = new ArrayList<>();
        keyDown = false;
    }

    private void run() {
        SwingUtilities.invokeLater(this::init);
    }

    private void init() {
        UIManager.put("swing.boldMetal", false);

        createWidgets();
        configureWidgets();
        constructGUI();
        showGUI();
        addPage(HOMEPAGE, true);
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
        sourcesMenuItem = new JMenuItem("Sources");
        networkMenuItem = new JMenuItem("Network");

        backBtn = new JButton("\u2b60");
        forwardBtn = new JButton("\u2b62");
        reloadBtn = new JButton("\u27f3");
        goBtn = new JButton("Go");
        evBtn = new JButton("Log");
        urlBar = new JTextField();

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
        sourcesMenuItem.addActionListener(this);
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

        devMenu.add(sourcesMenuItem);
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

    void addPage(String urlString, boolean externalInput) {
        currentPage = new Page(this);
        pages.add(currentPage);
        tabbedPane.insertTab("Loading", null, currentPage.getScrollPane(), "Loading page", tabbedPane.getTabCount()-1); // Minus 1 because although the last tab is actually the add tab button, this tab hasn't been added yet
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-2); // Minus 2 because the last tab is actually the add tab button
        tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), new PageTabComponent(this, tabbedPane));
        currentPage.navigate(urlString, externalInput);
    }

    public void removePage(int index) {
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

    public void setTitle(int index, String title) {
        gui.setTitle(title + " - Simpledoc browser v0.1");
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
        } else if (source == evBtn || source == evMenuItem) {
            currentPage.showEventViewer();
        } else if (source == exitMenuItem) {
            System.exit(0);
        } else if (source == newTabMenuItem) {
            addPage(getHomepage(), true);
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

    public String getHomepage() {
        return HOMEPAGE;
    }
}
