package ru.yandex.practicum.services.blog.core.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.ImageRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * <summary>
 * Мок-тесты для проверки бизнес-логики сервиса изображений (PostImageServiceImpl).
 * </summary>
 **/
@ExtendWith(MockitoExtension.class)
class PostImageServiceImplTest
{
    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private PostImageServiceImpl postImageService;

    /**
     * <summary>
     * Проверяет успешное считывание дефолтного изображения из ресурсов.
     * </summary>
     **/
    @Test
    void getDefaultImageShouldReturnBytesWhenResourceExists()
    {
        var bytes = postImageService.getDefaultImage();

        Assertions.assertNotNull(bytes);

        Assertions.assertTrue(bytes.length > 0, "Дефолтный массив байтов изображения не должен быть пустым");
    }

    /**
     * <summary>
     * Проверяет выброс IllegalStateException, если при чтении файла ресурсов падает IOException.
     * Использует mockConstruction для перехвата создания ClassPathResource.
     * </summary>
     **/
    @Test
    void getDefaultImageShouldThrowIllegalStateExceptionWhenIOExceptionOccurs()
    {
        try (var mockedConstruction = Mockito.mockConstruction(ClassPathResource.class, (mock, context) ->
        {
            Mockito.when(mock.getInputStream()).thenThrow(new IOException("Тестовый сбой ввода-вывода"));
        }))
        {
            var exception = Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> postImageService.getDefaultImage()
            );

            Assertions.assertTrue(exception.getMessage().contains("Не удалось прочитать default-post.png"));

            Assertions.assertInstanceOf(IOException.class, exception.getCause());
        }
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException, если метод вызван с null вместо ID публикации.
     * </summary>
     **/
    @Test
    void getPostImageBytesShouldThrowValidationExceptionWhenPostIdIsNull()
    {
        var exception = Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postImageService.getPostImageBytesByPostId(null)
        );

        Assertions.assertEquals("Идентификатор публикации не должен быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException, если публикации с переданным ID не существует в базе.
     * </summary>
     **/
    @Test
    void getPostImageBytesShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        var postId = 1L;

        Mockito.when(postRepository.findPostById(postId)).thenReturn(null);

        var exception = Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postImageService.getPostImageBytesByPostId(postId)
        );

        Assertions.assertTrue(exception.getMessage().contains("Не удалось найти публикацию с id '" + postId + "'"));
    }

    /**
     * <summary>
     * Проверяет возврат дефолтной картинки, если у существующего поста в репозитории нет своего кастомного изображения (вернулся null).
     * </summary>
     **/
    @Test
    void getPostImageBytesShouldReturnDefaultImageWhenCustomImageIsNull()
    {
        var postId = 1L;

        var dummyPost = new PostEntityObject(postId, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(postId)).thenReturn(dummyPost);

        Mockito.when(imageRepository.findPostImageBytesByPostId(postId)).thenReturn(null);

        var resultBytes = postImageService.getPostImageBytesByPostId(postId);

        Assertions.assertNotNull(resultBytes);

        Assertions.assertTrue(resultBytes.length > 0);
    }

    /**
     * <summary>
     * Успешный сценарий получения сохраненного изображения публикации.
     * </summary>
     **/
    @Test
    void getPostImageBytesShouldReturnCustomImageBytesWhenImageExists()
    {
        var postId = 1L;

        var dummyPost = new PostEntityObject(postId, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        var expectedBytes = new byte[]{10, 20, 30, 40};

        Mockito.when(postRepository.findPostById(postId)).thenReturn(dummyPost);

        Mockito.when(imageRepository.findPostImageBytesByPostId(postId)).thenReturn(expectedBytes);

        var resultBytes = postImageService.getPostImageBytesByPostId(postId);

        Assertions.assertNotNull(resultBytes);

        Assertions.assertArrayEquals(expectedBytes, resultBytes);
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при обновлении, если передан null вместо ID публикации.
     * </summary>
     **/
    @Test
    void updatePostImageShouldThrowValidationExceptionWhenPostIdIsNull()
    {
        var dummyBytes = new byte[]{1, 2, 3};

        var exception = Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postImageService.updatePostImage(null, dummyBytes)
        );

        Assertions.assertEquals("Идентификатор публикации не должен быть пустым.", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при обновлении, если передан null вместо массива байтов.
     * </summary>
     **/
    @Test
    void updatePostImageShouldThrowValidationExceptionWhenBytesArrayIsNull()
    {
        var exception = Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postImageService.updatePostImage(1L, null)
        );

        Assertions.assertEquals("Изображение не может быть пустым.", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс ApplicationValidationException при обновлении, если передан пустой массив байтов (length == 0).
     * </summary>
     **/
    @Test
    void updatePostImageShouldThrowValidationExceptionWhenBytesArrayIsEmpty()
    {
        var exception = Assertions.assertThrows(
                ApplicationValidationException.class,
                () -> postImageService.updatePostImage(1L, new byte[0])
        );

        Assertions.assertEquals("Изображение не может быть пустым.", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс EntityObjectNotFoundException при попытке обновить изображение несуществующей публикации.
     * </summary>
     **/
    @Test
    void updatePostImageShouldThrowNotFoundExceptionWhenPostDoesNotExist()
    {
        var postId = 1L;

        var dummyBytes = new byte[]{1, 2, 3};

        Mockito.when(postRepository.findPostById(postId)).thenReturn(null);

        var exception = Assertions.assertThrows(
                EntityObjectNotFoundException.class,
                () -> postImageService.updatePostImage(postId, dummyBytes)
        );

        Assertions.assertTrue(exception.getMessage().contains("Не удалось найти публикацию с id '" + postId + "'"));
    }

    /**
     * <summary>
     * Проверяет перехват непредвиденных исключений репозитория сохранений и оборачивание их в IllegalStateException.
     * </summary>
     **/
    @Test
    void updatePostImageShouldThrowIllegalStateExceptionWhenRepositoryThrowsRuntimeException()
    {
        var postId = 1L;

        var dummyBytes = new byte[]{1, 2, 3};

        var dummyPost = new PostEntityObject(postId, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(postId)).thenReturn(dummyPost);

        Mockito.doThrow(new RuntimeException("Сбой подключения к БД"))
                .when(imageRepository).saveOrUpdatePostImage(postId, dummyBytes);

        var exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> postImageService.updatePostImage(postId, dummyBytes)
        );

        Assertions.assertEquals("Не удалось прочитать изображение", exception.getMessage());

        Assertions.assertInstanceOf(RuntimeException.class, exception.getCause());
    }

    /**
     * <summary>
     * Успешный сценарий обновления/сохранения изображения публикации.
     * </summary>
     **/
    @Test
    void updatePostImageShouldExecuteSuccessfullyWhenHappyPath()
    {
        var postId = 1L;

        var dummyBytes = new byte[]{5, 6, 7, 8};

        var dummyPost = new PostEntityObject(postId, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), 0L, OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(postRepository.findPostById(postId)).thenReturn(dummyPost);

        Assertions.assertDoesNotThrow(() -> postImageService.updatePostImage(postId, dummyBytes));

        Mockito.verify(imageRepository, Mockito.times(1)).saveOrUpdatePostImage(postId, dummyBytes);
    }
}