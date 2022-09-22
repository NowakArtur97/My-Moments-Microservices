package com.nowakArtur97.myMoments.postService.feature.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
class PostObjectMapper {

    private final ObjectMapper objectMapper;

    public Mono<PostDTO> getPostDTOFromString(String postAsString, Flux<FilePart> photos) {

        return photos.map(Part::content)
                .flatMap(DataBufferUtil::mergeDataBuffers)
                .map(bytes -> new Binary(BsonBinarySubType.BINARY, bytes))
                .filter(image -> image.length() > 0)
                .collectList()
                .map(images -> getPostDTO(postAsString, images))
                .switchIfEmpty(Mono.just(getPostDTO(postAsString, Collections.emptyList())));
    }

    private PostDTO getPostDTO(String postAsString, List<Binary> images) {
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
    }
}
