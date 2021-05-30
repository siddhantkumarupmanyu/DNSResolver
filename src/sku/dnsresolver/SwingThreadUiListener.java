package sku.dnsresolver;

import sku.dnsresolver.ui.UiListener;

import javax.swing.*;

public class SwingThreadUiListener implements UiListener {

    private final UiListener delegate;

    public SwingThreadUiListener(UiListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void responseText(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.responseText(text);
            }
        });
    }
}

// this class is a decorator
