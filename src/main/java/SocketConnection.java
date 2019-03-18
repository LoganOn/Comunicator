import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SocketConnection {

    //reprezentuje IP
    private InetAddress multicast_group;
    private MulticastSocket socket;

    public SocketConnection() {
        try {
            this.socket = new MulticastSocket(70);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //tworzy obiekt na podstaiwe adresu IP
            this.multicast_group = InetAddress.getByName("239.255.255.255");
            socket.joinGroup(multicast_group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public InetAddress getMulticast_group() {
        return multicast_group;
    }

    public MulticastSocket getSocket() {
        return socket;
    }
}
