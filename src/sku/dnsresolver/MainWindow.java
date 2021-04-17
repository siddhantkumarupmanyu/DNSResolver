package sku.dnsresolver;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "DNS Resolver";
    public static final String RESOLVED_LABEL_NAME = "resolved-label";

    private final JLabel resolvedIp = createLabel("");

    public MainWindow() {
        super("DNS Resolver");
        setName(MAIN_WINDOW_NAME);
        add(resolvedIp);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(RESOLVED_LABEL_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }
}
