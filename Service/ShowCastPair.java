package Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import Interfaces.RegistroArvoreBMais;

public class ShowCastPair implements RegistroArvoreBMais<ShowCastPair> {
    
    private int showId;
    private int castId;
    
    public ShowCastPair() {
        this.showId = -1;
        this.castId = -1;
    }
    
    public ShowCastPair(int showId, int castId) {
        this.showId = showId;
        this.castId = castId;
    }
    
    public int getIdSerie() {
        return this.showId;
    }
    
    public void setIdSerie(int showId) {
        this.showId = showId;
    }
    
    public int getIdAtor() {
        return this.castId;
    }
    
    public void setIdAtor(int castId) {
        this.castId = castId;
    }
    
    @Override
    public short size() {
        return 8; // 4 bytes for each int (showId and castId)
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(showId);
        dos.writeInt(castId);
        
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        this.showId = dis.readInt();
        this.castId = dis.readInt();
    }

    @Override
    public int compareTo(ShowCastPair outro) {
        // First compare by showId
        if (this.showId != outro.showId) {
            return Integer.compare(this.showId, outro.showId);
        }
        // If showId is the same, compare by castId
        return Integer.compare(this.castId, outro.castId);
    }

    @Override
    public ShowCastPair clone() {
        return new ShowCastPair(this.showId, this.castId);
    }
    
    @Override
    public String toString() {
        return "Show: " + showId + ", Cast: " + castId;
    }
}
