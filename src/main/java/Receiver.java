
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver extends Thread {

    Thread thread;
    private Nick nick;
    private InetAddress inetAddress;
    private int port = 70;
    private Room room;
    private SocketConnection socketConnection;

    Receiver(Nick nick, Room room, InetAddress inetAddress) {
        this.nick = nick;
        this.room = room;
        this.inetAddress = inetAddress;
        thread = new Thread(this);
        thread.start();
        socketConnection = new SocketConnection();
    }

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        while (true) {
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            try {
               // assert socketConnection.getSocket() != null;
                socketConnection.getSocket().receive(datagramPacket);
                String received = new String(datagramPacket.getData(),
                        datagramPacket.getOffset(), datagramPacket.getLength());

                if (received.toUpperCase().equals(nick.getNick().toUpperCase())) {
                    String nickBusy = received + " BUSY";
                    DatagramPacket busy = new DatagramPacket(nickBusy.getBytes(), nickBusy.length(),
                            inetAddress, port);
                    socketConnection.getSocket().send(busy);
                }
                if (received.split(" ")[0].equals("JOIN"))
                    if (received.split(" ")[1].equals(room.getRoom())) {
                        System.out.println(received.split(" ")[2] + " join to room.");
                    }

              //dodajemy do listy
                if (received.split(" ")[0].equals("ROOM"))
                    if (received.split(" ")[1].equals(room.getRoom()))
                        room.addToList(received.split(" ")[2]);

                if (received.split(" ").length > 3) {
                    //drukowanie danych
                    if (received.split(" ")[0].equals("MSG"))
                        if (received.split(" ")[2].equals(room.getRoom() + ":") &&
                                !received.split(" ")[3].toUpperCase().equals("WHOIS") &&
                                !received.split(" ")[3].toUpperCase().equals("LEFT"))
                            System.out.println(received);

                    if (received.split(" ")[2].equals(room.getRoom() + ":")) {
                        //drukowanie kto opuscil
                        if (received.split(" ")[3].toUpperCase().equals("LEFT") &&
                                !received.split(" ")[1].equals(nick.getNick().substring(5)))
                            System.out.println(received.split(" ")[1] + " left room: " + room.getRoom());
                            //drukowanie kto jest
                        else if (received.split(" ")[3].toUpperCase().equals("WHOIS")) {
                            if (received.split(" ")[4].toUpperCase().equals(room.getRoom())) {
                                String whoIsMessage = "ROOM " + room.getRoom() + " " + nick.getNick().substring(5);
                                DatagramPacket dp = new DatagramPacket(whoIsMessage.getBytes(), whoIsMessage.length(),
                                        inetAddress, port);
                                socketConnection.getSocket().send(dp);
                            } else if (received.split(" ")[1].equals(nick.getNick().substring(5))) {
                                throw new ArrayIndexOutOfBoundsException();
                            }
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException out) {
                System.out.println("Podaj prawidłowy pokój");
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}