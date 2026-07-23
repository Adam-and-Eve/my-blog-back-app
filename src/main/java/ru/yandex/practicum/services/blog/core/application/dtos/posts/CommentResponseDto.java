package ru.yandex.practicum.services.blog.core.application.dtos.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <summary>
 * Объект передачи данных (DTO) для формирования ответа о комментарии.
 * Служит для передачи полной информации о комментарии из слоя бизнес-логики
 * на уровень представления (REST API). Обеспечивает иммутабельность
 * данных и независимость внешнего контракта от доменных сущностей.
 * </summary>
 **/
public class CommentResponseDto
{
    // region Fields

    /**
     * Идентификатор комментария.
     **/
    @JsonProperty("id")
    private final Long id;

    /**
     * Сообщение комментария.
     **/
    @JsonProperty("text")
    private final String text;

    /**
     * Идентификатор публикации.
     **/
    @JsonProperty("postId")
    private final Long postId;

    // endregion

    // region Constructors

    public CommentResponseDto(
            final Long id,
            final String text,
            final Long postId
    )
    {
        this.id = id;
        this.text = text;
        this.postId = postId;
    }

    // endregion

    // region Properties

    /**
     * Идентификатор комментария.
     **/
    public final Long getId()
    {
        return id;
    }

    /**
     * Сообщение комментария.
     **/
    public final String getText()
    {
        return text;
    }

    /**
     * Идентификатор публикации.
     **/
    public final Long getPostId()
    {
        return postId;
    }

    // endregion
}