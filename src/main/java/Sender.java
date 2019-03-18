import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.Scanner;

public class Sender extends Thread {

    Thread thread;
    private Nick nick;
    private Room room;
    private String multicast = "239.255.255.255";
    private int port = 70;
    private SocketConnection socketConnection;

    Sender(Nick nick, Room room) {
        this.nick = nick;
        this.room = room;
        thread = new Thread(this);
        thread.start();
        socketConnection = new SocketConnection();
    }

    private void joinRoom(InetAddress group, MulticastSocket socket) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wybierz ponownie pokój: ");
        room.setRoom(scanner.nextLine());
        String joinRoomNick = "JOIN " + room.getRoom() + " " + nick.getNick().substring(5);
        DatagramPacket joinRoom = new DatagramPacket(joinRoomNick.getBytes(), joinRoomNick.length(),
                group, socket.getLocalPort());
        try {
            socket.send(joinRoom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String nickName = nick.getNick().substring(5);
        Scanner in = new Scanner(System.in);
        boolean flag = false; //jezeli ktos jest ustawiamy na 1

        while (true) {
            String message = in.nextLine();

            if (message.toUpperCase().equals("WHOIS " + room.getRoom())) {
                room.resetList();
                flag = true;
            }

            message = "MSG " + nickName + " " + room.getRoom() + ": " + message;
            DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), socketConnection.getMulticast_group(), port);

            try {
                socketConnection.getSocket().send(dp);
                if (flag) {
                    flag = false;
                    sleep(500);
                    List<String> nicks = room.getNicksInRoom();
                    System.out.print("Ludzie w pokoju : " + nicks.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            //ponowne wybieranie pokoju po opuszczeniu
            if (message.split(" ")[message.split(" ").length - 1].toUpperCase().equals("LEFT")) {
                joinRoom(socketConnection.getMulticast_group(), socketConnection.getSocket());
            }
        }
    }
}