import java.nio.ByteBuffer;

public class Message {

    public static byte[] buildHandshake(byte[] peerId){
        byte[] handshake = new byte[68];
        ByteBuffer bb =  ByteBuffer.allocate(68);
        String pstr = "BitTorrent protocol";

        bb.put(0, (byte)0x19);
        bb.put(1, pstr.getBytes());
        bb.put(20, ByteBuffer.allocate(8).putInt(0).array());
        bb.put(28, peerId);


        return handshake;
    }

    public static byte[] buildKeepAlive(){

        return ByteBuffer.allocate(4).putInt(0).array();
    }

    public static byte[] buildChoke(){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(1).array());
        bb.rewind();
        return bb.array();
    }
    public static byte[] buildUnChoke(){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(1).array());
        bb.put(4, (byte)0x1);
        bb.rewind();
        return bb.array();
    }

    public static byte[] buildInterested(){
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(0, ByteBuffer.allocate(4).putInt(1).array());
        bb.put(4, (byte)0x2);
        bb.rewind();
        return bb.array();
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
}
