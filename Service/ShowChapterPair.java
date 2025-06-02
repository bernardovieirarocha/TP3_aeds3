package Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import Interfaces.RegistroArvoreBMais;

public class ShowChapterPair implements RegistroArvoreBMais<ShowChapterPair> {
    
    private int showId;
    private int chapterId;
    
    public ShowChapterPair() {
        this.showId = -1;
        this.chapterId = -1;
    }
    
    public ShowChapterPair(int showId, int chapterId) {
        this.showId = showId;
        this.chapterId = chapterId;
    }
    
    public int getIdSerie() {
        return this.showId;
    }
    
    public void setIdSerie(int showId) {
        this.showId = showId;
    }
    
    public int getIdEpisodio() {
        return this.chapterId;
    }
    
    public void setIdEpisodio(int chapterId) {
        this.chapterId = chapterId;
    }
    
    @Override
    public short size() {
        return 8; // 4 bytes for each int (showId and chapterId)
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(showId);
        dos.writeInt(chapterId);
        
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        this.showId = dis.readInt();
        this.chapterId = dis.readInt();
    }

    @Override
    public int compareTo(ShowChapterPair outro) {
        // First compare by showId
        if (this.showId != outro.showId) {
            return Integer.compare(this.showId, outro.showId);
        }
        // If showId is the same, compare by chapterId
        return Integer.compare(this.chapterId, outro.chapterId);
    }


    @Override
    public ShowChapterPair clone() {
        return new ShowChapterPair(this.showId, this.chapterId);
    }
    
    @Override
    public String toString() {
        return "Show: " + showId + ", Chapter: " + chapterId;
    }
}
