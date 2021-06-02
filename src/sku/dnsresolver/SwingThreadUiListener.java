package sku.dnsresolver;

import sku.dnsresolver.ui.UiListener;

import javax.swing.*;

public class SwingThreadUiListener implements UiListener {

    private final UiListener delegate;

    public SwingThreadUiListener(UiListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void responseText(String query, String formattedResponse) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.responseText(query, formattedResponse);
            }
        });
    }
}

// this class is a decorator
