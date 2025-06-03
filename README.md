# PUCFLIX 3.0 - Sistema de Gerenciamento de Séries de TV

## Integrantes: Arthur Signorini, Bernardo Rocha, Laura Persilva e Otávio Augusto

## Visão Geral do Projeto

PUCFLIX 3.0 é uma aplicação desenvolvida em Java para gerenciamento de séries de TV, episódios e informações de elenco. O sistema implementa estruturas de dados avançadas como índices invertidos, árvores B+ e tabelas hash para garantir armazenamento e busca eficientes. Foi desenvolvido como parte da disciplina de Algoritmos e Estruturas de Dados III (AEDs III), demonstrando a aplicação prática de algoritmos e estruturas complexas.

## Funcionalidades do Sistema

- **Gerenciamento de Séries**: CRUD completo para séries com informações detalhadas.
- **Gerenciamento de Episódios**: Controle de episódios com temporada, número e associação à série.
- **Gerenciamento de Elenco**: Cadastro de atores com vínculo às séries.
- **Busca Avançada**: Pesquisa textual com índices invertidos e pontuação TF-IDF.
- **Relacionamentos**: Controle das associações entre séries, episódios e elenco.
- **Persistência em Arquivos**: Armazenamento eficiente usando formatos binários personalizados.

## Arquitetura e Design

### Estrutura de Pacotes

```
├── App/            # Ponto de entrada da aplicação
├── Model/          # Classes de entidades e estruturas de dados
├── Service/        # Lógica de negócio e manipulação de dados
├── View/           # Interface com o usuário
├── Menu/           # Menus e controle de interação
├── Interfaces/     # Interfaces abstratas para estruturas de dados
└── dados/          # Arquivos de dados e índices
```

### Componentes Principais

#### Camada Model

- **TvShow**: Representa uma série de TV com título, gênero, ano e idioma.
- **Chapter**: Representa episódios com título, temporada, número e duração.
- **Cast**: Representa membros do elenco com vínculo à série.
- **ListaInvertida**: Implementação da estrutura de índice invertido por blocos.
- **ElementoLista**: Entrada no índice com ID e frequência/peso.

#### Camada Service

- **FileManager<T>**: Gerenciador genérico de arquivos com índice B+.
- **IndiceInvertido**: Gerencia os três índices invertidos (séries, episódios e atores).
- **ShowChapterManager**: Controla relação entre séries e episódios.
- **ShowCastManager**: Gerencia vínculos entre séries e elenco.

#### Camada View

- **TvShowView**: Interface para operações com séries.
- **ChapterView**: Interface para gerenciamento de episódios.
- **CastView**: Interface para operações com o elenco.

## Implementação do Índice Invertido

### Estrutura

Três índices invertidos distintos são implementados usando a classe `ListaInvertida`:

1. **listaInvertidaSeries**: Indexa títulos de séries.
2. **listaInvertidaEpisodios**: Indexa títulos de episódios.
3. **listaInvertidaAtores**: Indexa nomes de atores.

### Detalhes da Implementação

- **Arquivo Dicionário**: Mapeia termos para ponteiros de bloco.
- **Arquivo de Blocos**: Contém listas invertidas com pesos TF-IDF.
- **Organização em Blocos**: Cada bloco armazena múltiplas entradas e lida com overflow.

### Algoritmo de Busca

1. Processamento do termo: normalização, tokenização e remoção de stop words.
2. Cálculo de TF-IDF:
   - TF: frequência do termo no documento.
   - IDF: `log(total_doc / doc_com_termo) + 1`
3. Junção de resultados de múltiplos termos.
4. Ordenação por pontuação de relevância.

### Recursos

- Filtro de Stop Words
- Normalização de Texto (remoção de acentos, padronização de maiúsculas/minúsculas)
- Suporte a buscas com múltiplos termos
- Atualizações dinâmicas nos índices durante operações CRUD

## Desempenho

- **Tempo de busca**: O(k × log n), onde k é o número de termos e n o tamanho do dicionário.
- **Eficiência de armazenamento**: Estrutura em blocos configurável.
- **Escalabilidade**: Suporte a grandes vocabulários com persistência em disco.

## Exemplos de Uso

### Buscar Séries

```bash
Digite o termo de busca: "breaking bad"
Resultados:
1. Breaking Bad (ID: 1, Score: 8.45)
2. Breaking Point (ID: 15, Score: 4.32)
```

### Buscar Episódios

```bash
Digite o título do episódio: "pilot"
Resultados:
1. Pilot - Breaking Bad T1E1 (Score: 7.89)
2. The Pilot - Lost T1E1 (Score: 6.54)
```

### Buscar Atores

```bash
Digite o nome do ator: "bryan cranston"
Resultados:
1. Bryan Cranston (Score: 9.12)
   - Breaking Bad
   - Malcolm in the Middle
```

## Estruturas de Dados Utilizadas

1. Listas Invertidas
2. Árvores B+
3. Tabelas Hash
4. Arquivos Binários

### Organização dos Arquivos

```
dados/
├── Listas/
│   ├── lista_serie.dict
│   ├── lista_serie.bloc
│   ├── lista_episodio.dict
│   ├── lista_episodio.bloc
│   ├── lista_ator.dict
│   └── lista_ator.bloc
├── series.db
├── episodios.db
└── atores.db
```

### Algoritmo TF-IDF

```java
float tf = calculateTermFrequency(term, document);
float idf = (float)(Math.log((float)totalDocuments / documentsContainingTerm) + 1);
float score = tf * idf;
```

## Experiência de Desenvolvimento

### Desafios Enfrentados

- Integração de múltiplos índices invertidos
- Cálculo preciso de TF-IDF
- Balanceamento entre frequência e relevância nas buscas
- Gerenciamento de memória com grandes volumes de dados

### Aprendizados

- Estruturas avançadas de índices
- Implementação prática do TF-IDF
- Manipulação de arquivos binários personalizados
- Integração de múltiplas estruturas em um sistema funcional

### Otimizações

- Busca em dicionário com complexidade O(log n)
- Carregamento sob demanda melhora o uso de memória
- Resultados pré-ordenados eliminam ordenações em tempo de execução

## Requisitos do Sistema

- **Java**: JDK 8 ou superior
- **Memória**: Mínimo 512MB de RAM
- **Armazenamento**: 100MB de espaço livre
- **Sistema Operacional**: Compatível com Windows, macOS ou Linux

## Formulário

- O índice invertido com os termos dos títulos das séries foi criado usando a classe ListaInvertida? SIM
- O índice invertido com os termos dos títulos dos episódios foi criado usando a classe ListaInvertida? SIM
- O índice invertido com os termos dos nomes dos atores foi criado usando a classe ListaInvertida? SIM
- É possível buscar séries por palavras usando o índice invertido? SIM
- É possível buscar episódios por palavras usando o índice invertido? SIM
- É possível buscar atores por palavras usando o índice invertido? SIM
- O trabalho está completo? SIM
- O trabalho é original e não a cópia de um trabalho de um colega? SIM

## Instalação e Uso

1. **Compilar a aplicação**:
   ```bash
   javac -cp . App/Main.java
   ```

2. **Executar a aplicação**:
   ```bash
   java -cp . App.Main
   ```

3. **Navegar pelos menus**:
   - Use os números para selecionar opções
   - Siga os prompts para inserir dados
   - Use a busca para encontrar registros

---

*PUCFLIX 3.0 - Projeto de Implementação de Estruturas de Dados Avançadas*
