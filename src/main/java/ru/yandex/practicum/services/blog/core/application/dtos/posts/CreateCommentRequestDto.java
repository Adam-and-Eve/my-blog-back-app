package ru.yandex.practicum.services.blog.core.application.dtos.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <summary>
 * Объект передачи данных (DTO) для создания нового комментария.
 * Служит для передачи полной информации о комментарии из слоя представления (REST API) на
 * уровень бизнес-логики.
 * </summary>
 **/
public final class CreateCommentRequestDto
{
    // region Fields

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

    public CreateCommentRequestDto()
    {

    }

    public CreateCommentRequestDto(
            final String text,
            final Long postId
    )
    {
        this.text = text;
        this.postId = postId;
    }

    // endregion

    // region Properties

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