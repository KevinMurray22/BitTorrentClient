import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class DownloadTask implements Runnable {
    Peer peer;
    byte[] bitfield;
    boolean[] pieceArray;
    int pieceLength;

    public DownloadTask(Peer peer, boolean[] pieceArray, int pieceLength){

        this.peer = peer;
        this.pieceArray = pieceArray;
        this.pieceLength = pieceLength;
    }

    public void run(){
        try {
            System.out.println("Current piece array: " + Arrays.toString(pieceArray));
            MessageHandler handler = new MessageHandler(peer);
            if(handler.executeHandshake()) {

                handler.readMessages();
                System.out.println(peer);
                handler.sendUnChoke();
                handler.readMessages();
                handler.sendInterested();
                handler.readMessages();
                handler.requestPiece(getNextPieceIndex(), 0, (int)Math.pow(2.0,14.0));
                handler.readMessages();
            }
            handler.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("End of successful thread.");
    }

    public int getNextPieceIndex(){
        for (int i = 0; i < pieceArray.length; i++){
            if(!pieceArray[i])
                return i;
        }
            return -1;
    }

}
