package sku.dnsresolver.ui;

import sku.dnsresolver.util.Announcer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "DNS Resolver";
    public static final String RESOLVED_LABEL_NAME = "resolved-label";
    public static final String DOMAIN_TEXTFIELD_NAME = "domain-name-textfield";
    public static final String SERVER_IP_TEXTFIELD_NAME = "server-ip-textfield";
    public static final String SERVER_PORT_TEXTFIELD_NAME = "server-port-textfield";
    public static final String RESOLVE_BUTTON_NAME = "resolve-button";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    private JLabel resolvedIp;

    public MainWindow() {
        super("DNS Resolver");
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeControls(), responsePanel());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JPanel controls, JPanel response) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(response, BorderLayout.CENTER);
    }

    private JPanel makeControls() {
        final JTextField domainNameField = domainNameField();
        final JTextField serverIpField = serverIpField();
        final JFormattedTextField serverPortField = serverPortField();

        JPanel controls = new JPanel(new FlowLayout());
        controls.add(domainNameField);
        controls.add(serverIpField);
        controls.add(serverPortField);

        JButton resolveButton = new JButton("Resolve");
        resolveButton.setName(RESOLVE_BUTTON_NAME);

        resolveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().resolve(domainName(), serverIp(), serverPort());
            }

            private String domainName() {
                return domainNameField.getText();
            }

            private String serverIp() {
                return serverIpField.getText();
            }

            private String serverPort() {
                return String.valueOf(serverPortField.getValue());
            }

        });
        controls.add(resolveButton);

        return controls;
    }

    private JPanel responsePanel() {
        JLabel label = new JLabel("Resolved Ip: ");
        resolvedIp = new JLabel();
        resolvedIp.setName(RESOLVED_LABEL_NAME);
        resolvedIp.setBorder(new LineBorder(Color.BLACK));

        JPanel response = new JPanel(new FlowLayout());
        response.add(label);
        response.add(resolvedIp);
        return response;
    }

    private JTextField domainNameField() {
        JTextField domainNameField = new JTextField();
        domainNameField.setColumns(30);
        domainNameField.setName(DOMAIN_TEXTFIELD_NAME);
        return domainNameField;
    }

    private JTextField serverIpField() {
        JTextField serverIpField = new JTextField();
        serverIpField.setColumns(15);
        serverIpField.setName(SERVER_IP_TEXTFIELD_NAME);
        return serverIpField;
    }

    private JFormattedTextField serverPortField() {
        JFormattedTextField serverPortField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        serverPortField.setColumns(7);
        serverPortField.setName(SERVER_PORT_TEXTFIELD_NAME);
        return serverPortField;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }

    public void setLabelText(String string) {
        resolvedIp.setText(string);
    }
}
