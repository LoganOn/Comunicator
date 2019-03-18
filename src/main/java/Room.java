import java.util.ArrayList;
import java.util.List;

class Room {

    private List<String> nicks = new ArrayList<>();
    private String room;

    Room(String room) {
        this.room = room;
    }

    synchronized void setRoom(String room) {
        this.room = room;
    }

    synchronized String getRoom() {
        return room;
    }

    synchronized void resetList() {
        nicks.clear();
    }

    synchronized List<String> getNicksInRoom() {
        return nicks;
    }

    void addToList(String nick) {
        nicks.add(nick);
    }


}