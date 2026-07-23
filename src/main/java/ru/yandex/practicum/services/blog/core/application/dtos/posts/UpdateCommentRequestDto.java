package ru.yandex.practicum.services.blog.core.application.dtos.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <summary>
 * Объект передачи данных (DTO) для изменения существующего комментария.
 * Служит для передачи полной информации о комментарии из слоя представления (REST API) на
 * уровень бизнес-логики.
 * </summary>
 **/
public final class UpdateCommentRequestDto
{
    // region Fields

    /**
     * Идентификатор комментария.
     **/
    @JsonProperty("id")
    private Long id;

    /**
     * Сообщение комментария.
     **/
    @JsonProperty("text")
    private String text;

    /**
     * Идентификатор публикации.
     **/
    @JsonProperty("postId")
    private Long postId;

    // endregion

    // region Constructors

    public UpdateCommentRequestDto()
    {

    }

    public UpdateCommentRequestDto(
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
    public Long getId()
    {
        return id;
    }

    /**
     * Сообщение комментария.
     **/
    public String getText()
    {
        return text;
    }

    /**
     * Идентификатор публикации.
     **/
    public Long getPostId()
    {
        return postId;
    }

    // endregion
}