package ru.yandex.practicum.services.blog.core.application.mappers;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.CommentResponseDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostResponseDto;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;

/**
 * <summary>
 * Маппер для преобразования доменных сущностей публикаций в объекты передачи данных (DTO).
 * Обеспечивает разделение внутренней логики домена и внешнего API.
 * </summary>
 **/
public final class CommentMapper
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
     * Преобразует доменную сущность CommentEntityObject в CommentResponseDto.
     * </summary>
     * <param name="comment">
     * Доменная сущность комментария.
     * </param>
     * <return>
     * @return Сформированный объект ответа CommentResponseDto.
     * </return>
     **/
    public static CommentResponseDto mapToResponseDto(final Long postId, final CommentEntityObject comment)
    {
        if (comment == null)
        {
            return null;
        }
        else
        {
            return new CommentResponseDto(
                    comment.getId(),
                    comment.getText().getValue(),
                    postId
            );
        }
    }

    // endregion
}