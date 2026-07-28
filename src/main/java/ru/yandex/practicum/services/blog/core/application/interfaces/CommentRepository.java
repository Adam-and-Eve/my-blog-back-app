package ru.yandex.practicum.services.blog.core.application.interfaces;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;

import java.util.List;
import java.util.Optional;

/**
 * <summary>
 * Основной контракт репозитория для работы с комментариями на уровне базы данных.
 * </summary>
 **/
public interface CommentRepository
{
    // region Methods

    /**
     * <summary>
     * Обогащает переданный список публикаций связанными с ними комментариями.
     * Выполняет пакетный запрос к базе данных для извлечения
     * всех комментариев, привязанных к указанным постам, и распределяет их по
     * соответствующим объектам доменных сущностей.
     * </summary>
     * <param name="posts">
     * Список доменных сущностей публикаций, которые необходимо обогатить комментариями.
     * Если список пуст или равен null, выполнение метода прерывается без обращения к БД.
     * </param>
     **/
    public void enrichPostsWithComments(final List<PostEntityObject> posts);

    /**
     * <summary>
     * Получает комментарии по идентификатору публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * Комментарии публикации.
     * </return>
     **/
    public List<CommentEntityObject> findCommentsByPostId(final Long postId);

    /**
     * <summary>
     * Добавляет комментарий к публикации.
     * </summary>
     * <param name="comment">
     * Новый комментарий.
     * </param>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Сохраненный комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    public Optional<CommentEntityObject> saveComment(final CommentEntityObject comment, final Long postId);

    /**
     * <summary>
     * Обновляет комментарий публикации.
     * </summary>
     * <param name="comment">
     * Обновленный комментарий.
     * </param>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Обновленный комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    public Optional<CommentEntityObject> updateComment(final CommentEntityObject comment, final Long postId);

    /**
     * <summary>
     * Удаляет комментарий публикации.
     * </summary>
     * <param name="commentId">
     * Идентификатор комментария
     * </param>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Статус выполнения операции.
     * </return>
     **/
    public Boolean deleteComment(final Long commentId, final Long postId);

    // endregion
}