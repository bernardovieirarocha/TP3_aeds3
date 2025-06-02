package Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import Interfaces.Registro;

public class TvShow implements Registro {
    protected int showId;
    protected String title;
    protected LocalDate releaseYear;
    protected String synopsis;
    protected int synopsisLength;
    protected String platform;
    protected int seasonCount;
    protected int leadCastId;

        //constructor to pass attribute values
        public TvShow(int i, String n, LocalDate a ,  String si,int SS, String st, int QtdTe, int IDat){
            showId = i;
            title = n;
            releaseYear = a;
            synopsis = si;
            synopsisLength = SS;
            platform = st;
            seasonCount = QtdTe;
            leadCastId = IDat;
        }

        public TvShow( String n, LocalDate a ,  String si,int SS, String st, int QtdTe, int IDat){
            this.showId = 0;
            title = n;
            releaseYear = a;
            synopsis = si;
            synopsisLength = SS;
            platform = st;
            seasonCount = QtdTe;
            leadCastId = IDat;
        }

        public TvShow() {
        showId = -1;
        title = "";
        releaseYear = null;
        synopsis = "";
        synopsisLength = 0;
        platform = "";
        seasonCount = 0;
        leadCastId = 0;
        }

        public void setId(int id) {
            this.showId = id;
        }
    
        public int getId() {
            return showId;
        }
    
        public String getNome() {
            return title;
        }
    
        public void setNome(String Nome) {
            this.title = Nome;
        }
    
        public LocalDate getAnoLancamento() {
            return releaseYear;
        }
    
        public void setAnoLancamento(LocalDate AnoLancamento) {
            this.releaseYear = AnoLancamento;
        }
    
        public String getSinopse() {
            return synopsis;
        }
    
        public void setSinopse(String Sinopse) {
            this.synopsis = Sinopse;
        }
    
        public String getStreaming() {
            return platform;
        }
    
        public void setStreaming(String Streaming) {
            this.platform = Streaming;
        }

        public void setQtdTemporada(int QtdTe){
            this.seasonCount = QtdTe;
        }

        public int getQtdTemporada(){
            return seasonCount;
        }

        public void setIDator(int IDator){
            this.leadCastId = IDator;
        }

        public int getIDator(){
            return leadCastId;
        }

        public void SinopzeSize(int SS){
            this.synopsisLength = SS;
        }

        public int SinopseSize(){
            return synopsisLength;
        }
        


        //METHOD THAT DESCRIBES THE SERIES BY MEANS OF A BYTE ARRAY
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(b);
        dos.writeInt(showId);
        dos.writeUTF(title);
        dos.writeInt((int) this.releaseYear.toEpochDay());
        dos.writeInt(synopsisLength);
        dos.writeUTF(synopsis);
        dos.writeUTF(platform);
        dos.writeInt(seasonCount);
        dos.writeInt(leadCastId);

        return b.toByteArray();
    }

    //INVERSE METHOD: READ FROM FILE THE BYTE ARRAY AND LOAD THE OBJ

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        showId = dis.readInt();
        title = dis.readUTF();
        releaseYear = LocalDate.ofEpochDay(dis.readInt());
        synopsisLength = dis.readInt();
        synopsis = dis.readUTF();
        platform = dis.readUTF();
        seasonCount = dis.readInt();
        leadCastId = dis.readInt();
    }

    // English method names for better consistency
    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(LocalDate releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
        this.synopsisLength = synopsis.length();
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }

    public int getLeadCastId() {
        return leadCastId;
    }

    public void setLeadCastId(int leadCastId) {
        this.leadCastId = leadCastId;
    }
    
    public int getAssociatedCastId() {
        return leadCastId;
    }

    public void setAssociatedCastId(int associatedCastId) {
        this.leadCastId = associatedCastId;
    }
}


