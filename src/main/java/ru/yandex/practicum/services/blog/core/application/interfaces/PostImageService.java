package ru.yandex.practicum.services.blog.core.application.interfaces;

import org.springframework.web.multipart.MultipartFile;

/**
 * Контракт сервиса для работы с изображениями публикаций.
 **/
public interface PostImageService
{
    // region Methods

    /**
     * <summary>
     * Получение изображения публикации по умолчанию.
     * </summary>
     * <return>
     * @return Изображение публикации по умолчанию.
     * </return>
     **/
    public byte[] getDefaultImage();

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
    public byte[] getPostImageBytesByPostId(Long postId);

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
    public void updatePostImage(final Long postId, final byte[] imageContent);

    // endregion
}