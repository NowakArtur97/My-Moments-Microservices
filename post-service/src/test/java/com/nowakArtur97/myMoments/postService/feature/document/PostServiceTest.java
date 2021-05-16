package com.nowakArtur97.myMoments.postService.feature.document;

import com.nowakArtur97.myMoments.postService.feature.PostTestBuilder;
import com.nowakArtur97.myMoments.postService.feature.messaging.PostEventProducer;
import com.nowakArtur97.myMoments.postService.feature.resource.PostDTO;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
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

    @Mock
    private PostEventProducer postEventProducer;

    private static MockedStatic<UUID> mocked;

    private static PostTestBuilder postTestBuilder;

    @BeforeAll
    static void setUpBuilderAndUUID() {

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

        postService = new PostService(postRepository, postEventProducer);
    }

    @Nested
    class CreatePostTest {

        @Test
        void when_create_post_should_create_post() {

            Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image.jpg".getBytes());
            String authorExpected = "author";

            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withAuthor(authorExpected).withBinary(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);
            PostDocument postExpected = (PostDocument) postTestBuilder.withAuthor(authorExpected)
                    .withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);

            when(postRepository.save(postExpected)).thenReturn(Mono.just(postExpected));

            Mono<PostDocument> postActualMono = postService.createPost(authorExpected, postDTOExpected);

            StepVerifier.create(postActualMono)
                    .thenConsumeWhile(
                            postActual -> {
                                assertPost(postExpected, postActual);
                                assertAll(() -> verify(postRepository, times(1)).save(postExpected),
                                        () -> verifyNoMoreInteractions(postRepository),
                                        () -> verifyNoInteractions(postEventProducer));
                                return true;
                            }
                    ).verifyComplete();
        }
    }

    @Nested
    class UpdatePostTest {

        @Test
        void when_update_valid_post_should_update_post() {

            String postId = "post id";
            Binary imageExpectedBeforeUpdate = new Binary(BsonBinarySubType.BINARY, "image.jpg".getBytes());
            Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image2.jpg".getBytes());
            Binary imageExpected2 = new Binary(BsonBinarySubType.BINARY, "image3.jpg".getBytes());
            String authorExpected = "author";

            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption("new caption").withAuthor(authorExpected)
                    .withBinary(List.of(imageExpected, imageExpected2)).build(ObjectType.CREATE_DTO);
            PostDocument postExpectedBeforeUpdate = (PostDocument) postTestBuilder.withCaption("old caption")
                    .withAuthor(authorExpected).withBinary(List.of(imageExpectedBeforeUpdate)).build(ObjectType.DOCUMENT);
            PostDocument postExpected = (PostDocument) postTestBuilder.withCaption("new caption").withAuthor(authorExpected)
                    .withBinary(List.of(imageExpected, imageExpected2)).build(ObjectType.DOCUMENT);

            when(postRepository.findById(postId)).thenReturn(Mono.just(postExpectedBeforeUpdate));
            when(postRepository.save(postExpected)).thenReturn(Mono.just(postExpected));

            Mono<PostDocument> postActualMono = postService.updatePost(postId, authorExpected, postDTOExpected);

            StepVerifier.create(postActualMono)
                    .thenConsumeWhile(
                            postActual -> {
                                assertPost(postExpected, postActual);
                                assertAll(
                                        () -> verify(postRepository, times(1)).findById(postId),
                                        () -> verify(postRepository, times(1)).save(postExpected),
                                        () -> verifyNoMoreInteractions(postRepository),
                                        () -> verifyNoInteractions(postEventProducer));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_update_not_existing_post_should_throw_exception() {

            String postId = "post id";
            Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image2.jpg".getBytes());
            String authorExpected = "author";

            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption("new caption").withAuthor(authorExpected)
                    .withBinary(List.of(imageExpected)).build(ObjectType.CREATE_DTO);

            when(postRepository.findById(postId)).thenReturn(Mono.empty());

            Mono<PostDocument> postActualMono = postService.updatePost(postId, authorExpected, postDTOExpected);

            StepVerifier.create(postActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(postRepository, times(1)).findById(postId),
                                    () -> verifyNoMoreInteractions(postRepository),
                                    () -> verifyNoInteractions(postEventProducer))
                    ).verifyErrorMessage("Post with id: '" + postId + "' not found.");
        }

        @Test
        void when_update_other_user_post_should_throw_exception() {

            String postId = "post id";
            Binary imageExpectedBeforeUpdate = new Binary(BsonBinarySubType.BINARY, "image.jpg".getBytes());
            Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image2.jpg".getBytes());
            String authorExpected = "author";
            String otherUser = "other user";

            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption("new caption").withAuthor(authorExpected)
                    .withBinary(List.of(imageExpected)).build(ObjectType.CREATE_DTO);
            PostDocument postExpectedBeforeUpdate = (PostDocument) postTestBuilder.withCaption("old caption")
                    .withAuthor(authorExpected).withBinary(List.of(imageExpectedBeforeUpdate)).build(ObjectType.DOCUMENT);

            when(postRepository.findById(postId)).thenReturn(Mono.just(postExpectedBeforeUpdate));

            Mono<PostDocument> postActualMono = postService.updatePost(postId, otherUser, postDTOExpected);

            StepVerifier.create(postActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(postRepository, times(1)).findById(postId),
                                    () -> verifyNoMoreInteractions(postRepository),
                                    () -> verifyNoInteractions(postEventProducer))
                    ).verifyErrorMessage("User can only change his own posts.");
        }
    }

    @Nested
    class DeletePostTest {

        @Test
        void when_delete_post_should_delete_post() {

            String postId = "post id";
            String authorExpected = "author";

            PostDocument postExpected = (PostDocument) postTestBuilder.withCaption("new caption")
                    .withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(postRepository.findById(postId)).thenReturn(Mono.just(postExpected));
            when(postRepository.delete(postExpected)).thenReturn(Mono.empty());

            Mono<Void> postVoidMono = postService.deletePost(postId, authorExpected);

            StepVerifier.create(postVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(postRepository, times(1)).findById(postId),
                                    () -> verify(postRepository, times(1)).delete(postExpected),
                                    () -> verifyNoMoreInteractions(postRepository),
                                    () -> verify(postEventProducer, times(1))
                                            .sendPostDeleteEvent(postId),
                                    () -> verifyNoMoreInteractions(postEventProducer))
                    ).verifyComplete();
        }

        @Test
        void when_delete_not_existing_post_should_throw_exception() {

            String postId = "post id";
            String authorExpected = "author";

            when(postRepository.findById(postId)).thenReturn(Mono.empty());

            Mono<Void> postVoidMono = postService.deletePost(postId, authorExpected);

            StepVerifier.create(postVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(postRepository, times(1)).findById(postId),
                                    () -> verifyNoMoreInteractions(postRepository),
                                    () -> verifyNoInteractions(postEventProducer))
                    ).verifyErrorMessage("Post with id: '" + postId + "' not found.");
        }

        @Test
        void when_delete_other_user_post_should_throw_exception() {

            String postId = "post id";
            String authorExpected = "author";
            String otherUser = "other user";

            PostDocument postExpected = (PostDocument) postTestBuilder.withCaption("old caption")
                    .withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(postRepository.findById(postId)).thenReturn(Mono.just(postExpected));

            Mono<Void> postVoidMono = postService.deletePost(postId, otherUser);

            StepVerifier.create(postVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(postRepository, times(1)).findById(postId),
                                    () -> verifyNoMoreInteractions(postRepository),
                                    () -> verifyNoInteractions(postEventProducer))
                    ).verifyErrorMessage("User can only change his own posts.");
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
                                assertPost(postExpected, postActual);
                                assertAll(
                                        () -> verify(postRepository, times(1)).findById(postId),
                                        () -> verifyNoMoreInteractions(postRepository),
                                        () -> verifyNoInteractions(postEventProducer));
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
                    .then(() ->
                            assertAll(
                                    () -> verify(postRepository, times(1)).findById(postId),
                                    () -> verifyNoMoreInteractions(postRepository),
                                    () -> verifyNoInteractions(postEventProducer))
                    )
                    .verifyComplete();
        }

        @Test
        void when_find_posts_by_author_should_return_posts() {

            String author = "user";

            PostDocument postExpected = (PostDocument) postTestBuilder.withAuthor(author).build(ObjectType.DOCUMENT);
            PostDocument postExpected2 = (PostDocument) postTestBuilder.withCaption("caption 2").withAuthor(author)
                    .build(ObjectType.DOCUMENT);

            when(postRepository.findByAuthor(author)).thenReturn(Flux.just(postExpected, postExpected2));

            Flux<PostDocument> postsActualFlux = postService.findPostsByAuthor(author);

            StepVerifier.create(postsActualFlux)
                    .expectNextMatches(postActual -> assertPost(postExpected, postActual))
                    .expectNextMatches(postActual -> assertPost(postExpected2, postActual))
                    .then(() ->
                            assertAll(
                                    () -> verify(postRepository, times(1)).findByAuthor(author),
                                    () -> verifyNoMoreInteractions(postRepository),
                                    () -> verifyNoInteractions(postEventProducer)))
                    .verifyComplete();
        }
    }

    private boolean assertPost(PostDocument postExpected, PostDocument postActual) {

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
                                + postActual.getPhotos()));
        return true;
    }
}
