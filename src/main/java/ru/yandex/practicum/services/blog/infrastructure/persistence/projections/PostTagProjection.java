package ru.yandex.practicum.services.blog.infrastructure.persistence.projections;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.TagEntityObject;

/**
 * <summary>
 * Проекция данных для связи публикации и тега.
 * Используется для материализации данных из JOIN-запросов.
 * </summary>
 **/
public final class PostTagProjection
{
    // region Fields

    /*
     * Уникальный идентификатор сущности поста.
     */
    private final Long postId;

    /*
     * Сущность тега.
     */
    private final TagEntityObject tag;

    // endregion

    // region Constructors

    public PostTagProjection(
            final Long postId,
            final TagEntityObject tag)
    {
        this.postId = postId;

        this.tag = tag;
    }

    // endregion

    // region Properties

    /*
     * Возвращает уникальный идентификатор сущности поста.
     */
    public final Long getPostId()
    {
        return postId;
    }

    /*
     * Возвращает сущность тега.
     */
    public final TagEntityObject getTag()
    {
        return tag;
    }

    // endregion

    // region Methods



    // endregion
}