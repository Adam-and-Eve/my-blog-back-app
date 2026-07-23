package ru.yandex.practicum.services.blog.core.application.interfaces;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;

import java.util.List;

/**
 * <summary>
 * Основной контракт репозитория для работы с тегами на уровне базы данных.
 * </summary>
 **/
public interface ITagRepository
{
    // region Methods

    /**
     * <summary>
     * Обогащает переданный список публикаций связанными с ними тегами.
     * Выполняет пакетный запрос к базе данных для извлечения
     * всех тегов, привязанных к указанным постам, и распределяет их по
     * соответствующим объектам доменных сущностей.
     * </summary>
     * <param name="posts">
     * Список доменных сущностей публикаций, которые необходимо обогатить тегами.
     * Если список пуст или равен null, выполнение метода прерывается без обращения к БД.
     * </param>
     **/
    public void enrichPostsWithTags(final List<PostEntityObject> posts);

    /**
     * <summary>
     * Добавляет в базу данных теги и возвращает список их идентификаторов.
     * </summary>
     * <param name="tags">
     * Список тегов.
     * </param>
     **/
    public List<Long> getOrCreateTags(final List<String> tags);

    /**
     * <summary>
     * Обновляет теги публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <param name="tagIds">
     * Cписок идентификаторов тегов..
     * </param>
     **/
    public void updatePostTags(final Long postId, final List<Long> tagIds);

    /**
     * <summary>
     * Удаляет из базы данных все теги, которые не привязаны ни к одной публикации.
     * </summary>
     **/
    public void clearOrphanedTags();

    // endregion
}