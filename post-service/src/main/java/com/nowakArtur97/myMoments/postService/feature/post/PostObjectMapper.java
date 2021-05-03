package com.nowakArtur97.myMoments.postService.feature.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
class PostObjectMapper {

    private final ObjectMapper objectMapper;

    public Mono<PostDTO> getPostDTOFromString(String postAsString, Flux<FilePart> photos) {

        return photos.flatMap(filePart ->
                filePart.content().map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new Binary(BsonBinarySubType.BINARY, bytes);
                }).collectList())
                .map(images -> {
                    if (postAsString == null) {

                        return new PostDTO("", images);

                    } else {
                        try {
                            PostDTO postDTO = objectMapper.readValue(postAsString, PostDTO.class);

                            if (postDTO.getCaption() == null) {
                                postDTO.setCaption("");
                            }
                            postDTO.setPhotos(images);

                            return postDTO;

                        } catch (JsonProcessingException e) {
                            return new PostDTO("", images);
                        }
                    }
                }).switchIfEmpty(Mono.just(new PostDTO("", Collections.emptyList())))
                .next();
    }
}
