package ru.yandex.practicum.services.blog.core.application.dtos.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <summary>
 * Объект передачи данных (DTO) для создания новой публикации.
 * Служит для передачи полной информации о посте из слоя представления (REST API) на
 * уровень бизнес-логики.
 * </summary>
 **/
public class CreatePostRequestDto
{
    // region Fields

    /**
     * Заголовок публикации.
     **/
    @JsonProperty("title")
    private String _title;

    /**
     * Сообщение публикации в формате Markdown.
     **/
    @JsonProperty("text")
    private String _text;

    /**
     * Список тегов, связанных с публикацией.
     **/
    @JsonProperty("tags")
    private List<String> _tags;

    // endregion

    // region Constructors

    public CreatePostRequestDto()
    {

    }

    public CreatePostRequestDto(
            final String title,
            final String text,
            final List<String> tags
    )
    {
        _title = title;
        _text = text;
        _tags = tags;
    }

    // endregion

    // region Properties

    /**
     * Заголовок публикации.
     **/
    public String getTitle()
    {
        return _title;
    }

    /**
     * Сообщение публикации в формате Markdown.
     **/
    public String getText()
    {
        return _text;
    }

    /**
     * Список тегов, связанных с публикацией.
     **/
    public List<String> getTags()
    {
        return _tags;
    }

    // endregion
}