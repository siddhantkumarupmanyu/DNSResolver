package sku.dnsresolver.ui;

import org.apache.commons.lang.StringUtils;
import sku.dnsresolver.util.Announcer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame implements UiListener {
    public static final String MAIN_WINDOW_NAME = "DNS Resolver";
    public static final String RESPONSE_TEXT_AREA = "response-text-area";
    public static final String DOMAIN_TEXTFIELD_NAME = "domain-name-textfield";
    public static final String SERVER_IP_TEXTFIELD_NAME = "server-ip-textfield";
    public static final String SERVER_PORT_TEXTFIELD_NAME = "server-port-textfield";
    public static final String RESOLVE_BUTTON_NAME = "resolve-button";
    public static final String RECURSIVE_CHECKBOX_NAME = "recursive-checkbox";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    private JTextArea response;

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
        final JTextField serverPortField = serverPortField();

        JPanel requiredControls = new JPanel(new FlowLayout());
        requiredControls.add(domainNameField);
        requiredControls.add(serverIpField);
        requiredControls.add(serverPortField);

        final JCheckBox recursiveCheckbox = getRecursiveCheckbox();

        JPanel options = new JPanel(new FlowLayout());
        options.add(recursiveCheckbox);

        JButton resolveButton = new JButton("Resolve");
        resolveButton.setName(RESOLVE_BUTTON_NAME);

        resolveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().resolve(domainName(), serverIp(), serverPort(), recursion());
            }

            private String domainName() {
                return domainNameField.getText();
            }

            private String serverIp() {
                return serverIpField.getText();
            }

            private String serverPort() {
                return serverPortField.getText();
            }

            private boolean recursion() {
                return recursiveCheckbox.isSelected();
            }

        });

        requiredControls.add(resolveButton);

        JPanel controls = new JPanel(new BorderLayout());
        controls.add(requiredControls, BorderLayout.NORTH);
        controls.add(options, BorderLayout.SOUTH);

        return controls;
    }

    private JCheckBox getRecursiveCheckbox() {
        final JCheckBox checkBox = new JCheckBox("Recursive");
        checkBox.setSelected(true);
        checkBox.setName(RECURSIVE_CHECKBOX_NAME);
        return checkBox;
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

    private JTextField serverPortField() {
        JTextField serverPortField = new JTextField();
        serverPortField.setColumns(7);
        serverPortField.setName(SERVER_PORT_TEXTFIELD_NAME);
        return serverPortField;
    }

    private JPanel responsePanel() {
        response = new JTextArea();
        response.setName(RESPONSE_TEXT_AREA);
        response.setEditable(false);
        response.setBackground(Color.WHITE);
        response.setColumns(50);
        response.setRows(25);

        JScrollPane scroll = new JScrollPane(response);

        JPanel response = new JPanel(new FlowLayout());
        response.add(scroll);
        return response;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }

    @Override
    public void responseText(String text) {
        appendTextToResponse(text);
    }

    private void appendTextToResponse(String text) {
        response.append(horizontalLine());
        response.append(text);
    }

    private String horizontalLine() {
        return StringUtils.repeat("=", 50);
    }
}
