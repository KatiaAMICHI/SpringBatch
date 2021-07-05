package com.jump.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(final ChunkContext chunkContext) {
        log.info("[CustomChunkListener].................. beforeChunk {}", chunkContext);

    }

    @Override
    public void afterChunk(final ChunkContext chunkContext) {
        log.info("[CustomChunkListener].................. afterChunk {}", chunkContext);

    }

    @Override
    public void afterChunkError(final ChunkContext chunkContext) {
        log.info("[CustomChunkListener].................. afterChunkError {}", chunkContext);

    }
}
