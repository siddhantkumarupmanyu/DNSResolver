package sku.dnsresolver;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTextComponentDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import sku.dnsresolver.ui.MainWindow;

import javax.swing.*;

import static org.hamcrest.Matchers.containsString;

@SuppressWarnings("unchecked")
public class ApplicationDriver extends JFrameDriver {

    public ApplicationDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void responseTextAreaContains(String ipAddress) {
        new JTextComponentDriver<>(this, JTextArea.class, named(MainWindow.RESPONSE_TEXT_AREA))
                .hasText(containsString("address: " + ipAddress));
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
