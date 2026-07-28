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
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CreatePostRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.UpdateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.UpdatePostRequestDto;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.CommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.ImageRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.TagRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <summary>
 * Мок-тесты для проверки бизнес-логики основного сервиса публикаций (PostServiceImpl).
 * </summary>
 **/
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest
{
    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostServiceImpl postService;

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при некорректном номере страницы.
     * </summary>
     **/
    @Test
    void getPostsPageShouldThrowValidationExceptionWhenPageNumberIsInvalid()
    {
        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.getPostsPage("search", null, 10L)
        );

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.getPostsPage("search", 0L, 10L)
        );
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при некорректном размере страницы.
     * </summary>
     **/
    @Test
    void getPostsPageShouldThrowValidationExceptionWhenPageSizeIsInvalid()
    {
        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.getPostsPage("search", 1L, null)
        );

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.getPostsPage("search", 1L, 0L)
        );
    }

    /**
     * <summary>
     * Проверяет корректный возврат пустой пагинации, если количество постов в репозитории равно 0.
     * </summary>
     **/
    @Test
    void getPostsPageShouldReturnEmptyResponseWhenTotalPostsIsZero()
    {
        Mockito.when(postRepository.countAllPosts(Mockito.any())).thenReturn(0L);

        var result = postService.getPostsPage("search", 1L, 10L);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(result.getPosts().isEmpty());

        Assertions.assertEquals(0L, result.getLastPage());

        Assertions.assertFalse(result.getHasNext());

        Assertions.assertFalse(result.getHasPrev());
    }

    /**
     * <summary>
     * Успешное получение заполненной страницы публикаций с обогащением тегами и комментариями.
     * </summary>
     **/
    @Test
    void getPostsPageShouldReturnContentWhenHappyPath()
    {
        Mockito.when(postRepository.countAllPosts(Mockito.any())).thenReturn(15L);

        var mockPost = Mockito.mock(PostEntityObject.class);

        var mockTitle = Mockito.mock(PostTitleValueObject.class);

        var mockText = Mockito.mock(PostTextValueObject.class);

        Mockito.when(mockPost.getId()).thenReturn(1L);

        Mockito.when(mockPost.getTitle()).thenReturn(mockTitle);

        Mockito.when(mockTitle.getValue()).thenReturn("Заголовок");

        Mockito.when(mockPost.getText()).thenReturn(mockText);

        Mockito.when(mockText.getValue()).thenReturn("Текст");

        Mockito.when(mockPost.getTags()).thenReturn(Set.of());

        Mockito.when(mockPost.getLikesCount()).thenReturn(5L);

        Mockito.when(mockPost.getCommentsCount()).thenReturn(2L);

        Mockito.when(postRepository.findAllPosts(Mockito.any(), Mockito.eq(0L), Mockito.eq(10L)))
                .thenReturn(List.of(mockPost));

        var result = postService.getPostsPage("search", 1L, 10L);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(1, result.getPosts().size());

        Assertions.assertEquals(2L, result.getLastPage());

        Assertions.assertTrue(result.getHasNext());

        Assertions.assertFalse(result.getHasPrev());

        Mockito.verify(tagRepository, Mockito.times(1)).enrichPostsWithTags(Mockito.anyList());

        Mockito.verify(commentRepository, Mockito.times(1)).enrichPostsWithComments(Mockito.anyList());
    }

    /**
     * <summary>
     * Проверяет корректный возврат пустой страницы, если запрошен номер страницы за пределами последней.
     * </summary>
     **/
    @Test
    void getPostsPageShouldReturnEmptyPageWhenRequestedPageIsBeyondLastPage()
    {
        Mockito.when(postRepository.countAllPosts(Mockito.any())).thenReturn(5L);

        var result = postService.getPostsPage("search", 2L, 5L);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(result.getPosts().isEmpty());

        Assertions.assertEquals(1L, result.getLastPage());

        Assertions.assertFalse(result.getHasNext());

        Assertions.assertTrue(result.getHasPrev());
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при передаче null вместо идентификатора публикации.
     * </summary>
     **/
    @Test
    void getPostByIdShouldThrowValidationExceptionWhenIdIsNull()
    {
        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.getPostById(null)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если публикация по ID отсутствует.
     * </summary>
     **/
    @Test
    void getPostByIdShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        Mockito.when(postRepository.findPostById(1L)).thenReturn(null);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.getPostById(1L)
        );
    }

    /**
     * <summary>
     * Успешное получение публикации по идентификатору.
     * </summary>
     **/
    @Test
    void getPostByIdShouldReturnMappedDtoWhenPostExists()
    {
        var mockPost = Mockito.mock(PostEntityObject.class);

        var mockTitle = Mockito.mock(PostTitleValueObject.class);

        var mockText = Mockito.mock(PostTextValueObject.class);

        Mockito.when(mockPost.getId()).thenReturn(1L);

        Mockito.when(mockPost.getTitle()).thenReturn(mockTitle);

        Mockito.when(mockTitle.getValue()).thenReturn("Заголовок");

        Mockito.when(mockPost.getText()).thenReturn(mockText);

        Mockito.when(mockText.getValue()).thenReturn("Текст");

        Mockito.when(mockPost.getTags()).thenReturn(Set.of());

        Mockito.when(mockPost.getLikesCount()).thenReturn(10L);

        Mockito.when(mockPost.getCommentsCount()).thenReturn(0L);

        Mockito.when(postRepository.findPostById(1L)).thenReturn(mockPost);

        Mockito.when(commentRepository.findCommentsByPostId(1L)).thenReturn(List.of());

        var result = postService.getPostById(1L);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(1L, result.getId());

        Assertions.assertEquals("Заголовок", result.getTitle());

        Mockito.verify(tagRepository, Mockito.times(1)).enrichPostsWithTags(Mockito.anyList());

        Mockito.verify(mockPost, Mockito.times(1)).initializeComments(Mockito.anyList());
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException при попытке лайкнуть несуществующий пост.
     * </summary>
     **/
    @Test
    void likePostShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        Mockito.when(postRepository.incrementLikesCount(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.likePost(1L)
        );
    }

    /**
     * <summary>
     * Успешное увеличение лайков публикации.
     * </summary>
     **/
    @Test
    void likePostShouldReturnNewLikesCountWhenSuccessful()
    {
        Mockito.when(postRepository.incrementLikesCount(1L)).thenReturn(Optional.of(42L));

        var result = postService.likePost(1L);

        Assertions.assertEquals(42L, result);
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException, если тело запроса на создание null.
     * </summary>
     **/
    @Test
    void createPostShouldThrowValidationExceptionWhenRequestIsNull()
    {
        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.createPost(null)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если репозиторий вернул пустой Optional при создании.
     * </summary>
     **/
    @Test
    void createPostShouldThrowNotFoundExceptionWhenRepositoryFailsToCreate()
    {
        var request = new CreatePostRequestDto("Заголовок", "Текст", List.of());

        Mockito.when(postRepository.createPost(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.createPost(request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если созданный объект не удается считать из базы после вставки.
     * </summary>
     **/
    @Test
    void createPostShouldThrowNotFoundExceptionWhenCreatedPostCannotBeFound()
    {
        var request = new CreatePostRequestDto("Заголовок", "Текст", List.of());

        Mockito.when(postRepository.createPost(Mockito.any())).thenReturn(Optional.of(1L));

        Mockito.when(postRepository.findPostById(1L)).thenReturn(null);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.createPost(request)
        );
    }

    /**
     * <summary>
     * Успешное создание публикации с корректной обработкой прикрепляемых тегов.
     * </summary>
     **/
    @Test
    void createPostShouldCreateAndReturnDtoWithTagsWhenHappyPath()
    {
        var tags = List.of("java", "spring");

        var request = new CreatePostRequestDto("Новый пост", "Контент", tags);

        var mockPost = Mockito.mock(PostEntityObject.class);

        var mockTitle = Mockito.mock(PostTitleValueObject.class);

        var mockText = Mockito.mock(PostTextValueObject.class);

        Mockito.when(mockPost.getId()).thenReturn(1L);

        Mockito.when(mockPost.getTitle()).thenReturn(mockTitle);

        Mockito.when(mockTitle.getValue()).thenReturn("Новый пост");

        Mockito.when(mockPost.getText()).thenReturn(mockText);

        Mockito.when(mockText.getValue()).thenReturn("Контент");

        Mockito.when(mockPost.getTags()).thenReturn(Set.of());

        Mockito.when(mockPost.getLikesCount()).thenReturn(0L);

        Mockito.when(mockPost.getCommentsCount()).thenReturn(0L);

        Mockito.when(postRepository.createPost(Mockito.any())).thenReturn(Optional.of(1L));

        Mockito.when(postRepository.findPostById(1L)).thenReturn(mockPost);

        Mockito.when(tagRepository.getOrCreateTags(tags)).thenReturn(List.of(10L, 11L));

        var result = postService.createPost(request);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(1L, result.getId());

        Assertions.assertEquals("Новый пост", result.getTitle());

        Mockito.verify(tagRepository, Mockito.times(1)).updatePostTags(1L, List.of(10L, 11L));

        Mockito.verify(tagRepository, Mockito.times(1)).enrichPostsWithTags(Mockito.anyList());
    }

    /**
     * <summary>
     * Проверяет валидацию параметров на null при обновлении публикации.
     * </summary>
     **/
    @Test
    void updatePostByIdShouldThrowValidationExceptionWhenParametersAreNull()
    {
        var request = new UpdatePostRequestDto("Заголовок", "Текст", List.of());

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.updatePostById(null, request)
        );

        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.updatePostById(1L, null)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если обновляемый пост отсутствует в БД.
     * </summary>
     **/
    @Test
    void updatePostByIdShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        var request = new UpdatePostRequestDto("Заголовок", "Текст", List.of());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(null);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.updatePostById(1L, request)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если репозиторий вернул false при попытке обновления данных.
     * </summary>
     **/
    @Test
    void updatePostByIdShouldThrowNotFoundExceptionWhenRepositoryUpdateFails()
    {
        var request = new UpdatePostRequestDto("Заголовок", "Текст", List.of());

        var mockPost = Mockito.mock(PostEntityObject.class);

        Mockito.when(postRepository.findPostById(1L)).thenReturn(mockPost);

        Mockito.when(postRepository.updatePostById(mockPost)).thenReturn(false);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.updatePostById(1L, request)
        );
    }

    /**
     * <summary>
     * Успешное обновление текстовых полей публикации и очистка потерянных тегов.
     * </summary>
     **/
    @Test
    void updatePostByIdShouldUpdateAndReturnDtoWhenHappyPath()
    {
        var request = new UpdatePostRequestDto("Обновленный заголовок", "Новый текст", List.of());

        var mockPost = Mockito.mock(PostEntityObject.class);

        var mockTitle = Mockito.mock(PostTitleValueObject.class);

        var mockText = Mockito.mock(PostTextValueObject.class);

        Mockito.when(mockPost.getId()).thenReturn(1L);

        Mockito.when(mockPost.getTitle()).thenReturn(mockTitle);

        Mockito.when(mockTitle.getValue()).thenReturn("Обновленный заголовок");

        Mockito.when(mockPost.getText()).thenReturn(mockText);

        Mockito.when(mockText.getValue()).thenReturn("Новый текст");

        Mockito.when(mockPost.getTags()).thenReturn(Set.of());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(mockPost);

        Mockito.when(postRepository.updatePostById(mockPost)).thenReturn(true);

        var result = postService.updatePostById(1L, request);

        Assertions.assertNotNull(result);

        Assertions.assertEquals("Обновленный заголовок", result.getTitle());

        Mockito.verify(tagRepository, Mockito.times(1)).updatePostTags(1L, List.of());

        Mockito.verify(tagRepository, Mockito.times(1)).clearOrphanedTags();
    }

    /**
     * <summary>
     * Успешное обновление публикации с изменением списка тегов на новые.
     * </summary>
     **/
    @Test
    void updatePostByIdShouldUpdateTagsWhenNewTagsAreProvided()
    {
        var newTags = List.of("java", "architecture");

        var request = new UpdatePostRequestDto("Заголовок", "Текст", newTags);

        var mockPost = Mockito.mock(PostEntityObject.class);

        var mockTitle = Mockito.mock(PostTitleValueObject.class);

        var mockText = Mockito.mock(PostTextValueObject.class);

        Mockito.when(mockPost.getId()).thenReturn(1L);

        Mockito.when(mockPost.getTitle()).thenReturn(mockTitle);

        Mockito.when(mockTitle.getValue()).thenReturn("Заголовок");

        Mockito.when(mockPost.getText()).thenReturn(mockText);

        Mockito.when(mockText.getValue()).thenReturn("Текст");

        Mockito.when(mockPost.getTags()).thenReturn(Set.of());

        Mockito.when(postRepository.findPostById(1L)).thenReturn(mockPost);

        Mockito.when(postRepository.updatePostById(mockPost)).thenReturn(true);

        Mockito.when(tagRepository.getOrCreateTags(newTags)).thenReturn(List.of(10L, 20L));

        var result = postService.updatePostById(1L, request);

        Assertions.assertNotNull(result);

        Mockito.verify(tagRepository, Mockito.times(1)).updatePostTags(1L, List.of(10L, 20L));

        Mockito.verify(tagRepository, Mockito.times(1)).clearOrphanedTags();
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при передаче null-идентификатора при удалении.
     * </summary>
     **/
    @Test
    void deletePostByIdShouldThrowValidationExceptionWhenIdIsNull()
    {
        Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postService.deletePostById(null)
        );
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если репозиторий вернул false при удалении публикации.
     * </summary>
     **/
    @Test
    void deletePostByIdShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        Mockito.when(postRepository.deletePostById(1L)).thenReturn(false);

        Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postService.deletePostById(1L)
        );
    }

    /**
     * <summary>
     * Успешный сценарий удаления публикации по идентификатору.
     * </summary>
     **/
    @Test
    void deletePostByIdShouldReturnTrueWhenSuccessful()
    {
        Mockito.when(postRepository.deletePostById(1L)).thenReturn(true);

        var result = postService.deletePostById(1L);

        Assertions.assertTrue(result);

        Mockito.verify(tagRepository, Mockito.times(1)).clearOrphanedTags();
    }
}