package ru.yandex.practicum.services.blog.infrastructure.persistence.projections;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;

/**
 * <summary>
 * Проекция данных для связи публикации и комментария.
 * Используется для материализации данных из JOIN-запросов.
 * </summary>
 **/
public final class PostCommentProjection
{
    // region Fields

    /*
     * Уникальный идентификатор сущности поста.
     */
    private final Long postId;

    /*
     * Сущность комментария.
     */
    private final CommentEntityObject comment;

    // endregion

    // region Constructors

    public PostCommentProjection(final Long postId, final CommentEntityObject comment)
    {
        this.postId = postId;
        this.comment = comment;
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
     * Возвращает сущность комментария.
     */
    public final CommentEntityObject getComment()
    {
        return comment;
    }

    // endregion
}