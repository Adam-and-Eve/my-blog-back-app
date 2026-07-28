package ru.yandex.practicum.services.blog.core.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CreateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.UpdateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.CommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <summary>
 * Мок-тесты для проверки бизнес-логики сервиса комментариев (PostCommentServiceImpl).
 * </summary>
 **/
@ExtendWith(MockitoExtension.class)
public final class PostCommentServiceImplTest
{
    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostCommentServiceImpl postCommentService;

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException, если метод получения комментариев вызван с null вместо ID поста.
     * </summary>
     **/
    @Test
    void getCommentsByPostIdShouldThrowValidationExceptionWhenPostIdIsNull()
    {
        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.getCommentsByPostId(null)
        );
    }

    /**
     * <summary>
     * Успешный сценарий получения списка комментариев для существующего поста.
     * </summary>
     **/
    @Test
    void getCommentsByPostIdShouldReturnMappedDtosWhenPostExists()
    {
        var postId = 1L;

        var comment = new CommentEntityObject(
                10L,
                new CommentTextValueObject("Текст комментария"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        Mockito.when(commentRepository.findCommentsByPostId(postId)).thenReturn(List.of(comment));

        var result = postCommentService.getCommentsByPostId(postId);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals(10L, result.getFirst().getId());

        Assertions.assertEquals("Текст комментария", result.getFirst().getText());
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при передаче null в качестве идентификатора поста.
     * </summary>
     **/
    @Test
    void createCommentShouldThrowValidationExceptionWhenPostIdIsNull()
    {
        var request = new CreateCommentRequestDto("Контент", 1L);

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.createComment(null, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException, если тело запроса (DTO) отсутствует.
     * </summary>
     **/
    @Test
    void createCommentShouldThrowValidationExceptionWhenRequestDtoIsNull()
    {
        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.createComment(1L, null)
        );
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при несовпадении postId в URL и теле запроса.
     * </summary>
     **/
    @Test
    void createCommentShouldThrowValidationExceptionWhenPostIdsMismatched()
    {
        var urlPostId = 1L;

        var bodyPostId = 2L;

        var request = new CreateCommentRequestDto("Контент", bodyPostId);

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.createComment(urlPostId, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если публикация по переданному ID не найдена.
     * </summary>
     **/
    @Test
    void createCommentShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        var postId = 1L;

        var request = new CreateCommentRequestDto("Контент", postId);

        Mockito.when(postRepository.findPostById(postId)).thenReturn(null);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postCommentService.createComment(postId, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс IllegalStateException при сбое сохранения комментария репозиторием.
     * </summary>
     **/
    @Test
    void createCommentShouldThrowIllegalStateExceptionWhenRepositoryReturnsEmptyOptional()
    {
        var postId = 1L;

        var request = new CreateCommentRequestDto("Контент", postId);

        var post = new PostEntityObject(postId, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(postId)).thenReturn(post);

        Mockito.when(commentRepository.saveComment(Mockito.any(CommentEntityObject.class), Mockito.eq(postId)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> postCommentService.createComment(postId, request)
        );
    }

    /**
     * <summary>
     * Успешный сценарий создания комментария.
     * </summary>
     **/
    @Test
    void createCommentShouldSaveAndReturnDtoWhenHappyPath()
    {
        var postId = 1L;

        var request = new CreateCommentRequestDto("Успешный комментарий", postId);

        var post = new PostEntityObject(postId, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        var savedComment = new CommentEntityObject(100L, new CommentTextValueObject("Успешный комментарий"), OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(postId)).thenReturn(post);

        Mockito.when(commentRepository.saveComment(Mockito.any(CommentEntityObject.class), Mockito.eq(postId)))
                .thenReturn(Optional.of(savedComment));

        var result = postCommentService.createComment(postId, request);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(100L, result.getId());

        Assertions.assertEquals("Успешный комментарий", result.getText());
    }

    /**
     * <summary>
     * Проверяет валидацию на null для всех трех входных параметров метода обновления.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowValidationExceptionWhenParametersAreNull()
    {
        var request = new UpdateCommentRequestDto(10L, "Текст", 1L);

        Assertions.assertThrows(ApplicationValidationException.class, () -> postCommentService.updateComment(null, 10L, request));

        Assertions.assertThrows(ApplicationValidationException.class, () -> postCommentService.updateComment(1L, null, request));

        Assertions.assertThrows(ApplicationValidationException.class, () -> postCommentService.updateComment(1L, 10L, null));
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при несоответствии postId в URL и DTO.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowValidationExceptionWhenPostIdMismatched()
    {
        var request = new UpdateCommentRequestDto(10L, "Текст", 2L);

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.updateComment(1L, 10L, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при несоответствии commentId в URL и DTO.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowValidationExceptionWhenCommentIdMismatched()
    {
        var request = new UpdateCommentRequestDto(20L, "Текст", 1L);

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.updateComment(1L, 10L, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если публикация для обновления комментария не существует.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        var request = new UpdateCommentRequestDto(10L, "Текст", 1L);

        Mockito.when(postRepository.findPostById(1L)).thenReturn(null);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postCommentService.updateComment(1L, 10L, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если обогащенный пост не содержит указанного комментария.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowNotFoundExceptionWhenCommentIsMissingInPost()
    {
        var request = new UpdateCommentRequestDto(10L, "Текст", 1L);

        var post = new PostEntityObject(1L, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(post);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postCommentService.updateComment(1L, 10L, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс IllegalStateException при неудачном апдейте внутри репозитория (возвращен пустой Optional).
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowIllegalStateExceptionWhenRepositoryFails()
    {
        var request = new UpdateCommentRequestDto(10L, "Новый текст", 1L);

        var post = new PostEntityObject(1L, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        var comment = new CommentEntityObject(10L, new CommentTextValueObject("Старый текст"), OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(post);

        Mockito.doAnswer(invocation -> {
            post.initializeComments(List.of(comment));
            return null;
        }).when(commentRepository).enrichPostsWithComments(Mockito.anyList());

        Mockito.when(commentRepository.updateComment(Mockito.any(CommentEntityObject.class), Mockito.eq(1L)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> postCommentService.updateComment(1L, 10L, request)
        );
    }

    /**
     * <summary>
     * Успешное обновление контента комментария с валидацией изменения текста.
     * </summary>
     **/
    @Test
    void updateCommentShouldModifyTextAndReturnDtoWhenHappyPath()
    {
        var request = new UpdateCommentRequestDto(10L, "Новый текст", 1L);

        var post = new PostEntityObject(1L, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        var comment = new CommentEntityObject(10L, new CommentTextValueObject("Старый текст"), OffsetDateTime.now(), OffsetDateTime.now());

        var updatedComment = new CommentEntityObject(10L, new CommentTextValueObject("Новый текст"), comment.getCreatedAt(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(post);

        Mockito.doAnswer(invocation -> {
            post.initializeComments(List.of(comment));
            return null;
        }).when(commentRepository).enrichPostsWithComments(Mockito.anyList());

        Mockito.when(commentRepository.updateComment(Mockito.any(CommentEntityObject.class), Mockito.eq(1L)))
                .thenReturn(Optional.of(updatedComment));

        var result = postCommentService.updateComment(1L, 10L, request);

        Assertions.assertNotNull(result);

        Assertions.assertEquals("Новый текст", result.getText());
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException, если в теле запроса на обновление отсутствует ID комментария.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowValidationExceptionWhenCommentIdInBodyIsNull()
    {
        var request = new UpdateCommentRequestDto(null, "Текст обновления", 1L);

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.updateComment(1L, 10L, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException, если в теле запроса на обновление отсутствует ID публикации.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowValidationExceptionWhenPostIdInBodyIsNull()
    {
        var request = new UpdateCommentRequestDto(10L, "Текст обновления", null);

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postCommentService.updateComment(1L, 10L, request)
        );
    }

    /**
     * <summary>
     * Проверяет валидацию на null идентификаторов при удалении.
     * </summary>
     **/
    @Test
    void deleteCommentShouldThrowValidationExceptionWhenIdsAreNull()
    {
        Assertions.assertThrows(ApplicationValidationException.class, () -> postCommentService.deleteComment(null, 10L));

        Assertions.assertThrows(ApplicationValidationException.class, () -> postCommentService.deleteComment(1L, null));
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException при попытке удалить комментарий у несуществующего поста.
     * </summary>
     **/
    @Test
    void deleteCommentShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        Mockito.when(postRepository.findPostById(1L)).thenReturn(null);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postCommentService.deleteComment(1L, 10L)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если удаляемый комментарий отсутствует внутри публикации.
     * </summary>
     **/
    @Test
    void deleteCommentShouldThrowNotFoundExceptionWhenCommentIsMissingInPost()
    {
        var post = new PostEntityObject(1L, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(post);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postCommentService.deleteComment(1L, 10L)
        );
    }

    /**
     * <summary>
     * Проверяет выброс IllegalStateException, если репозиторий вернул false при удалении существующего комментария.
     * </summary>
     **/
    @Test
    void deleteCommentShouldThrowIllegalStateExceptionWhenRepositoryReturnsFalse()
    {
        var post = new PostEntityObject(1L, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        var comment = new CommentEntityObject(10L, new CommentTextValueObject("Текст"), OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(post);

        Mockito.doAnswer(invocation -> {
            post.initializeComments(List.of(comment));
            return null;
        }).when(commentRepository).enrichPostsWithComments(Mockito.anyList());

        Mockito.when(commentRepository.deleteComment(10L, 1L)).thenReturn(false);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> postCommentService.deleteComment(1L, 10L)
        );
    }

    /**
     * <summary>
     * Успешный сценарий удаления комментария из публикации.
     * </summary>
     **/
    @Test
    void deleteCommentShouldExecuteSuccessfullyWhenHappyPath()
    {
        var post = new PostEntityObject(1L, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        var comment = new CommentEntityObject(10L, new CommentTextValueObject("Текст"), OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(post);

        Mockito.doAnswer(invocation -> {
            post.initializeComments(List.of(comment));
            return null;
        }).when(commentRepository).enrichPostsWithComments(Mockito.anyList());

        Mockito.when(commentRepository.deleteComment(10L, 1L)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> postCommentService.deleteComment(1L, 10L));

        Mockito.verify(commentRepository, Mockito.times(1)).deleteComment(10L, 1L);
    }
}