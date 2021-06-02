package sku.dnsresolver.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class ExpandableResult extends JPanel {

    public ExpandableResult() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    public void addItem(final String heading, String result) {

        final ExpandableItem item = new ExpandableItem(heading, result);

        add(Box.createRigidArea(new Dimension(0, 2)));
        add(item);
        add(Box.createRigidArea(new Dimension(0, 2)));

        revalidate();
    }


    private static class ExpandableItem extends JPanel {

        @SuppressWarnings("FieldCanBeLocal")
        private final JTextField headingField;
        private final JTextArea resultArea;

        private boolean isCollapsed = true;

        public ExpandableItem(String heading, String result) {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            headingField = createHeadingField(heading);
            resultArea = createResultTextArea(result);

            add(headingField);

            headingField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    expandOrCollapse();
                }
            });
        }

        public void expandOrCollapse() {
            if (isCollapsed) {
                add(resultArea);
            } else {
                remove(1);
            }

            isCollapsed = !isCollapsed;

            revalidate();
            repaint();
        }

        private JTextField createHeadingField(String heading) {
            final JTextField headingField = new JTextField(heading);

            headingField.setName(heading);

            headingField.setHighlighter(null);
            headingField.setEditable(false);

            headingField.setBackground(Color.WHITE);

            headingField.setHorizontalAlignment(JTextField.CENTER);

            headingField.setPreferredSize(new Dimension(headingField.getPreferredSize().width, 50));
            headingField.setMaximumSize(new Dimension(headingField.getMaximumSize().width, 50));

            return headingField;
        }

        private JTextArea createResultTextArea(String result) {
            final JTextArea resultArea = new JTextArea(result);

            resultArea.setName(MainWindow.RESPONSE_TEXT_AREA);

            resultArea.setEditable(false);
            resultArea.setMaximumSize(new Dimension(resultArea.getMaximumSize().width, resultArea.getPreferredSize().height));
            return resultArea;
        }

    }

}

