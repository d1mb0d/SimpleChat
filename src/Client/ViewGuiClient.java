package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class ViewGuiClient {
    private final Client client;
    private JFrame frame = new JFrame("Chat");
    private JTextArea messages = new JTextArea(30, 20);
    private JTextArea users = new JTextArea(30, 15);
    private JPanel panel = new JPanel();
    private JTextField textField = new JTextField(40);
    private JButton buttonDisable = new JButton("Disconnect");
    private JButton buttonConnect = new JButton("Connect");

    public ViewGuiClient(Client client) {
        this.client = client;
    }
    protected void initClientFrame() {
        messages.setEditable(false);
        users.setEditable(false);
        frame.add(new JScrollPane(messages), BorderLayout.CENTER);
        frame.add(new JScrollPane(users), BorderLayout.EAST);
        panel.add(textField);
        panel.add(buttonConnect);
        panel.add(buttonDisable);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        messages.setBackground(Color.ORANGE);
        users.setBackground(Color.CYAN);
        panel.setBackground(Color.YELLOW);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnected()) {
                    client.disconnectClient();
                }
                System.exit(0);
            }
        });

        frame.setVisible(true);

        buttonDisable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.disconnectClient();
            }
        });

        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.connectToServer();
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessageToServer(textField.getText());
                textField.setText("");
            }
        });
    }
    protected void addMessage(String text) {
        messages.append(text);
    }
    protected void refreshUserList(Set<String> listUsers) {
        users.setText("");
        if (client.isConnected()) {
            StringBuilder text = new StringBuilder("List of users:\n");
            for (String user : listUsers) {
                text.append(user + "\n");
            }
            users.append(text.toString());
        }
    }
    protected String getServerAddressFromOptionPane() {
        while (true) {
            String ServerAddress = JOptionPane.showInputDialog(
                    frame, "Enter the server address:",
                    "Entering the server address",
                    JOptionPane.QUESTION_MESSAGE
            );
            return ServerAddress.trim();
        }
    }
    protected int getPortServerFromOptionPane() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame, "Enter the server port:",
                    "Entering the server port",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame, "An incorrect server port has been entered. Try again.",
                        "Server port input error", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    protected String getUsername() {
        return JOptionPane.showInputDialog(
                frame, "Enter the user name:",
                "Entering the user name",
                JOptionPane.QUESTION_MESSAGE
        );
    }
    protected void errorDialogWindow(String text) {
        JOptionPane.showMessageDialog(
                frame, text,
                "Error", JOptionPane.ERROR_MESSAGE
        );
    }
}
