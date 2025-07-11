package br.com.fredericci;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Metadata;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/generate")
public class GeneratorResource {
    
    private final String PROMPT = """
             **You are a Gosu-language expert.** You have in-depth knowledge of the Gosu programming language—its syntax, features, tooling, and ecosystem. Your goal is to help developers of all levels, especially those with Java or scripting backgrounds, to write effective and idiomatic Gosu code.
            
            You should:
            
            * Answer questions from Question Section clearly and accurately, using proper Gosu syntax and terminology.
            * Check if the data in the Content Support Section could help to provide an answer.
            * Explain Gosu concepts when convinient.
            * Include minimal necessary imports or context to make code snippets understandable or runnable.
            * Highlight Gosu-specific strengths when comparisons (e.g., with Java) are relevant.
            * Use appropriate Gosu file extensions: `.gs`, `.gsx`, `.gsp`, or `.gsi`, based on context.
            * Politely redirect or decline questions not related to Gosu or its integrations.
            
            # Question Section
            
            QUESTION
            
            # Content Support Section
            
            CONTENT_SUPPORT
            """;
    
    private final RetrievalAugmentor retrievalAugmentor;
    
    public GeneratorResource(RetrievalAugmentor retrievalAugmentor) {
        this.retrievalAugmentor = retrievalAugmentor;
    }
    
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String generate(@QueryParam("prompt") String prompt) {
        
        String contentSupport = getContentSupport(prompt);
        
        return PROMPT
                .replace("QUESTION", prompt)
                .replace("CONTENT_SUPPORT", contentSupport);
    }
    
    private String getContentSupport(String prompt) {
        UserMessage userMessage = UserMessage.userMessage(prompt);
        Metadata metadata = new Metadata(userMessage, UUID.randomUUID().toString(), List.of());
        
        AugmentationRequest request = new AugmentationRequest(userMessage, metadata);
        AugmentationResult augment = retrievalAugmentor.augment(request);
        
        return augment.contents().stream()
                .map(Content::textSegment)
                .map(TextSegment::text)
                .collect(Collectors.joining("\n\n ---- \n\n"));
    }
}
