package sku.dnsresolver;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;
import sku.dnsresolver.ui.MainWindow;

import javax.swing.*;

import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("unchecked")
public class ApplicationDriver extends JFrameDriver {

    public ApplicationDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void hasLabelWithString(String ipAddress) {
        new JLabelDriver(this, named(MainWindow.RESOLVED_LABEL_NAME))
                .hasText(equalTo(ipAddress));
    }

    public void resolveDomainName(String domainName, String serverIp, String serverPort) {
        textField(MainWindow.DOMAIN_TEXTFIELD_NAME).replaceAllText(domainName);
        textField(MainWindow.SERVER_IP_TEXTFIELD_NAME).replaceAllText(serverIp);
        textField(MainWindow.SERVER_PORT_TEXTFIELD_NAME).replaceAllText(serverPort);
        resolveButton().click();
    }

    private JTextFieldDriver textField(String fieldName) {
        JTextFieldDriver newItemId =
                new JTextFieldDriver(this, JTextField.class, named(fieldName));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JButtonDriver resolveButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.RESOLVE_BUTTON_NAME));
    }
}
