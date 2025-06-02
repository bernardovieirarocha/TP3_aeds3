package Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import Interfaces.Registro;

public class Cast implements Registro{
    protected int castId;
    protected String actorName;
    protected char gender;
    protected int associatedShowId;

    public Cast(int i, String n, char g, int IDSerie){
        castId = i;
        actorName = n;
        gender = g;
        associatedShowId = IDSerie;
    }

    public Cast(String n, char g, int IDSerie){
        this.castId = 0;
        actorName = n;
        gender = g;
        associatedShowId = IDSerie;
    }

    public Cast(){
        castId = -1;
        actorName = "";
        gender = ' ';
        associatedShowId = 0;
    }

    public void setId(int id) {
        this.castId = id;
    }

    public int getId() {
        return castId;
    }

    public String getNome() {
        return actorName;
    }

    public void setNome(String Nome) {
        this.actorName = Nome;
    }

    public char getGenero() {
        return gender;
    }

    public void setGenero(char Genero) {
        this.gender = Genero;
    }

    public int getIDSerie(){
        return associatedShowId;
    }

    public void setIDSerie(int i){
        this.associatedShowId = i;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(b);
        dos.writeInt(castId);
        dos.writeUTF(actorName);
        dos.writeChar(gender);
        dos.writeInt(associatedShowId);
        
        return b.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        castId = dis.readInt();
        actorName = dis.readUTF();
        gender = dis.readChar();
        associatedShowId = dis.readInt();
    }

    // English method names for better consistency
    public int getCastId() {
        return castId;
    }

    public void setCastId(int castId) {
        this.castId = castId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getAssociatedShowId() {
        return associatedShowId;
    }

    public void setAssociatedShowId(int associatedShowId) {
        this.associatedShowId = associatedShowId;
    }

}
