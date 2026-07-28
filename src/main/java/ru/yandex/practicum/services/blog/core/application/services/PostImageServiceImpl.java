package ru.yandex.practicum.services.blog.core.application.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.ImageRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostImageService;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;

import java.io.IOException;

/**
 * Сервис для работы с изображениями публикаций.
 **/
@Service
@Transactional(readOnly = true)
public class PostImageServiceImpl implements PostImageService
{
    // region Fields

    /*
     * Репозиторий публикаций.
     */
    private final PostRepository postRepository;

    /*
     * Репозиторий изображений.
     */
    private final ImageRepository imageRepository;

    // endregion

    // region Constructors

    public PostImageServiceImpl(
            final PostRepository postRepository,
            final ImageRepository imageRepository)
    {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Получение изображения публикации по умолчанию.
     * </summary>
     * <return>
     * @return Изображение публикации по умолчанию.
     * </return>
     **/
    @Override
    public byte[] getDefaultImage()
    {
        var resource = new ClassPathResource("static/images/default-post.png");

        try (var inputStream = resource.getInputStream())
        {
            return inputStream.readAllBytes();
        }
        catch (IOException ex)
        {
            throw new IllegalStateException(
                    "Не удалось прочитать default-post.png",
                    ex);
        }
    }

    /**
     * <summary>
     * Поиск изображения публикации в базе данных сервиса.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Содержимое изображения публикации.
     * </param>
     **/
    @Override
    @Transactional
    public byte[] getPostImageBytesByPostId(Long postId)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не должен быть пустым");
        }

        if (postRepository.findPostById(postId) == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + postId + "'.");
        }

        var imageContent = imageRepository.findPostImageBytesByPostId(postId);

        if (imageContent == null)
        {
            return getDefaultImage();
        }

        return imageContent;
    }

    /**
     * <summary>
     * Сохранение и изменение изображения публикации в базе данных сервиса.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <param name="imageContent">
     * Содержимое изображения публикации.
     * </param>
     **/
    @Override
    @Transactional
    public void updatePostImage(final Long postId, final byte[] imageContent)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не должен быть пустым.");
        }

        if (imageContent == null || imageContent.length == 0)
        {
            throw new ApplicationValidationException(
                    "Изображение не может быть пустым.");
        }

        if (postRepository.findPostById(postId) == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + postId + "'.");
        }

        try
        {
            imageRepository.saveOrUpdatePostImage(postId, imageContent);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Не удалось прочитать изображение", ex);
        }
    }

    // endregion
}