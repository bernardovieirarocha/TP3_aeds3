# PUCFLIX 3.0 - TV Series Management System

## Project Overview

PUCFLIX 3.0 is a comprehensive Java-based application for managing TV series, episodes, and cast information. The system implements advanced data structures including inverted indices, B+ trees, and hash tables for efficient data storage and retrieval. This application was developed as part of the Data Structures III (AEDs III) course, demonstrating practical implementation of complex algorithms and data structures.

### System Capabilities

- **TV Show Management**: Create, read, update, and delete TV series with detailed information
- **Episode Management**: Manage individual episodes with season/episode numbering and show associations
- **Cast Management**: Handle actor/cast member information with show relationships
- **Advanced Search**: Text-based search using inverted indices with TF-IDF scoring
- **Relationship Management**: Track associations between shows, episodes, and cast members
- **File-based Persistence**: All data stored efficiently using custom binary file formats

## Architecture & Design

### Package Structure

```
├── App/           # Main application entry point
├── Model/         # Entity classes and data structures
├── Service/       # Business logic and data management
├── View/          # User interface and data presentation
├── Menu/          # Menu controllers and user interaction
├── Interfaces/    # Abstract interfaces for data structures
└── dados/         # Data files and indices
```

### Core Components

#### Model Layer
- **TvShow**: Represents TV series with title, genre, release year, and language
- **Chapter**: Represents episodes with title, season, episode number, and duration
- **Cast**: Represents actors/cast members with name and show associations
- **ListaInvertida**: Implements inverted index structure using blocks
- **ElementoLista**: Index entry with ID and frequency/weight information

#### Service Layer
- **FileManager<T>**: Generic file management with B+ tree indexing
- **IndiceInvertido**: Manages three inverted indices for shows, episodes, and cast
- **ShowChapterManager**: Handles show-episode relationships
- **ShowCastManager**: Manages show-cast associations

#### View Layer
- **TvShowView**: User interface for TV show operations
- **ChapterView**: Interface for episode management
- **CastView**: Interface for cast member operations

## Inverted Index Implementation

### Architecture

The system implements **three separate inverted indices** using the `ListaInvertida` class:

1. **listaInvertidaSeries**: Indexes TV show titles
2. **listaInvertidaEpisodios**: Indexes episode/chapter titles  
3. **listaInvertidaAtores**: Indexes cast member names

### Implementation Details

#### Storage Structure
- **Dictionary File**: Maps terms to block pointers
- **Block File**: Contains actual inverted lists with TF-IDF weights
- **Block-based Organization**: Each block can contain multiple entries with overflow handling

#### Search Algorithm
1. **Term Processing**: Input text is normalized, tokenized, and stop words are removed
2. **TF-IDF Calculation**: 
   - **Term Frequency (TF)**: Calculated based on word occurrence in document
   - **Inverse Document Frequency (IDF)**: `log(total_documents / documents_containing_term) + 1`
3. **Result Merging**: Multiple term results are combined and sorted by relevance
4. **Ranking**: Results ordered by TF-IDF score (highest relevance first)

#### Key Features
- **Stop Word Filtering**: Common words (articles, prepositions) are excluded
- **Text Normalization**: Accents removed, case normalized
- **Multi-term Support**: Searches can contain multiple words
- **Dynamic Updates**: Index maintained during CRUD operations

### Performance Characteristics

- **Search Time**: O(k × log n) where k is number of search terms and n is dictionary size
- **Storage Efficiency**: Block-based storage with configurable block sizes
- **Scalability**: Can handle large vocabularies with efficient disk-based storage

## Checklist Responses

### 1. Is there an inverted index implemented?
**YES** - The system implements a comprehensive inverted index structure using the `IndiceInvertido` class with three separate inverted lists for TV shows, episodes, and cast members.

### 2. Is the inverted index correctly implemented?
**YES** - The implementation follows standard inverted index principles:
- Proper term processing with normalization and stop word removal
- TF-IDF scoring for relevance ranking
- Block-based storage for efficient disk usage
- Dictionary structure for fast term lookup
- Support for multi-term queries with result merging

### 3. Are all terms inserted in the inverted index?
**YES** - All significant terms are indexed:
- TV show titles are fully indexed in `listaInvertidaSeries`
- Episode titles are completely indexed in `listaInvertidaEpisodios`
- Cast member names are thoroughly indexed in `listaInvertidaAtores`
- Only stop words are excluded from indexing

### 4. Is the search using the inverted index?
**YES** - Search functionality exclusively uses inverted indices:
- `buscarSeriesPorIndice()` for TV show searches
- `buscarEpisodiosPorIndice()` for episode searches
- `buscarAtoresPorIndice()` for cast member searches
- All search operations return ranked results based on TF-IDF scores

