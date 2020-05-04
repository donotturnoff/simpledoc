package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class SDTPBrowser implements ActionListener, KeyListener {

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
    private Set<Page> pages;
    private Page currentPage;

    public static void main(String[] args) {
        URL.setURLStreamHandlerFactory(new SDTPURLStreamHandlerFactory());
        SDTPBrowser browser = new SDTPBrowser();
        browser.run();
    }

    private SDTPBrowser() {
        pages = new HashSet<>();
    }

    private void run() {
        SwingUtilities.invokeLater(this::init);
    }

    private void init() {
        createWidgets();
        configureWidgets();
        constructGUI();
        showGUI();
        Page startPage = new Page();
        addPage(startPage);
        startPage.navigate(HOMEPAGE);
    }

    private void createWidgets() {
        gui = new JFrame("Simpledoc browser v0.1");
        navBar = new JPanel();
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tabbedPane = new JTabbedPane();

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        backBtn = new JButton("\u2b60");
        forwardsBtn = new JButton("\u2b62");
        reloadBtn = new JButton("\u27f3");
        newTabBtn = new JButton("+");
        urlBar = new JTextField(60);

        statusLabel = new JLabel("Welcome", JLabel.LEFT);
    }

    private void configureWidgets() {
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setMinimumSize(new Dimension(800, 600));

        backBtn.addActionListener(this);
        forwardsBtn.addActionListener(this);
        reloadBtn.addActionListener(this);
        newTabBtn.addActionListener(this);

        urlBar.addKeyListener(this);
    }

    private void constructGUI() {
        navBar.add(backBtn);
        navBar.add(forwardsBtn);
        navBar.add(reloadBtn);
        navBar.add(urlBar);
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

    private void addPage(Page page) {
        currentPage = page;
        pages.add(page);
        tabbedPane.addTab("Loading", page.getPanel());
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
}
