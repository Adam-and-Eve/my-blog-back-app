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
public class PostResponseDto
{
    // region Fields

    /**
     * Уникальный идентификатор сущности.
     **/
    private Long id;

    /**
     * Заголовок публикации.
     **/
    private String title;

    /**
     * Сообщение публикации в формате Markdown.
     **/
    private String text;

    /**
     * Список тегов, связанных с публикацией.
     **/
    private List<String> tags;

    /**
     * Количество лайков.
     **/
    private Long likesCount;

    /**
     * Количество комментариев.
     **/
    private Long commentsCount;

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

    public Long getId()
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
    public String getTitle()
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
    public String getText()
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
    public List<String> getTags()
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
    public Long getLikesCount()
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
    public Long getCommentsCount()
    {
        return commentsCount;
    }

    // endregion
}