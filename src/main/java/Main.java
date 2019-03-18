
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Main {
final static int PORT = 70;
    public static void main(String[] args) throws IOException {
        SocketConnection socketConnection = new SocketConnection();
        Nick nick = setNick(socketConnection.getMulticast_group(), PORT, socketConnection.getSocket());
        Room room = new Room(setRoom());
        Sender sender = new Sender(nick, room);
        Receiver receiver = new Receiver(nick, room, socketConnection.getMulticast_group());

        try {
            sender.thread.join();
            receiver.thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String setRoom() {
        Scanner in = new Scanner(System.in);
        String roomName = "";

        while (roomName.length() <= 0) {
            System.out.println("Wybierz pokój: ");
            roomName = in.nextLine();
            System.out.println("Twój pokój to : " + roomName);
        }
        return roomName;
    }

    private static Nick setNick(InetAddress multicast_group, int port, MulticastSocket socket) {
        Scanner in = new Scanner(System.in);
        System.out.println("Wybierz nick: ");
        Nick nick = new Nick("NICK " + in.nextLine());
        System.out.println("Twój nick to : " + nick.getNick());

        DatagramPacket sNick = new DatagramPacket(
                nick.getNick().getBytes(), nick.getNick().length(),
                multicast_group, port
        );

        try {
            socket.send(sNick);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            String received = "";
            try {

                socket.setSoTimeout(1000);
                socket.receive(datagramPacket);
                received = new String(datagramPacket.getData(),
                        datagramPacket.getOffset(),
                        datagramPacket.getLength());
            } catch (SocketTimeoutException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (received.equals(nick.getNick() + " BUSY")) {
                System.out.println(received);
                nick = setNick(multicast_group, port, socket);
            }
        }
        return nick;
    }
}