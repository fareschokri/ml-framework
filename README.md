# MLFramework Application

## Purpose

The MLFramework application is designed to provide a RESTful API for machine learning tasks, focusing on Natural Language Processing (NLP). The application supports training, loading and using models for document categorization, language detection, tokenization and named entity recognition. This framework leverages the Apache OpenNLP library to perform the various tasks.

## Features

### Document Categorizer
- **Train Model**: Train a document categorizer model using a training data file.
- **Load Model**: Load a pre-trained document categorizer model from a binary file.
- **Process File**: Classify entries from an input file and save the results to an output file.
- **Process Text**: Categorize a given text and return all possible outcomes and their probabilities.

### Language Detector
- **Train Model**: Train a language detection model using a training data file.
- **Load Model**: Load a pre-trained language detection model from a binary file.
- **Process File**: Detect languages for entries in an input file and save the results to an output file.
- **Process Text**: Detect the language of a given text.
- **Get Languages**: Return all model languages.

### Tokenizer
- **Train Model**: Train a tokenizer model using a training data file.
- **Load Model**: Load a pre-trained tokenizer model from a binary file.
- **Process File**: Tokenize entries from an input file and save the results to an output file.
- **Process Text**: Tokenize a given text.

### Name Finder
- **Train Model**: Train a name finder model using a training data file.
- **Load Model**: Load a pre-trained name finder model from a binary file.
- **Process File**: Detect named entities from an input file and save the results to an output file.
- **Process Text**: Detect named entities for a given text.

## Usage

The application exposes the following REST endpoints:

### Document Categorizer Endpoints
- **POST /doccat/train**: Train a new document categorizer model.
    - Parameters: `trainingDataFile`, `modelBinOutput`, `algorithm`, `cutoff`, `iterations`, `languageCode`
- **POST /doccat/load**: Load an existing document categorizer model.
    - Parameters: `modelBinFile`
- **GET /doccat/process-file**: Process a file using the document categorizer model.
    - Parameters: `inputFile`, `outputFile`
- **GET /doccat/process-text**: Process text using the document categorizer model.
    - Parameters: `text`

### Language Detector Endpoints
- **POST /lang-detector/train**: Train a new language detector model.
    - Parameters: `trainingDataFile`, `modelBinOutput`, `algorithm`, `cutoff`, `iterations`
- **POST /lang-detector/load**: Load an existing language detector model.
    - Parameters: `modelBinFile`
- **GET /lang-detector/process-file**: Process a file using the language detector model.
    - Parameters: `inputFile`, `outputFile`
- **GET /lang-detector/process-text**: Process text using the language detector model.
    - Parameters: `text`
- **GET /lang-detector/get-languages**: Get all Model languages.

### Tokenizer Endpoints
- **POST /tokenizer/train**: Train a new tokenizer model.
    - Parameters: `trainingDataFile`, `modelBinOutput`, `algorithm`, `cutoff`, `iterations`, `languageCode`
- **POST /tokenizer/load**: Load an existing tokenizer model.
    - Parameters: `modelBinFile`
- **GET /tokenizer/process-file**: Process a file using the tokenizer model.
    - Parameters: `inputFile`, `outputFile`
- **GET /tokenizer/process-text**: Process text using the tokenizer model.
    - Parameters: `text`

### Name Finder Endpoints
- **POST /name-finder/train**: Train a new name finder model, currently supports one named entity (typeFinder) training. Eventually to handle multiple entities.
    - Parameters: `trainingDataFile`, `modelBinOutput`, `algorithm`, `cutoff`, `iterations`, `languageCode`, `typeFinder`
- **POST /name-finder/load**: Load an existing name finder model.
    - Parameters: `modelBinFile`
- **GET /name-finder/process-file**: Process a file using the name finder model.
    - Parameters: `inputFile`, `outputFile`
- **GET /name-finder/process-text**: Process text using the name finder model.
    - Parameters: `text`

### Training Data Formats
Depending on the model, each line of the training data file should respect: 
- **Document Categorizer**: \<category>\tab\<text>
- **Language Detector**: \<languageCode>\tab\<text>
- **Tokenizer**: \<token>\<SPLIT> \<token>\<SPLIT> \<token> \<text> (Note that tokens are split automatically on whitespace and at least one <SPLIT> tag must be present in the training text.)
- **Name Finder**: \<text> \<START:typeName> \<entity> \<END> \<text>

More information on training formats and opennlp API at: https://opennlp.apache.org/docs/2.3.3/manual/opennlp.html

## Setup and Configuration

1. **Clone the Repository**:
    ```sh
    git clone https://github.com/fareschokri/ml-framework.git
    cd <repository-directory>
    ```

2. **Configure Application Properties**:
   Ensure that your `application.properties` file contains the necessary configuration for the models and their parameters.

3. **Build and Run**:
    ```sh
    ./gradlew build
    java -jar build/libs/ml-framework.jar
    ```

4. **Access the Endpoints**:
   The application will run on `http://localhost:8080`. Use tools like Postman or curl to interact with the endpoints.


## Contribution

Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

---