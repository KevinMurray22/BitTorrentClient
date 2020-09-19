import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public class MessageHandler {
    Peer peer;
    byte[] myPeerId;
    Socket socket;
    OutputStream out;
    InputStream in;

    public MessageHandler(Peer peer) throws UnknownHostException {
        this.peer = peer;
        myPeerId = generatePeerId();
        byte[] currentIP = {73,(byte)157,96,95};
        InetAddress currentAddress= InetAddress.getByAddress(currentIP);
        InetAddress remoteAddress = InetAddress.getByAddress(peer.getAddress());
        InetAddress connectingAddress;
        if(remoteAddress.equals(currentAddress)){
            byte[] local = {127,0,0,1};
            connectingAddress = InetAddress.getByAddress(local);
            System.out.println("Remote address equals current address.");
        }
        else
            connectingAddress = remoteAddress;

        String addressString = Arrays.toString(connectingAddress.getAddress());
        System.out.println("Trying to connect to: " + addressString + ":" + peer.getPort());
        try {
            socket = new Socket(connectingAddress , peer.getPort());
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Connection refused by: " +  Arrays.toString(peer.getAddress()) + ":" + peer.getPort());
        }
        System.out.println("Connected to: " + addressString + ":" + peer.getPort());


    }

    public boolean executeHandshake() throws IOException {
        Message myHandshake = new Message();
        myHandshake.buildHandshake(myPeerId, peer.getSha1());
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        bOut.write(myHandshake.getMessage());
        bOut.writeTo(out);
        bOut.reset();
        byte[] buf = new byte[68];
        byte[] temp = new byte[7];
        try{
           in.readNBytes(buf, 0, 68);
            //in.readNBytes(temp, 0, 7);
           // myHandshake.handshakeParse(buf);
            //byte[] temp = in.readAllBytes();
           // buf = Arrays.copyOfRange(temp, 0, 68);
            myHandshake.handshakeParse(buf);
           // System.out.printf(Arrays.toString(temp));

        }
        catch (EOFException e){
            System.out.println("End of input stream reached");
        }

        if(Arrays.equals(peer.getSha1(),myHandshake.getSha1()))
            return true;
        else
            return false;
    }

    public void close() throws IOException {
        socket.close();
    }

    public byte[] generatePeerId(){
        byte[] peerId = new byte[20];
        new SecureRandom().nextBytes(peerId);
        peerId[0] = '-';
        peerId[1] = 'K';
        peerId[2] = 'T';
        peerId[3] = '0';
        peerId[4] = '0';
        peerId[5] = '0';
        peerId[6] = '1';
        peerId[7] = '-';

        return peerId;
    }

    public void readMessages() throws IOException {
        byte[] bLen = new byte[4];
        byte[] buf;
        byte[] byteMsg;

        while(in.available()!=0) {
            try {
                in.readNBytes(bLen, 0, 4);
                int length = ByteBuffer.wrap(Arrays.copyOfRange(bLen, 0, 4)).getInt();
                buf = new byte[length];
                in.readNBytes(buf, 0, length);
                byteMsg = new byte[bLen.length + buf.length];
                System.arraycopy(bLen, 0, byteMsg, 0, bLen.length);
                System.arraycopy(buf, 0, byteMsg, bLen.length, buf.length);
                Message message = new Message(byteMsg);
                System.out.println("Received message in bytes: " + Arrays.toString(byteMsg));
                handle(message);

            } catch (EOFException e) {
                System.out.println("End of input stream reached");
            }

        }
    }

    public void sendMessage(Message message) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        bOut.write(message.getMessage());
        bOut.writeTo(out);
        bOut.reset();
    }

    public void sendInterested() throws IOException {
        Message message = new Message();
        message.buildInterested();
        sendMessage(message);
    }

    public void sendUnChoke() throws IOException {
        Message message = new Message();
        message.buildUnChoke();
        sendMessage(message);
    }

    public void requestPiece(int index, int begin, int length) throws IOException {
        System.out.println("Requesting piece: " + index + " with beginning: " + begin + " and length: " + length);

        Message message = new Message();
        message.buildRequest(index, begin, length);
        sendMessage(message);
    }

    private void handle(Message message) {
        System.out.println("Handling message of id: " + message.getId());
        switch (message.getId()) {
            case 0 -> peer.setPeer_choking(true);
            case 1 -> peer.setPeer_choking(false);
            case 2 -> peer.setPeer_interested(true);
            case 3 -> peer.setPeer_interested(false);
            case 4 -> peer.setBitInBitfield(ByteBuffer.wrap(message.getPayload()).getInt());
            case 5 -> peer.setBitfield(message.getPayload());
            case 7 -> writeToString(message.getPayload());
        }

    }

    public void writeToString(byte[] payload){
        byte[] block = Arrays.copyOfRange(payload, 8, payload.length);
        System.out.println("Downloaded block: " + Arrays.toString(block));
    }

    public byte[] toByteArrayProper(int num){
        int digits = 0;
        for (int x = num; x>0; x/=10)
            digits++;
        byte[] byteArray = new byte[digits];
        for (int x = byteArray.length-1; x>=0; x++){
            byteArray[x] = (byte)(num%10);
            num/=10;
        }
        return byteArray;
    }
}
