import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class Peer {
    private final byte[] address;
    private final int port;
    private final byte[] sha1;
    private BitSet bitfield;
    boolean am_choking = true;
    boolean am_interested = false;
    boolean peer_choking = true;
    boolean peer_interested = false;

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

    public BitSet getBitfield() {
        return bitfield;
    }

    public void setBitfield(byte[] bitfield) {
        this.bitfield = BitSet.valueOf(bitfield);
    }

    public void setBitInBitfield(int index){
        bitfield.set(index);
    }

    public boolean isAm_choking() {
        return am_choking;
    }

    public void setAm_choking(boolean am_choking) {
        this.am_choking = am_choking;
    }

    public boolean isPeer_choking() {
        return peer_choking;
    }

    public void setPeer_choking(boolean peer_choking) {
        this.peer_choking = peer_choking;
    }

    public boolean isAm_interested() {
        return am_interested;
    }

    public void setAm_interested(boolean am_interested) {
        this.am_interested = am_interested;
    }

    public boolean isPeer_interested() {
        return peer_interested;
    }

    public void setPeer_interested(boolean peer_interested){
        this.peer_interested = peer_interested;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "address=" + Arrays.toString(address) +
                ", port=" + port +
                ", sha1=" + Arrays.toString(sha1) +
                ", bitfield=" + bitfield +
                ", am_choking=" + am_choking +
                ", am_interested=" + am_interested +
                ", peer_choking=" + peer_choking +
                ", peer_interested=" + peer_interested +
                '}';
    }
}
