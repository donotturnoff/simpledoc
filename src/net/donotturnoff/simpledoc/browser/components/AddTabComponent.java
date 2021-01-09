package net.donotturnoff.simpledoc.browser.components;

import net.donotturnoff.simpledoc.browser.SDTPBrowser;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class AddTabComponent extends JPanel  {

    private final SDTPBrowser browser;

    public AddTabComponent(final SDTPBrowser browser)  {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.browser = browser;
        setOpaque(false);

        JButton button = new AddTabButton();

        add(button);
        setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
    }


    private class AddTabButton extends JButton implements ActionListener {
        public AddTabButton() {
            setText("+");
            setToolTipText("Add new tab");
            setUI(new BasicButtonUI());
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setContentAreaFilled(false);
            setFocusable(false);
            setBackground(new Color(200, 200, 200));
            addMouseListener(buttonMouseListener);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            browser.addPage(browser.getConfig().getProperty("homepage"));
        }

        //we don't want to update UI for this button
        public void updateUI() {}
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

