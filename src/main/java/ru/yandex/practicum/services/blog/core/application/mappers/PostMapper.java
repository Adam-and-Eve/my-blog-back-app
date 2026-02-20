package ru.yandex.practicum.services.blog.core.application.mappers;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostResponseDto;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTagNameObject;

import java.util.stream.Collectors;

/**
 * <summary>
 * Маппер для преобразования доменных сущностей публикаций в объекты передачи данных (DTO).
 * Обеспечивает разделение внутренней логики домена и внешнего API.
 * </summary>
 **/
public final class PostMapper
{
    // region Fields



    // endregion

    // region Constructors



    // endregion

    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Преобразует доменную сущность PostEntityObject в PostResponseDto.
     * </summary>
     * <param name="post">
     * Доменная сущность поста.
     * </param>
     * <return>
     * @return Сформированный объект ответа PostResponseDto.
     * </return>
     **/
    public static PostResponseDto mapToResponseDto(final PostEntityObject post)
    {
        if (post == null)
        {
            return null;
        }
        else
        {
            return new PostResponseDto(
                    post.getId(),
                    post.getTitle().getValue(),
                    post.getText().getValue().length() > 128
                        ? post.getText().getValue().substring(0, 128) + "..."
                        : post.getText().getValue(),
                    post.getTags().stream()
                            .map(tag -> tag.getName().getValue())
                            .collect(Collectors.toList()),
                    post.getLikesCount(),
                    0L
            );
        }
    }

    // endregion
}