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
    private static final String HOMEPAGE = "sdtp://localhost:5000";

    // Containers
    private JFrame gui;
    private JPanel navBar, urlBarContainer, statusBar;
    private JTabbedPane tabbedPane;

    // Menus
    private JMenuBar menuBar;
    private JMenu fileMenu;

    // Inputs
    private JButton backBtn, forwardBtn, reloadBtn, goBtn, newTabBtn;
    private JTextField urlBar;

    // Labels
    private JLabel statusLabel;

    // Non-GUI
    private final List<Page> pages;
    private Page currentPage;

    public static void main(String[] args) {
        URL.setURLStreamHandlerFactory(new SDTPURLStreamHandlerFactory());
        SDTPBrowser browser = new SDTPBrowser();
        browser.run();
    }

    private SDTPBrowser() {
        pages = new ArrayList<>();
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
        addPage(HOMEPAGE);
    }

    private void createWidgets() {
        gui = new JFrame("Simpledoc browser v0.1");
        navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.X_AXIS));
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        urlBarContainer = new JPanel();
        urlBarContainer.setLayout(new BoxLayout(urlBarContainer, BoxLayout.X_AXIS));

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        backBtn = new JButton("\u2b60");
        forwardBtn = new JButton("\u2b62");
        reloadBtn = new JButton("\u27f3");
        goBtn = new JButton("Go");
        newTabBtn = new JButton("+");
        urlBar = new JTextField();

        statusLabel = new JLabel("Welcome", JLabel.LEFT);
    }

    private void configureWidgets() {
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setMinimumSize(new Dimension(800, 600));

        navBar.setBorder(new CompoundBorder(navBar.getBorder(), new EmptyBorder(5, 5, 5, 5)));
        urlBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        urlBarContainer.setBorder(new CompoundBorder(BorderFactory.createLoweredBevelBorder(), new EmptyBorder(2, 2, 2, 2)));

        urlBarContainer.setBackground(Color.WHITE);

        tabbedPane.addChangeListener(this);

        backBtn.addActionListener(this);
        forwardBtn.addActionListener(this);
        reloadBtn.addActionListener(this);
        goBtn.addActionListener(this);
        newTabBtn.addActionListener(this);

        urlBar.addKeyListener(this);

        navBar.setToolTipText("Enter a URL to visit");
        backBtn.setToolTipText("Visit previous page");
        forwardBtn.setToolTipText("Visit next page");
        reloadBtn.setToolTipText("Reload current page");
        newTabBtn.setToolTipText("Add a new tab");
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
        navBar.add(Box.createRigidArea(new Dimension(5, 0)));
        navBar.add(newTabBtn);

        menuBar.add(fileMenu);

        statusBar.add(statusLabel);

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

    private void addPage(String urlString) {
        currentPage = new Page(this);
        pages.add(currentPage);
        tabbedPane.addTab("Loading", currentPage.getScrollPane());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
        tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), new CustomTabComponent(this, tabbedPane));
        currentPage.navigate(urlString);
    }

    public void removePage(int index) {
        pages.remove(index);
        tabbedPane.remove(index);

        if (pages.size() > 0) {
            int newIndex = tabbedPane.getSelectedIndex() % (pages.size());
            tabbedPane.setSelectedIndex(newIndex);
            currentPage = pages.get(newIndex);
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
        JButton source = (JButton) actionEvent.getSource();
        if (source == backBtn) {
            currentPage.back();
        } else if (source == forwardBtn) {
            currentPage.forward();
        } else if (source == reloadBtn) {
            currentPage.reload();
        } else if (source == goBtn) {
            currentPage.navigate(urlBar.getText());
        } else if (source == newTabBtn) {
            addPage(HOMEPAGE);
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            currentPage.navigate(urlBar.getText());
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource() == tabbedPane) {
            int index = tabbedPane.getSelectedIndex();
            if (index >= 0 && index < pages.size()) {
                currentPage = pages.get(index);
                setUrlBar(currentPage.getUrl());
            } else {
                System.exit(0);
            }
        }
    }
}
