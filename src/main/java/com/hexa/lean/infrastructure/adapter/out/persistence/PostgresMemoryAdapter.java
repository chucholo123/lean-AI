package com.hexa.lean.infrastructure.adapter.out.persistence;

import com.hexa.lean.domain.model.MemoryFragment;
import com.hexa.lean.domain.port.out.MemoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostgresMemoryAdapter implements MemoryRepositoryPort {

    private final VectorStore vectorStore;

    @Override
    public List<MemoryFragment> findRelevantMemories(String userQuery, int maxResults) {
        // Buscamos en la base de datos vectorial por similitud semántica
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.query(userQuery).withTopK(maxResults)
        );

        // Mapeamos el documento técnico de Spring AI a nuestro modelo puro de Dominio
        return results.stream()
                .map(doc -> new MemoryFragment(doc.getContent(), 0.9)) // 0.9 es un score simulado
                .toList();
    }

    @Override
    public void saveFact(String fact) {
        Document memoryDoc = new Document(fact);
        vectorStore.add(List.of(memoryDoc));
    }

    @Override
    public void deleteFact(Long memoryId) {
        // Lógica para borrar de DB
    }
}
