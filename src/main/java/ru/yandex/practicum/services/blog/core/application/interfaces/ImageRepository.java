package ru.yandex.practicum.services.blog.core.application.interfaces;

/**
 * <summary>
 * Основной контракт репозитория для работы с изображениями на уровне базы данных.
 * </summary>
 **/
public interface ImageRepository
{
    // region Methods

    /**
     * <summary>
     * Добавляет или обновляет в базе данных изображение публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <param name="imageContent">
     * Изображение публикации.
     * </param>
     * <return>
     * Статус выполнения операции.
     * </return>
     **/
    public Boolean saveOrUpdatePostImage(final Long postId, final byte[] imageContent);

    /**
     * <summary>
     * Получает изображение по идентификатору публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * Изображение публикации.
     * </return>
     **/
    public byte[] findPostImageBytesByPostId(final Long postId);

    // endregion
}