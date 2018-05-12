package queries;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class is a dialog for entering a name - new PC file name ending with suffix ".sql".
 *
 * @author Vladimír Župka 2016
 */
public class Q_GetTextFromDialog extends JDialog {

    Container cont;
    JPanel panel = new JPanel();
    JPanel titlePanel = new JPanel();
    JPanel dataPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    GroupLayout layout = new GroupLayout(panel);
    JLabel titleLabel = new JLabel();
    JLabel parentPathLabel = new JLabel();
    JLabel newNameLabel = new JLabel();
    JTextField textField = new JTextField();
    JButton cancel = new JButton("Cancel");
    JButton enter = new JButton("Enter");
    int windowWidth = 460;
    int windowHeight = 150;

    String returnedText;

    //static final Color DIM_BLUE = Color.getHSBColor(0.60f, 0.2f, 0.5f); // blue little saturated dim (gray)
    static final Color DARKER_BLUE = new Color(50, 60, 160);

    /**
     * Constructor
     *
     * @param windowTitle
     */
    public Q_GetTextFromDialog(String windowTitle) {
        super();
        super.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        super.setTitle(windowTitle);
    }

    public String getTextFromDialog(String parentTitle, String newNameTitle, String parentPathString, String fileName,
            int currentX, int currentY) {

        titleLabel.setText(parentTitle);
        titleLabel.setBackground(DARKER_BLUE);

//        parentPathLabel.setText(parentPathString);
//        parentPathLabel.setBackground(DARKER_BLUE);

        newNameLabel.setText(newNameTitle);
//        newNameLabel.setForeground(DARKER_BLUE);

        textField.setMaximumSize(new Dimension(300, 20));
        textField.setText(fileName);

        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.LINE_AXIS));

        dataPanel.add(newNameLabel);
        dataPanel.add(textField);

        buttonPanel.add(cancel);
        buttonPanel.add(enter);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        panel.setLayout(layout);

        layout.setAutoCreateGaps(false);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout
                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(titleLabel)
                                .addComponent(dataPanel)
                                .addGap(5)
                                .addComponent(buttonPanel)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel)
                                .addComponent(dataPanel)
                                .addGap(5)
                                .addComponent(buttonPanel)
                        )
        );

        // Listeners
        enter.addActionListener(en -> {
            returnedText = textField.getText();
            dispose();
        });

        textField.addActionListener(tf -> {
            returnedText = textField.getText();
            dispose();
        });

        cancel.addActionListener(en -> {
            returnedText = null;
            dispose();
        });

        cont = getContentPane();
        cont.add(panel);

        setSize(windowWidth, windowHeight);
        setLocation(currentX, currentY);
        setVisible(true);
        pack();

        return returnedText;
    }
}
