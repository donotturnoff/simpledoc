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
    private JPanel navBar, statusBar;
    private JTabbedPane tabbedPane;

    // Menus
    private JMenuBar menuBar;
    private JMenu fileMenu;

    // Inputs
    private JButton backBtn, forwardsBtn, reloadBtn, newTabBtn;
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

    public void setUrlBar(URL url) {
        if (url == null) {
            urlBar.setText("Loading");
        } else {
            urlBar.setText(url.toString());
        }
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

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        backBtn = new JButton("\u2b60");
        forwardsBtn = new JButton("\u2b62");
        reloadBtn = new JButton("\u27f3");
        newTabBtn = new JButton("+");
        urlBar = new JTextField();

        statusLabel = new JLabel("Welcome", JLabel.LEFT);
    }

    private void configureWidgets() {
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setMinimumSize(new Dimension(800, 600));

        navBar.setBorder(new CompoundBorder(navBar.getBorder(), new EmptyBorder(5, 5, 5, 5)));

        tabbedPane.addChangeListener(this);

        backBtn.addActionListener(this);
        forwardsBtn.addActionListener(this);
        reloadBtn.addActionListener(this);
        newTabBtn.addActionListener(this);

        urlBar.addKeyListener(this);
    }

    private void constructGUI() {
        navBar.add(backBtn);
        navBar.add(Box.createRigidArea(new Dimension(5, 0)));
        navBar.add(forwardsBtn);
        navBar.add(Box.createRigidArea(new Dimension(5, 0)));
        navBar.add(reloadBtn);
        navBar.add(Box.createRigidArea(new Dimension(5, 0)));
        navBar.add(urlBar);
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

        int newIndex = tabbedPane.getSelectedIndex()%(pages.size());
        tabbedPane.setSelectedIndex(newIndex);
        currentPage = pages.get(newIndex);
    }

    public void setTitle(int tab, String title) {
        gui.setTitle(title + " - Simpledoc browser v0.1");
        tabbedPane.setTitleAt(tab, title);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton source = (JButton) actionEvent.getSource();
        if (source == backBtn) {
            currentPage.back();
        } else if (source == forwardsBtn) {
            currentPage.forward();
        } else if (source == reloadBtn) {
            currentPage.reload();
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
            currentPage = pages.get(tabbedPane.getSelectedIndex());
            setUrlBar(currentPage.getUrl());
        }
    }
}
