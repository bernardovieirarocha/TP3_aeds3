package Service;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import Model.Ator;
import Model.ElementoLista;
import Model.Episodio;
import Model.ListaInvertida;
import Model.Serie;
import Model.StopWords;

public class IndiceInvertido {

    private StopWords stopWords;
    private ListaInvertida listaInvertidaAtores;
    private ListaInvertida listaInvertidaSeries;
    private ListaInvertida listaInvertidaEpisodios;

    public IndiceInvertido() {
        try {
            new File("dados").mkdirs(); // Garante que o diretório existe

            stopWords = new StopWords();

            listaInvertidaAtores = new ListaInvertida(
                    4,
                    "dados/Listas/lista_ator.dict",
                    "dados/Listas/lista_ator.bloc");

            listaInvertidaSeries = new ListaInvertida(
                    4,
                    "dados/Listas/lista_serie.dict",
                    "dados/Listas/lista_serie.bloc");

            listaInvertidaEpisodios = new ListaInvertida(
                    4,
                    "dados/Listas/lista_episodio.dict",
                    "dados/Listas/lista_episodio.bloc");

        } catch (Exception e) {
            System.out.println("Erro ao inicializar os índices invertidos.");
            e.printStackTrace();
        }
    }

    // -------------------------
    // FUNÇÕES AUXILIARES
    // -------------------------
    private String normalize(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();
    }

    private ArrayList<String> getPalavras(String texto) {
        ArrayList<String> palavras = new ArrayList<>();
        String[] partes = normalize(texto).split(" ");
        for (String palavra : partes) {
            if (!stopWords.isStopWord(palavra) && !palavra.isBlank()) {
                palavras.add(palavra);
            }
        }
        return palavras;
    }

    private float calcularFrequencia(String palavra, ArrayList<String> palavras) {
        float freq = 0;
        int total = palavras.size();
        palavra = normalize(palavra);
        for (String p : palavras) {
            if (normalize(p).equals(palavra)) {
                freq++;
            }
        }
        return freq / total;
    }

    // -------------------------
    // INSERIR / ATUALIZAR / EXCLUIR
    // -------------------------

    public void inserirAtor(Ator ator) {
        try {
            ArrayList<String> palavras = getPalavras(ator.getNome());
            for (String palavra : palavras) {
                float freq = calcularFrequencia(palavra, palavras);
                listaInvertidaAtores.create(palavra, new ElementoLista(ator.getId(), freq));
            }
        } catch (Exception e) {
            System.out.println("Erro ao indexar ator.");
            e.printStackTrace();
        }
    }

    public void atualizarAtor(String nomeAntigo, Ator ator) {
        try {
            ArrayList<String> palavrasAntigas = getPalavras(nomeAntigo);
            for (String palavra : palavrasAntigas) {
                listaInvertidaAtores.delete(palavra, ator.getId());
            }
            inserirAtor(ator);
        } catch (Exception e) {
            System.out.println("Erro ao atualizar ator no índice.");
            e.printStackTrace();
        }
    }

    public void excluirAtor(String nome, int id) {
        try {
            ArrayList<String> palavras = getPalavras(nome);
            for (String palavra : palavras) {
                listaInvertidaAtores.delete(palavra, id);
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir ator do índice.");
            e.printStackTrace();
        }
    }

    public void inserirSerie(Serie serie) {
        try {
            ArrayList<String> palavras = getPalavras(serie.getNome());
            for (String palavra : palavras) {
                float freq = calcularFrequencia(palavra, palavras);
                listaInvertidaSeries.create(palavra, new ElementoLista(serie.getId(), freq));
            }
        } catch (Exception e) {
            System.out.println("Erro ao indexar série.");
            e.printStackTrace();
        }
    }

    public void atualizarSerie(String nomeAntigo, Serie serie) {
        try {
            ArrayList<String> palavrasAntigas = getPalavras(nomeAntigo);
            for (String palavra : palavrasAntigas) {
                listaInvertidaSeries.delete(palavra, serie.getId());
            }
            inserirSerie(serie);
        } catch (Exception e) {
            System.out.println("Erro ao atualizar série no índice.");
            e.printStackTrace();
        }
    }

    public void excluirSerie(String nome, int id) {
        try {
            ArrayList<String> palavras = getPalavras(nome);
            for (String palavra : palavras) {
                listaInvertidaSeries.delete(palavra, id);
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir série do índice.");
            e.printStackTrace();
        }
    }

    public void inserirEpisodio(Episodio episodio) {
        try {
            ArrayList<String> palavras = getPalavras(episodio.getNome());
            for (String palavra : palavras) {
                float freq = calcularFrequencia(palavra, palavras);
                listaInvertidaEpisodios.create(palavra, new ElementoLista(episodio.getId(), freq));
            }
        } catch (Exception e) {
            System.out.println("Erro ao indexar episódio.");
            e.printStackTrace();
        }
    }

    public void atualizarEpisodio(String tituloAntigo, Episodio episodio) {
        try {
            ArrayList<String> palavrasAntigas = getPalavras(tituloAntigo);
            for (String palavra : palavrasAntigas) {
                listaInvertidaEpisodios.delete(palavra, episodio.getId());
            }
            inserirEpisodio(episodio);
        } catch (Exception e) {
            System.out.println("Erro ao atualizar episódio no índice.");
            e.printStackTrace();
        }
    }

    public void excluirEpisodio(String titulo, int id) {
        try {
            ArrayList<String> palavras = getPalavras(titulo);
            for (String palavra : palavras) {
                listaInvertidaEpisodios.delete(palavra, id);
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir episódio do índice.");
            e.printStackTrace();
        }
    }

    // -------------------------
    // BUSCAR COM LISTA INVERTIDA
    // -------------------------

    public ElementoLista[] buscarSeriesPorIndice(String termo) {
        return buscarPorIndice(termo, listaInvertidaSeries);
    }

    public ElementoLista[] buscarAtoresPorIndice(String termo) {
        return buscarPorIndice(termo, listaInvertidaAtores);
    }

    public ElementoLista[] buscarEpisodiosPorIndice(String termo) {
        return buscarPorIndice(termo, listaInvertidaEpisodios);
    }

    // 🔥 Função genérica para qualquer índice
    private ElementoLista[] buscarPorIndice(String termo, ListaInvertida listaInvertida) {
        try {
            ArrayList<ElementoLista> resultado = new ArrayList<>();
            ArrayList<String> termos = getPalavras(termo);
            int total = listaInvertida.numeroEntidades();

            if (total == 0)
                total = 1;

            for (String t : termos) {
                ElementoLista[] lista = listaInvertida.read(t);
                float idf = (float) (Math.log((float) total / (lista.length == 0 ? 1 : lista.length)) + 1);

                if (Float.isInfinite(idf) || Float.isNaN(idf)) {
                    idf = 1;
                }

                for (ElementoLista el : lista) {
                    resultado.add(new ElementoLista(el.getId(), el.getFrequencia() * idf));
                }
            }

            var mapa = resultado.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            ElementoLista::getId,
                            el -> el,
                            (e1, e2) -> new ElementoLista(e1.getId(), e1.getFrequencia() + e2.getFrequencia())));

            ElementoLista[] resposta = mapa.values().toArray(new ElementoLista[0]);
            Arrays.sort(resposta, Comparator.comparing(ElementoLista::getFrequencia).reversed());

            return resposta;

        } catch (Exception e) {
            System.out.println("Erro na busca por índice invertido.");
            e.printStackTrace();
            return new ElementoLista[0];
        }
    }
}
