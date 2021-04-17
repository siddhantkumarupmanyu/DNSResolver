package sku.dnsresolver;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import sku.dnsresolver.MainWindow;

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
}
