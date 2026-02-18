package ru.yandex.practicum.services.blog.core.application.dtos.posts;

import java.util.List;

/**
 * <summary>
 * Объект передачи данных (DTO) для формирования ответа о публикации.
 * Служит для передачи полной информации о посте из слоя бизнес-логики
 * на уровень представления (REST API). Обеспечивает иммутабельность
 * данных и независимость внешнего контракта от доменных сущностей.
 * </summary>
 **/
public final class PostResponseDto
{
    // region Fields

    /**
     * Уникальный идентификатор сущности.
     **/
    private final Long id;

    /**
     * Заголовок публикации.
     **/
    private final String title;

    /**
     * Сообщение публикации в формате Markdown.
     **/
    private final String text;

    /**
     * Список тегов, связанных с публикацией.
     **/
    private final List<String> tags;

    /**
     * Количество лайков.
     **/
    private final Long likesCount;

    /**
     * Количество комментариев.
     **/
    private final Long commentsCount;

    // endregion

    // region Constructors

    public PostResponseDto(
            Long id,
            String title,
            String text,
            List<String> tags,
            Long likesCount,
            Long commentsCount
    )
    {
        this.id = id;

        this.title = title;

        this.text = text;

        this.tags = tags;

        this.likesCount = likesCount;

        this.commentsCount = commentsCount;
    }

    // endregion

    // region Properties

    public final  Long getId()
    {
        return id;
    }

    /**
     * <summary>
     * Возвращает заголовок публикации.
     * </summary>
     * <return>
     * @return Значение заголовка.
     * </return>
     **/
    public final String getTitle()
    {
        return title;
    }

    /**
     * <summary>
     * Возвращает сообщение публикации в формате Markdown.
     * </summary>
     * <return>
     * @return Значение сообщения в формате Markdown.
     * </return>
     **/
    public final String getText()
    {
        return text;
    }

    /**
     * <summary>
     * Возвращает список тегов публикации.
     * </summary>
     * <return>
     * @return Список тегов публикации.
     * </return>
     **/
    public final List<String> getTags()
    {
        return tags;
    }

    /**
     * <summary>
     * Возвращает количество лайков публикации.
     * </summary>
     * <return>
     * @return Количество лайков публикации.
     * </return>
     **/
    public final Long getLikesCount()
    {
        return likesCount;
    }

    /**
     * <summary>
     * Возвращает количество комментариев к публикации.
     * </summary>
     * <return>
     * @return Количество комментариев к публикации.
     * </return>
     **/
    public final Long getCommentsCount()
    {
        return commentsCount;
    }

    // endregion
}