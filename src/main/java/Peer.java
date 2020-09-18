import java.nio.ByteBuffer;
import java.util.Arrays;

public class Peer {
    private byte[] address;
    private int port;
    private byte[] sha1;
    private byte[] bitfield;

    public Peer(byte[] addr, byte[] sha1){

        address = Arrays.copyOfRange(addr,0, 4);
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.put(2, Arrays.copyOfRange(addr,4, 6));
        this.port = bb.getInt();
        this.sha1 = sha1;
    }

    public byte[] getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public byte[] getSha1() {
        return sha1;
    }

    public byte[] getBitfield() {
        return bitfield;
    }

    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    @Override
    public String toString() {
        return "address=" + Arrays.toString(address) +
                ", port=" + port;

    }
}
