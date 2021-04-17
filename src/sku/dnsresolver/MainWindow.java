package sku.dnsresolver;

import javax.swing.*;

public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "DNS Resolver";

    public MainWindow() {
        super("DNS Resolver");
        setName(MAIN_WINDOW_NAME);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
