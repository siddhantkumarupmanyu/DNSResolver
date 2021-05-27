package sku.dnsresolver;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;
import sku.dnsresolver.ui.MainWindow;

import javax.swing.*;

import static org.hamcrest.Matchers.containsString;

@SuppressWarnings("unchecked")
public class ApplicationDriver extends JFrameDriver {

    public ApplicationDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(ComponentDriver.named(MainWindow.MAIN_WINDOW_NAME), ComponentDriver.showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void responseTextAreaContains(String ipAddress) {
        new JTextComponentDriver<>(this, JTextArea.class, ComponentDriver.named(MainWindow.RESPONSE_TEXT_AREA))
                .hasText(containsString("address: " + ipAddress));
    }

    public void resolveDomainName(String domainName, String serverIp, String serverPort, boolean recursive) {
        setRequired(domainName, serverIp, serverPort);
        setOptions(recursive);
        resolveButton().click();
    }

    private void setRequired(String domainName, String serverIp, String serverPort) {
        textField(MainWindow.DOMAIN_TEXTFIELD_NAME).replaceAllText(domainName);
        textField(MainWindow.SERVER_IP_TEXTFIELD_NAME).replaceAllText(serverIp);
        textField(MainWindow.SERVER_PORT_TEXTFIELD_NAME).replaceAllText(serverPort);
    }

    private void setOptions(boolean recursive) {
        JCheckBoxDriver driver = new JCheckBoxDriver(this, JCheckBox.class, ComponentDriver.named(MainWindow.RECURSIVE_CHECKBOX_NAME));
        if (!recursive) {
            driver.click();
        }
    }

    private JTextFieldDriver textField(String fieldName) {
        JTextFieldDriver newItemId =
                new JTextFieldDriver(this, JTextField.class, ComponentDriver.named(fieldName));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JButtonDriver resolveButton() {
        return new JButtonDriver(this, JButton.class, ComponentDriver.named(MainWindow.RESOLVE_BUTTON_NAME));
    }

}