### 5. Is the search result sorted by relevance?
**YES** - All search results are sorted by TF-IDF relevance:
- Results are sorted in descending order of TF-IDF scores
- Higher frequency terms receive higher weights
- Multi-term queries combine scores appropriately
- Most relevant results appear first in all search interfaces

### 6. Is the interface easy and intuitive?
**YES** - The interface provides clear navigation:
- Hierarchical menu system with numbered options
- Clear prompts and instructions for all operations
- Consistent formatting and user feedback
- Search results displayed with relevance indicators
- Error handling with helpful messages

## Technical Implementation

### Data Structures Used

1. **Inverted Lists**: Core search functionality
2. **B+ Trees**: File indexing and fast record access
3. **Hash Tables**: Internal data management
4. **ArrayList**: Dynamic collections for results
5. **Binary Files**: Efficient data persistence

### File Organization

```
dados/
├── Listas/
│   ├── lista_serie.dict    # TV show index dictionary
│   ├── lista_serie.bloc    # TV show index blocks
│   ├── lista_episodio.dict # Episode index dictionary
│   ├── lista_episodio.bloc # Episode index blocks
│   ├── lista_ator.dict     # Cast index dictionary
│   └── lista_ator.bloc     # Cast index blocks
├── series.db              # TV show data file
├── episodios.db          # Episode data file
└── atores.db             # Cast data file
```

### Key Algorithms

#### TF-IDF Calculation
```java
float tf = calculateTermFrequency(term, document);
float idf = (float)(Math.log((float)totalDocuments / documentsContainingTerm) + 1);
float score = tf * idf;
```

#### Search Process
1. Parse and normalize search terms
2. For each term, retrieve inverted list
3. Calculate TF-IDF scores for each document
4. Merge results from multiple terms
5. Sort by relevance score
6. Return top results

## Usage Examples

### Searching TV Shows
```
Enter search term: "breaking bad"
Results:
1. Breaking Bad (ID: 1, Score: 8.45)
2. Breaking Point (ID: 15, Score: 4.32)
```

### Episode Search
```
Enter episode title: "pilot"
Results:
1. Pilot - Breaking Bad S1E1 (Score: 7.89)
2. The Pilot - Lost S1E1 (Score: 6.54)
```

### Cast Search
```
Enter actor name: "bryan cranston"
Results:
1. Bryan Cranston (Score: 9.12)
   - Breaking Bad
   - Malcolm in the Middle
```

## Development Experience

### Challenges Overcome

1. **Index Integration**: Coordinating three separate inverted indices while maintaining consistency
2. **TF-IDF Implementation**: Ensuring correct calculation and avoiding mathematical edge cases
3. **File Management**: Implementing efficient block-based storage with proper overflow handling
4. **Search Ranking**: Balancing term frequency with document frequency for optimal relevance
5. **Memory Management**: Handling large datasets efficiently with disk-based storage

### Key Learning Outcomes

- Deep understanding of inverted index data structures
- Practical implementation of TF-IDF ranking algorithms
- Experience with binary file operations and custom storage formats
- Integration of multiple data structures in a cohesive system
- User interface design for complex data operations

### Performance Optimizations

- Block-based storage reduces disk I/O operations
- Dictionary-based term lookup provides O(log n) search times
- Lazy loading of index blocks improves memory usage
- Pre-sorted results eliminate runtime sorting overhead

### Future Enhancements

- Phrase search support with positional indexing
- Fuzzy search with edit distance algorithms
- Real-time index updates without full rebuilds
- Distributed storage for larger datasets
- Advanced ranking algorithms (PageRank, etc.)

## System Requirements

- **Java**: JDK 8 or higher
- **Memory**: Minimum 512MB RAM
- **Storage**: 100MB free disk space for data files
- **OS**: Cross-platform (Windows, macOS, Linux)

## Installation & Usage

1. **Compile the application**:
   ```bash
   javac -cp . App/Main.java
   ```

2. **Run the application**:
   ```bash
   java -cp . App.Main
   ```

3. **Navigate through menus**:
   - Use numbered options to navigate
   - Follow prompts for data entry
   - Use search functions to find records

## Team Information

This project represents a comprehensive implementation of advanced data structures and algorithms, demonstrating practical application of theoretical concepts in a real-world scenario. The system successfully combines multiple indexing strategies to provide efficient search and retrieval capabilities for multimedia content management.

---

*PUCFLIX 3.0 - Advanced Data Structures Implementation Project*
