package ru.yandex.practicum.services.blog.core.application.dtos.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <summary>
 * Объект передачи данных (DTO) для обновления существующей публикации.
 * Служит для передачи полной информации о посте из слоя представления (REST API) на
 * уровень бизнес-логики.
 * </summary>
 **/
public class UpdatePostRequestDto
{
    // region Fields

    /**
     * Заголовок публикации.
     **/
    @JsonProperty("title")
    private String title;

    /**
     * Сообщение публикации в формате Markdown.
     **/
    @JsonProperty("text")
    private String text;

    /**
     * Список тегов, связанных с публикацией.
     **/
    @JsonProperty("tags")
    private List<String> tags;

    // endregion

    // region Constructors

    public UpdatePostRequestDto()
    {

    }

    public UpdatePostRequestDto(
            final String title,
            final String text,
            final List<String> tags
    )
    {
        this.title = title;
        this.text = text;
        this.tags = tags;
    }

    // endregion

    // region Properties

    /**
     * Заголовок публикации.
     **/
    public String getTitle()
    {
        return title;
    }

    /**
     * Сообщение публикации в формате Markdown.
     **/
    public String getText()
    {
        return text;
    }

    /**
     * Список тегов, связанных с публикацией.
     **/
    public List<String> getTags()
    {
        return tags;
    }

    // endregion
}