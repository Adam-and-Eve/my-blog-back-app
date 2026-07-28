package ru.yandex.practicum.services.blog.core.application.interfaces;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.CommentResponseDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CreateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.UpdateCommentRequestDto;

import java.util.List;

/**
 * Контракт сервиса управления комментариями к публикациям.
 **/
public interface PostCommentService
{
    // region Methods

    /**
     * <summary>
     * Получение комментариев по идентификатору публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Комментарии публикации.
     * </return>
     **/
    public List<CommentResponseDto> getCommentsByPostId(final Long postId);

    /**
     * <summary>
     * Добавление комментария к публикации.
     * </summary>
     * <param name="postId">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="comment">
     * Новый комментарий к публикации.
     * </param>
     * <return>
     * @return Комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    public CommentResponseDto createComment(final Long postId, final CreateCommentRequestDto comment);

    /**
     * <summary>
     * Обновления комментария публикации.
     * </summary>
     * <param name="postId">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="comment">
     * Обновленный комментарий к публикации.
     * </param>
     * <return>
     * @return Обновленный комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    public CommentResponseDto updateComment(final Long postId, final Long commentId, final UpdateCommentRequestDto commentDto);

    /**
     * <summary>
     * Удаления комментария публикации.
     * </summary>
     * <param name="postId">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="commentId">
     * Уникальный идентификатор комментария.
     * </param>
     **/
    public void deleteComment(final Long postId, final Long commentId);

    // endregion
}