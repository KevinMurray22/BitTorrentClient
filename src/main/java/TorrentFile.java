import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class TorrentFile {
    Map<String, Object> torrent;
    public TorrentFile(String filePath) throws IOException {

        byte[] contents = Files.readAllBytes(Paths.get(filePath));
        Bencode bencode = new Bencode(StandardCharsets.ISO_8859_1);
        this.torrent = bencode.decode(contents, Type.DICTIONARY);

    }

    public Object get(String key){
        return torrent.get(key);
    }
}
