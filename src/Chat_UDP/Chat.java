package Chat_UDP;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.Objects;

public class Chat extends JFrame{
    private JPanel contentPane;
    private JButton btnSend;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JTextArea txtChatArea;
    private JTextArea txtMessage;
    private JTextArea txtInputName;
    private JTextArea txtName;
    private JTextField txtFieldIP;
    private JTextField txtNetIfName;
    private JTextField txtFieldPort;
    private final int maxNameChar =  10;

    public String getIpAddress() { return txtFieldIP.getText(); }
    public int getPort() { return Integer.parseInt(txtFieldPort.getText()); }
    public String getNetIfName() { return txtNetIfName.getText(); }
    public JTextArea getTxtChatArea() { return txtChatArea; }

    public static void main(String[] args) {
        Chat chat = new Chat();
        Thread thread = new Thread(new Receiver(chat.getTxtChatArea(), chat.getIpAddress(), chat.getPort(), chat.getNetIfName()));
        thread.start();
    }
    private boolean checkForMessages() throws IOException {
        if(        Objects.equals(txtMessage.getText(), "Please enter something to say. \nUse SHIFT-ENTER for new row.")
                || Objects.equals(txtMessage.getText(), "Please enter something to say. \nUse SHIFT-ENTER for new row.\n")
                || Objects.equals(txtMessage.getText(), "Try again with a name.")
                || Objects.equals(txtMessage.getText(), "\n")
                || Objects.equals(txtMessage.getText(), "")){
            txtMessage.setText("Please enter something to say. \nUse SHIFT-ENTER for new row.");
            txtMessage.setEditable(true);
            return false;
        }
        return true;
    }
    private boolean checkIfChar(){
        for (char name:txtInputName.getText().toCharArray()) {
            if(Character.isLetterOrDigit(name)){
                return true;
            }
        }
        return false;
    }
    private void sendMessage(String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(getIpAddress());
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, getPort());
        socket.send(packet);
        socket.close();
    }

    Chat() {
        setContentPane(contentPane);
        pack();
        setTitle("Chat 0.2 Multicast version");
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        btnDisconnect.setEnabled(false);
        btnSend.setEnabled(false);
        txtMessage.setEditable(false);

        btnConnect.addActionListener(e -> {
            if(Objects.equals(txtInputName.getText(), "Enter your name") || !checkIfChar()) {
                txtMessage.setText("Try again with a name.");
            }
            else{
                txtName.append(txtInputName.getText() + "\n");                                                          // Will change with TCP/IP
                btnSend.setEnabled(true);
                txtMessage.setEditable(true);
                btnConnect.setEnabled(false);
                btnDisconnect.setText("Off kommer senare");
                btnDisconnect.setEnabled(false);
                txtInputName.setEditable(false);
                txtInputName.setEnabled(false);
                txtFieldIP.setEnabled(false);
                txtNetIfName.setEnabled(false);
                txtFieldPort.setEnabled(false);
            }
        });

        btnSend.addActionListener(e -> {
            try {
                if (checkForMessages()){
                    sendMessage("\n" + txtInputName.getText() + " says: " + txtMessage.getText());
                    txtMessage.setEditable(true);
                    txtMessage.setText("");
                }
            } catch (IOException ex) {
                System.out.println(txtMessage);
                ex.printStackTrace();
            }
        });
        txtInputName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                txtInputName.setText("");
            }
        });

        txtInputName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(txtInputName.getText().length() > maxNameChar + 1){
                    e.consume();
                    String shorterName = txtInputName.getText().substring(0, maxNameChar);
                    txtInputName.setText(shorterName);
                }
                else if (txtInputName.getText().length() > maxNameChar){
                    txtMessage.setText("Please only 10 letter name");
                    e.consume();
                }
                super.keyTyped(e);
            }
        });

        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER && (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0){
                        txtMessage.append("\n");
                        e.consume();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_ENTER && checkForMessages()) {
                        sendMessage("\n" + txtInputName.getText() + " says: " + txtMessage.getText());
                        txtMessage.setText("");
                        e.consume();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
