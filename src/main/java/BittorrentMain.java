import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;


import com.dampcake.bencode.*;
//big-buck-bunny.torrent
public class BittorrentMain {

    static public void main(String[] args) throws IOException, NoSuchAlgorithmException, URISyntaxException {

        byte[] contents = Files.readAllBytes(Paths.get("/home/kevin/IdeaProjects/BittorrentClient/src/main/resources/puppy.jpg.torrent"));
        Bencode bencode = new Bencode();
        Map<String, Object> torrent = bencode.decode(contents, Type.DICTIONARY);

        DatagramSocket socket = new DatagramSocket();


        byte[] connectRequest = buildConnectionRequest();

        URI uri = new URI(torrent.get("announce").toString());

        DatagramPacket packet = new DatagramPacket(connectRequest, connectRequest.length, InetAddress.getByName(uri.getHost()), uri.getPort() );

        socket.send(packet);


        packet = new DatagramPacket(connectRequest, connectRequest.length);

        System.out.println("\nWaiting...");
        socket.receive(packet);

        byte[] receivedMsg = packet.getData();
        byte[] action = Arrays.copyOfRange(receivedMsg, 0, 4);
        byte[] sentTransactionId = Arrays.copyOfRange(receivedMsg, 4, 8);
        byte[] connectionId = Arrays.copyOfRange(receivedMsg, 8, 16);

        byte[] announceRequest = buildAnnounceRequest(connectionId, torrent);

        packet = new DatagramPacket(announceRequest, announceRequest.length, InetAddress.getByName(uri.getHost()), uri.getPort());
        socket.send(packet);
        byte[] announceResponse = new byte[100];
        packet = new DatagramPacket(announceResponse, announceResponse.length);
        socket.receive(packet);
        receivedMsg = packet.getData();
        action = Arrays.copyOfRange(receivedMsg, 0, 4);
        sentTransactionId = Arrays.copyOfRange(receivedMsg, 4, 8);
        byte[] leechers = Arrays.copyOfRange(receivedMsg, 12, 16);
        byte[] seeders = Arrays.copyOfRange(receivedMsg, 16, 20);
        List<byte[]> addresses = group(Arrays.copyOfRange(receivedMsg, 20, receivedMsg.length), 6);
        for (byte foo:addresses.get(0)
             ) {
                    System.out.printf("%02X", foo);
                    System.out.print(" ");
        }
        byte[] myAddress = Arrays.copyOfRange(addresses.get(0),0, 4);
        short myPort = ByteBuffer.wrap(Arrays.copyOfRange(addresses.get(0),4, 6)).getShort();
        System.out.println("Trying to connect to: " + Arrays.toString(myAddress) + " port: " + myPort);
        Socket tcpSocket = new Socket(InetAddress.getByAddress(myAddress), myPort);
        PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        out.print(Message.buildHandshake(getPeerId()));
        char[] tcpMsg = new char[68];
        in.read(tcpMsg);
        System.out.println(tcpMsg);

    }

    public static byte[] buildConnectionRequest(){

        ByteBuffer bb = ByteBuffer.allocate(16);
        byte[] sendConnection_id1 = {0x00, 0x00, 0x04};
        byte[] sendConnection_id2 = { 0x17,0x27, 0x10, 0x19, (byte)0x80};
        byte[] sendAction = {0x00, 0x00, 0x00, 0x00};
        byte[] sendTransactionId = new byte[4];
        new SecureRandom().nextBytes(sendTransactionId);

        bb.put(0, sendConnection_id1);
        bb.put(3, sendConnection_id2);
        bb.put(8, sendAction);
        bb.put(12, sendTransactionId);
        bb.rewind();
        byte[] connectRequest = new byte[16];
        bb.get(connectRequest);
        return connectRequest;

    }

    public static List<byte[]> group(byte[] stream, int groupSize){
        List<byte[]> groups = new ArrayList<byte[]>();
        for (int i = 0; i<stream.length; i+=groupSize){
            byte[] group = Arrays.copyOfRange(stream, i, i + groupSize);
            int howManyZeroes = 0;

            for(int x = 0; x < group.length; x++) {
                if (group[x] == 0)
                   howManyZeroes++;
            }

            if(howManyZeroes!=6)
                groups.add(group);
            else
                break;
        }

        return groups;
    }


    public static byte[] buildAnnounceRequest(byte[] connectionId, Map<String, Object> torrent) throws NoSuchAlgorithmException {
        Bencode bencode = new Bencode();
        ByteBuffer bb = ByteBuffer.allocate(98);
        Map<String, Object> info = (Map<String, Object>)torrent.get("info");
        byte[] infoBytes = bencode.encode((Map<String, Object>)torrent.get("info"));

        byte[] size = bencode.encode((Long)info.get("length"));


        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.update(infoBytes);
        byte[] sha1 = digest.digest();


        bb.put(0, connectionId);                   //connection id
        byte[] temp = new byte[4];
        temp[3]=0x1;
        bb.put(8, temp);                         //action
        byte[] transactionId = new byte[4];
        new SecureRandom().nextBytes(transactionId);
        bb.put(12, transactionId);                  //transacrion id
        bb.put(16, sha1);                            // infohash
        bb.put(36, getPeerId());                      //peer id
        temp = new byte[8];
        bb.put(56, temp);                      //downloaded
        bb.put(64, size);                       // left
        bb.put(72, temp);                      //upload
        temp = new byte[4];
        bb.put(80, temp);                      //event
        bb.put(84, temp);                      //ip address
        byte[] key = new byte[4];
        new SecureRandom().nextBytes(transactionId);
        bb.put(88, key);                        //key
        temp = new byte[4];
        temp[3]=-0x1;
        bb.put(92, temp);                    //num want
        bb.put(96, ByteBuffer.allocate(2).putShort((short) 6688).array());
        byte[] announceRequest = new byte[98];
        bb.rewind();
        bb.get(announceRequest);

        return announceRequest;
    }

    public static byte[] getPeerId(){
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

}
