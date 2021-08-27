package com.nowakArtur97.myMoments.postService.feature.resource;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DataBufferUtil {

    public static Mono<byte[]> mergeDataBuffers(Flux<DataBuffer> dataBufferFlux) {

        return DataBufferUtils.join(dataBufferFlux).map(DataBufferUtil::readFromDataBuffer);
    }

    private static byte[] readFromDataBuffer(DataBuffer dataBuffer) {

        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);

        return bytes;
    }
}
