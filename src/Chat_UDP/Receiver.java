package Chat_UDP;

import javax.swing.*;
import java.io.IOException;
import java.net.*;

public class Receiver implements Runnable{
    private final JTextArea txtChatArea;
    private final String ipAddress;
    private final int port;
    private final String netIfName;

    public Receiver(JTextArea txtChatArea, String ipAddress, int port, String netIfName){
        this.txtChatArea = txtChatArea;
        this.ipAddress = ipAddress;
        this.port = port;
        this.netIfName = netIfName;
    }

    public void receiver() throws IOException {
        byte[] buffer = new byte[1024];
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress broadcast = InetAddress.getByName(ipAddress);
        InetSocketAddress group = new InetSocketAddress(broadcast, port);
        NetworkInterface netIf = NetworkInterface.getByName(netIfName);
        socket.joinGroup(group, netIf);
                                                                                                                        //noinspection InfiniteLoopStatement
        while(true){
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData(), 0, packet.getLength());
            txtChatArea.append(msg);
        }
    }

    @Override
    public void run() {
        try {
            receiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
