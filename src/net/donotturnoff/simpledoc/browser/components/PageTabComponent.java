package net.donotturnoff.simpledoc.browser.components;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.SDTPBrowser;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class PageTabComponent extends JPanel  {

    private final SDTPBrowser browser;
    private final JTabbedPane pane;
    private final JLabel label;

    public PageTabComponent(final SDTPBrowser browser, final JTabbedPane pane, final Page page)  {
        // Unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.pane = pane;
        this.browser = browser;
        setOpaque(false);

        label = new JLabel() {
            public String getText() {
                return page.getTitle();
            }

            public Icon getIcon() {
                return page.getFavicon();
            }
        };

        add(label);
        updateLabel();

        // Add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        // Tab button
        JButton button = new TabButton();
        add(button);

        // Add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
    }

    public void updateLabel() {
        label.setSize(label.getPreferredSize());
        label.revalidate();
        label.repaint();
    }

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 16;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close this tab");
            setUI(new BasicButtonUI());            // Bare-bones UI
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setContentAreaFilled(false);
            setFocusable(false);
            setBackground(new Color(200, 200, 200));
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(PageTabComponent.this);
            if (i != -1) {
                browser.removePage(i);
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {}

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            //Shift the cross for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            if (getModel().isRollover()) {
                g2.setColor(Color.RED); // Highlight cross on mouseover
            } else {
                g2.setColor(Color.BLACK);
            }
            int delta = 4;

            // Draw cross
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setContentAreaFilled(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setContentAreaFilled(false);
            }
        }
    };
}
