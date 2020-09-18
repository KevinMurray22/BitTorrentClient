import java.nio.ByteBuffer;
import java.util.Arrays;

public class Message {

    byte[] message;
    int msgLen;
    int id;
    byte[] payload;
    byte[] sha1;

    public Message(){

    }

    public Message(byte[] msg){
        message = msg;
        int msgLen = ByteBuffer.wrap(Arrays.copyOfRange(msg, 0, 4)).getInt();
        if(msg.length > 4){
            Byte temp = msg[4];
            id = temp.byteValue();
        }

        if(msg.length > 5)
            payload = Arrays.copyOfRange(msg,5, msg.length);
        else
            payload = new byte[0];

    }



    public void buildHandshake(byte[] peerId, byte[] sha1){
        byte[] handshake = new byte[68];
        ByteBuffer bb =  ByteBuffer.allocate(68);
        String pstr = "BitTorrent protocol";

        bb.put(0, (byte)0x13);
        bb.put(1, pstr.getBytes());
        bb.put(20, ByteBuffer.allocate(8).putInt(0).array());
        bb.put(28, sha1);
        bb.put(48, peerId);

        bb.rewind();
        bb.get(handshake);
        this.message = handshake;
    }

    public void buildKeepAlive(){

        message = ByteBuffer.allocate(4).putInt(0).array();
    }

    public static byte[] buildChoke(){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(1).array());
        bb.rewind();
        return bb.array();
    }
    public void buildUnChoke(){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(1).array());
        bb.put(4, (byte)0x1);
        bb.rewind();
        message = bb.array();
    }

    public void buildInterested(){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(1).array());
        bb.put(4, (byte)0x2);
        bb.rewind();
        message =  bb.array();
    }

    public static byte[] buildNotInterested(){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(1).array());
        bb.put(4, (byte)0x3);
        bb.rewind();
        return bb.array();
    }

    public static byte[] buildHave(byte[] payload){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(5).array());
        bb.put(4, (byte)0x4);
        bb.put(5, payload);
        bb.rewind();
        return bb.array();
    }

    public static byte[] buildBitField(byte[] bitfield){
        ByteBuffer bb = ByteBuffer.allocate(14);
        bb.put(0, ByteBuffer.allocate(4).putInt(bitfield.length+1).array());
        bb.put(4, (byte)0x5);
        bb.put(5, bitfield);
        bb.rewind();
        return bb.array();
    }
    /*
    public static byte[] buildRequest(byte[] payload){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(13).array());
        bb.put(4, (byte)0x6);
        bb.put(5, payload);
        bb.rewind();
        return bb.array();

    }

    public static byte[] buildPiece(){

    }

    public static byte[] buildCancel(){

    }

    public static byte[] buildPort(){

    }

     */

    public byte[] getMessage() {
        return message;
    }

    public int getMsgLen() {
        return msgLen;
    }

    public int getId() {
        return id;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getSha1() {
        return sha1;
    }

    public void handshakeParse(byte[] msg){
        message = msg;
        sha1 = Arrays.copyOfRange(msg, 28, 48);
    }
}
