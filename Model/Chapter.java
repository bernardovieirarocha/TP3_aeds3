package Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import Interfaces.Registro;

public class Chapter implements Registro{
    protected int chapterId;
    protected String title;
    protected int seasonNumber;
    protected LocalDate releaseDate;
    protected long duration;
    protected int showId;
    protected int episodeNumber;

    public Chapter(int i, String n, int t, LocalDate d, long du, int showId, int episodeNumber){
        chapterId = i;
        title = n;
        seasonNumber = t;
        releaseDate = d;
        duration = du;
        this.showId = showId;
        this.episodeNumber = episodeNumber;
    }

    public Chapter( String n, int t, LocalDate d, long du, int showId, int episodeNumber){
        this.chapterId = 0;
        title = n;
        seasonNumber = t;
        releaseDate = d;
        duration = du;
        this.showId = showId;
        this.episodeNumber = episodeNumber;
    }

    public Chapter( ){
        chapterId=-1;
        title = "";
        seasonNumber = 0;
        LocalDate.now();
        duration = 0;
        showId = 0;
    }

    public void setId(int id) {
        this.chapterId = id;
    }

    public int getId() {
        return chapterId;
    }

     public String getNome() {
        return title;
    }

    public void setNome(String Nome) {
        this.title = Nome;
    }

    public int getTemporada() {
        return seasonNumber;
    }

    public void setTemporada(int temporada) {
        this.seasonNumber = temporada;
    }

    public LocalDate getDataLancamento() {
        return releaseDate;
    }

    public void setDataLancamento(LocalDate DataLancamento) {
        this.releaseDate = DataLancamento;
    }

    public long getDuracao() {
        return duration;
    }

    public void setDuracao(long Duracao) {
        this.duration = Duracao;
    }

    public void setIdSerie(int id) {
        this.showId = id;
    }

    public int getIdSerie() {
        return showId;
    }

    public void setNumeroEpisodio(int N){
        this.episodeNumber = N;
    }

    public int getNumero(){
        return episodeNumber;
    }
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(b);
        dos.writeInt(chapterId);
        dos.writeUTF(title);
        dos.writeInt(seasonNumber);
        dos.writeInt((int) this.releaseDate.toEpochDay());
        dos.writeLong(duration);
        dos.writeInt(showId);
        return b.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.chapterId = dis.readInt();
        title = dis.readUTF();
        seasonNumber = dis.readInt();
        this.releaseDate = LocalDate.ofEpochDay(dis.readInt());
        duration = dis.readLong();
        showId = dis.readInt();
    }

    // English method names for better consistency
    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getAssociatedShowId() {
        return showId;
    }

    public void setAssociatedShowId(int showId) {
        this.showId = showId;
    }

    public int getChapterNumber() {
        return episodeNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.episodeNumber = chapterNumber;
    }

}
