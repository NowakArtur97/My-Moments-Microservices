package com.nowakArtur97.myMoments.postService.feature.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class PostObjectMapper {

    private final ObjectMapper objectMapper;

    public Mono<PostDTO> getPostDTOFromString(String postAsString, Flux<FilePart> photos) {

        return photos.collectList().map(images -> {
            if (postAsString == null) {
                return new PostDTO("", images);
            } else {
                return getPostDTO(postAsString, images);
            }
        });
    }

    private PostDTO getPostDTO(String postAsString, java.util.List<FilePart> images) {

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
