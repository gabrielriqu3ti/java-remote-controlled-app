package org.guiapp.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class App extends JFrame {

    JTextField tfInputText;
    JButton btCopyText;
    JLabel lbCopiedText;

    public App() {
        setTitle(getClass().getCanonicalName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setName("App");
        setPreferredSize(new Dimension(200, 100));
        JPanel pn = new JPanel();
        pn.setName("mainPanel");
        pn.setLayout(new BoxLayout(pn, BoxLayout.PAGE_AXIS));
        pn.add(getTfInputText());
        pn.add(getBtCopyText());
        pn.add(getLbCopiedText());
        add(pn);
        pack();
    }

    private JTextField getTfInputText() {
        if (tfInputText == null) {
            tfInputText = new JTextField("Insert text here...");
            tfInputText.setName(String.join(".", getName(), "JTextField", "inputText"));
        }
        return tfInputText;
    }

    private JButton getBtCopyText() {
        if (btCopyText == null) {
            btCopyText = new JButton("Copy");
            btCopyText.setName(String.join(".", getName(), "JButton", "copyText"));
            btCopyText.addActionListener((e) -> getLbCopiedText().setText(getTfInputText().getText()));
        }
        return btCopyText;
    }

    private JLabel getLbCopiedText() {
        if (lbCopiedText == null) {
            lbCopiedText = new JLabel("Typing...");
            lbCopiedText.setName(String.join(".", getName(), "JLabel", "copiedText"));
        }
        return lbCopiedText;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame app = new App();
            app.setVisible(true);
        });
    }
}
