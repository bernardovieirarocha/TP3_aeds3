package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Scanner;

public class StopWords {
    private static final String arquivoStopWords = "Model/stopword.txt";
    private static ArrayList<String> stopWords = new ArrayList<>();

    public StopWords() {
        File arquivo;
        try {
            arquivo = new File(arquivoStopWords);
            if (!arquivo.exists()) {
                System.out.println("Arquivo de stop words não encontrado.");
            } else {
                Scanner leitor = new Scanner(arquivo);
                while (leitor.hasNext()) {
                    String palavra = leitor.next();
                    stopWords.add(normalize(palavra.trim()));
                }
                leitor.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao abrir o arquivo de stop words.");
        }
    }

    private String normalize(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "")
            .toLowerCase();
    }

    public boolean isStopWord(String palavra) {
        return stopWords.contains(normalize(palavra));
    }

    public ArrayList<String> getStopWords() {
        return stopWords;
    }

    public void addStopWord(String palavra) {
        stopWords.add(normalize(palavra));
    }

    public void removeStopWord(String palavra) {
        stopWords.remove(normalize(palavra));
    }

    public int size() {
        return stopWords.size();
    }

    public boolean isEmpty() {
        return stopWords.isEmpty();
    }
} // StopWords
