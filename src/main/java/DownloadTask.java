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

    public DownloadTask(Peer peer){
        this.peer = peer;
    }

    public void run(){
       // try {
/*
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
            Socket socket = new Socket(connectingAddress , peer.getPort());
            System.out.println("Connected to: " + addressString + ":" + peer.getPort());
            DataOutputStream out = new DataOutputStream((socket.getOutputStream()));
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            DataInputStream in = new DataInputStream(socket.getInputStream());


            Message handshake = new Message();
            handshake.buildHandshake(getPeerId(), peer.getSha1());
            //byte[] handshake = Message.buildHandshake(getPeerId(), peer.getSha1());
            OutputStream out = socket.getOutputStream();
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bOut.write(handshake.getMessage());
            bOut.writeTo(out);
            bOut.reset();
            InputStream in = socket.getInputStream();


            System.out.println("");
            for (byte foo:handshake.getMessage()
                 ) {
                    System.out.printf("%02X ", foo);
            }
            System.out.println("");

            out.write(handshake.getMessage());
           // System.out.println("Bytes written: " + out.size());
            Message recMsg = new Message();
            byte[] temp = new byte[68];
            try{
                in.readNBytes(temp, 0, 68);
                recMsg.handshakeParse(temp);

            }
            catch (EOFException e){
                System.out.println("End of input stream reached");
            }

            System.out.println("");
            System.out.print("Handshake: ");
            System.out.printf(new String(recMsg.getMessage(), StandardCharsets.ISO_8859_1));
            System.out.println("");
            for (byte foo:recMsg.getMessage()
            ) {
                System.out.printf("%02X ", foo);
            }
            System.out.println("");
            System.out.println("");
/*
            Message bitfield = new Message();
            temp = new byte[68];
            try{
                in.readAllBytes();
                bitfield = new Message(temp);

            }
            catch (EOFException e){
                System.out.println("End of input stream reached");
            }






         //   socket.close();
            System.out.println("End of successful thread.");
        } catch (IOException e) {
            System.out.println("Connection refused by: " +  Arrays.toString(peer.getAddress()) + ":" + peer.getPort());
            //e.printStackTrace();
        }

 */
        try {
            MessageHandler handler = new MessageHandler(peer);
            System.out.println(handler.executeHandshake());
           handler.readMessages();
            System.out.println(Arrays.toString(peer.getBitfield()));
            handler.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("End of successful thread.");
    }

    public void messageHandler(Message message){
        if(message.getId() == 5){
            bitfield = Arrays.copyOfRange(message.getPayload(), 0, message.getMsgLen() -1);
        }
    }

}
