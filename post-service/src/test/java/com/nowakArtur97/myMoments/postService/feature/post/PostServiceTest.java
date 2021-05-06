package com.nowakArtur97.myMoments.postService.feature.post;

import com.nowakArtur97.myMoments.postService.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.generator.NameWithSpacesGenerator;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostService_Tests")
class PostServiceTest {

    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private static MockedStatic<UUID> mocked;

    private static PostTestBuilder postTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

        postTestBuilder = new PostTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @AfterAll
    static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @BeforeEach
    void setUp() {

        postService = new PostService(postRepository);
    }

    @Nested
    class CreatePostTest {

        @Test
        void when_create_post_should_create_post() {

            Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image.jpg" .getBytes());
            String authorExpected = "author";

            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withAuthor(authorExpected).withBinary(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);
            PostDocument postExpected = (PostDocument) postTestBuilder.withAuthor(authorExpected)
                    .withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);

            when(postRepository.save(postExpected)).thenReturn(Mono.just(postExpected));

            Mono<PostDocument> postActualMono = postService.createPost(postDTOExpected, authorExpected);

            StepVerifier.create(postActualMono)
                    .thenConsumeWhile(
                            postActual -> {
                                assertAll(() -> assertEquals(postExpected, postActual,
                                        () -> "should return post: " + postExpected + ", but was: " + postActual),
                                        () -> assertEquals(postExpected.getId(), postActual.getId(),
                                                () -> "should return post with id: " + postExpected.getId() + ", but was: "
                                                        + postActual.getId()),
                                        () -> assertEquals(postExpected.getCaption(), postActual.getCaption(),
                                                () -> "should return post with caption: " + postExpected.getCaption() + ", but was: "
                                                        + postActual.getCaption()),
                                        () -> assertEquals(postExpected.getAuthor(), postActual.getAuthor(),
                                                () -> "should return post with author: " + postExpected.getAuthor() + ", but was: "
                                                        + postActual.getAuthor()),
                                        () -> assertEquals(postExpected.getPhotos(), postActual.getPhotos(),
                                                () -> "should return post with photos: " + postExpected.getPhotos() + ", but was: "
                                                        + postActual.getPhotos()),
                                        () -> verify(postRepository, times(1)).save(postExpected),
                                        () -> verifyNoMoreInteractions(postRepository));
                                return true;
                            }
                    ).verifyComplete();
        }
    }

    @Nested
    class OtherPostTest {

        @Test
        void when_find_existing_post_by_id_should_return_post() {

            String postId = "id";
            PostDocument postExpected = (PostDocument) postTestBuilder.withId(postId).build(ObjectType.DOCUMENT);

            when(postRepository.findById(postId)).thenReturn(Mono.just(postExpected));

            Mono<PostDocument> postActualMono = postService.findPostById(postId);

            StepVerifier.create(postActualMono)
                    .thenConsumeWhile(
                            postActual -> {
                                assertAll(() -> assertEquals(postExpected, postActual,
                                        () -> "should return post: " + postExpected + ", but was: " + postActual),
                                        () -> assertEquals(postExpected.getId(), postActual.getId(),
                                                () -> "should return post with id: " + postExpected.getId() + ", but was: "
                                                        + postActual.getId()),
                                        () -> assertEquals(postExpected.getCaption(), postActual.getCaption(),
                                                () -> "should return post with caption: " + postExpected.getCaption() + ", but was: "
                                                        + postActual.getCaption()),
                                        () -> assertEquals(postExpected.getAuthor(), postActual.getAuthor(),
                                                () -> "should return post with author: " + postExpected.getAuthor() + ", but was: "
                                                        + postActual.getAuthor()),
                                        () -> assertEquals(postExpected.getPhotos(), postActual.getPhotos(),
                                                () -> "should return post with photos: " + postExpected.getPhotos() + ", but was: "
                                                        + postActual.getPhotos()),
                                        () -> verify(postRepository, times(1)).findById(postId),
                                        () -> verifyNoMoreInteractions(postRepository));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_find_not_existing_post_by_id_should_return_empty_mono() {

            String postId = "id";

            when(postRepository.findById(postId)).thenReturn(Mono.empty());

            Mono<PostDocument> postActualMono = postService.findPostById(postId);

            StepVerifier.create(postActualMono)
                    .expectNextCount(0)
                    .then(() -> {
                                assertAll(
                                        () -> verify(postRepository, times(1)).findById(postId),
                                        () -> verifyNoMoreInteractions(postRepository));
                            }
                    )
                    .verifyComplete();
        }
    }
}
