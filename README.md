# Gosu Prompt Generator

Gosu Prompt Generator is a Java-based application that leverages Quarkus and LangChain4j to generate prompts and perform easy retrieval-augmented generation (RAG) using Gosu language documentation.


## API Usage

GET http://localhost:8080/generate?prompt=Create an example of structure and use Dynamic to instantiate it
Returns: Hello from Quarkus REST


## Running Locally

./mvnw quarkus:dev


## Building Image

```
./mvnw clean package
docker build -f src/main/docker/Dockerfile.jvm -t fredericci/gosu-prompt-generator .
docker push fredericci/gosu-prompt-generator:latest 
```

## Running the image


```
docker run --rm -it -v ./easy-rag-catalog:/tmp/easy-rag-catalog -v ./easy-rag-embeddings.json:/tmp/easy-rag-embeddings.json -p 8080:8080 -e RAG_PATH=/tmp/easy-rag-catalog -e RAG_FILE=/tmp/easy-rag-embeddings.json fredericci/gosu-prompt-generator
```

