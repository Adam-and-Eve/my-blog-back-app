package ru.yandex.practicum.services.blog.core.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CreateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.UpdateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.ICommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.IImageRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.ITagRepository;
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
 * Мок-тесты для проверки бизнес-логики и сценариев валидации в PostService.
 * </summary>
 **/
@ExtendWith(MockitoExtension.class)
public final class PostServiceTest
{
    @Mock
    private IPostRepository postRepository;

    @Mock
    private ITagRepository tagRepository;

    @Mock
    private IImageRepository imageRepository;

    @Mock
    private ICommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    // region getPostsPage Tests

    /**
     * <summary>
     * Проверяет, что при передаче некорректного номера страницы (меньше 1)
     * выбрасывается ApplicationValidationException.
     * </summary>
     **/
    @Test
    void getPostsPageShouldThrowExceptionWhenPageNumberIsInvalid()
    {
        var exception = Assertions.assertThrows(
            ApplicationValidationException.class,
            () -> postService.getPostsPage("search", 0L, 10L)
        );

        Assertions.assertTrue(
            exception.getMessage().contains("Номер страницы должен быть больше или равен 1"));
    }

    /**
     * <summary>
     * Проверяет, что при передаче некорректного размера страницы (null)
     * выбрасывается ApplicationValidationException.
     * </summary>
     **/
    @Test
    void getPostsPageShouldThrowExceptionWhenPageSizeIsNull()
    {
        var exception = Assertions.assertThrows(
            ApplicationValidationException.class,
            () -> postService.getPostsPage("search", 1L, null)
        );

        Assertions.assertTrue(
            exception.getMessage().contains("Размер страницы должен быть больше или равен 1"));
    }

    /**
     * <summary>
     * Проверяет корректный расчет метаданных пагинации и обогащение постов тегами и комментариями.
     * </summary>
     **/
    @Test
    void getPostsPageShouldReturnCorrectPageDataWhenPostsExist()
    {
        var pageSize = 5L;

        var post = new PostEntityObject(
            new PostTitleValueObject("Заголовок"),
            new PostTextValueObject("Текст поста")
        );

        ReflectionTestUtils.setField(post, "id", 1L);

        Mockito.when(
            postRepository.countAllPosts(
                Mockito.any())).thenReturn(12L);

        Mockito.when(
            postRepository.findAllPosts(
                Mockito.any(),
                Mockito.eq(0L),
                Mockito.eq(pageSize))).thenReturn(List.of(post));

        var result = postService.getPostsPage("test", 1L, pageSize);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
            3L,
            result.getLastPage(),
            "Всего должно получиться 3 страницы (12 постов по 5 на страницу)");

        Mockito.verify(
            tagRepository,
            Mockito.times(1)).enrichPostsWithTags(Mockito.anyList());

        Mockito.verify(
           commentRepository,
           Mockito.times(1)).enrichPostsWithComments(Mockito.anyList());
    }

    // endregion

    // region likePost Tests

    /**
     * <summary>
     * Проверяет успешное увеличение счетчика лайков, если публикация существует.
     * </summary>
     **/
    @Test
    void likePostShouldReturnNewLikesCountWhenPostExists()
    {
        var postId = 1L;

        Mockito.when(postRepository.incrementLikesCount(postId)).thenReturn(Optional.of(15L));

        var likesCount = postService.likePost(postId);

        Assertions.assertEquals(15L, likesCount);
    }

    /**
     * <summary>
     * Проверяет выброс исключения EntityObjectNotFoundException, если пост для лайка не найден.
     * </summary>
     **/
    @Test
    void likePostShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        var postId = 1L;
        Mockito.when(postRepository.incrementLikesCount(postId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.likePost(postId)
        );
    }

    // endregion

    // region createComment Tests

    /**
     * <summary>
     * Проверяет успешное добавление нового комментария к существующему посту.
     * </summary>
     **/
    @Test
    void createCommentShouldSaveCommentAndReturnDtoWhenPostExists()
    {
        var postId = 1L;

        var request = new CreateCommentRequestDto("Контент комментария", postId);

        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        ReflectionTestUtils.setField(post, "id", postId);

        var savedComment = new CommentEntityObject(
                100L,
                new CommentTextValueObject("Контент комментария"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        Mockito.when(postRepository.findPostById(postId)).thenReturn(post);

        Mockito.when(
                commentRepository.saveComment(
                        Mockito.any(CommentEntityObject.class),
                        Mockito.eq(postId)))
                .thenReturn(Optional.of(savedComment));

        var result = postService.createComment(postId, request);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(100L, result.getId());

        Assertions.assertEquals("Контент комментария", result.getText());
    }

    // endregion

    // region updateComment Tests

    /**
     * <summary>
     * Проверяет изменение текста существующего комментария, принадлежащего указанному посту.
     * </summary>
     **/
    @Test
    void updateCommentShouldModifyTextAndReturnUpdatedDto()
    {
        var postId = 1L;

        var commentId = 50L;

        var request = new UpdateCommentRequestDto(commentId, "Обновленный текст комментария", postId);

        var post = new PostEntityObject(
                new PostTitleValueObject("Заголовок"),
                new PostTextValueObject("Текст"));

        ReflectionTestUtils.setField(post, "id", postId);

        var existingComment = new CommentEntityObject(
                commentId,
                new CommentTextValueObject("Старый текст комментария"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        Mockito.doAnswer(invocation -> {
            post.initializeComments(List.of(existingComment));
            return null;
        }).when(commentRepository).enrichPostsWithComments(Mockito.anyList());

        Mockito.when(postRepository.findPostById(postId)).thenReturn(post);

        var updatedComment = new CommentEntityObject(
                commentId,
                new CommentTextValueObject("Обновленный текст комментария"),
                existingComment.getCreatedAt(),
                OffsetDateTime.now()
        );

        Mockito.when(
                commentRepository.updateComment(
                        Mockito.any(CommentEntityObject.class),
                        Mockito.eq(postId)))
                .thenReturn(Optional.of(updatedComment));

        var result = postService.updateComment(postId, commentId, request);

        Assertions.assertNotNull(result);

        Assertions.assertEquals("Обновленный текст комментария", result.getText());
    }

    /**
     * <summary>
     * Проверяет, что метод выбрасывает исключение, если комментарий не принадлежит публикации.
     * </summary>
     **/
    @Test
    void updateCommentShouldThrowNotFoundExceptionWhenCommentIsMissingInPost()
    {
        var postId = 1L;

        var commentId = 50L;

        var request = new UpdateCommentRequestDto(commentId, "Текст", postId);

        var post = new PostEntityObject(
                new PostTitleValueObject("Заголовок"),
                new PostTextValueObject("Текст"));

        ReflectionTestUtils.setField(post, "id", postId);

        Mockito.when(postRepository.findPostById(postId)).thenReturn(post);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.updateComment(postId, commentId, request)
        );
    }

    // endregion

    // region deletePostById Tests

    /**
     * <summary>
     * Проверяет корректное удаление публикации и последующую очистку висячих тегов (orphaned tags).
     * </summary>
     **/
    @Test
    void deletePostByIdShouldReturnTrueAndClearTagsWhenSuccessful()
    {
        var postId = 1L;

        Mockito.when(postRepository.deletePostById(postId)).thenReturn(true);

        var result = postService.deletePostById(postId);

        Assertions.assertTrue(result);

        Mockito.verify(tagRepository, Mockito.times(1)).clearOrphanedTags();
    }

    // endregion
}