package ru.yandex.practicum.services.blog.core.application.interfaces;

import ru.yandex.practicum.services.blog.core.application.queries.PostSearchCriteria;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;

import java.util.List;
import java.util.Optional;

/**
 * <summary>
 * Основной контракт репозитория для работы с публикациями на уровне базы данных.
 * </summary>
 **/
public interface IPostRepository
{
    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Получение постраничного списка публикаций с учетом фильтрации и пагинации на стороне БД.
     * </summary>
     * <param name="criteria">
     * Объект с критериями для поиска публикаций.
     * </param>
     * <param name="offset">
     * Количество записей, которые нужно пропустить (для пагинации).
     * </param>
     * <param name="limit">
     * Максимальное количество возвращаемых записей (размер страницы).
     * </param>
     * <return>
     * @return Отсортированный список найденных доменных сущностей публикаций.
     * </return>
     **/
    public List<PostEntityObject> findAllPosts(
            final PostSearchCriteria criteria,
            final Long offset,
            final Long limit);

    /**
     * <summary>
     * Получение публикации.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Доменная сущность публикации.
     * </return>
     **/
    public PostEntityObject findPostById(final Long id);

    /**
     * <summary>
     * Подсчет общего количества публикаций, удовлетворяющих заданным фильтрам.
     * Необходим для корректного расчета метаданных пагинации (общее количество страниц).
     * </summary>
     * <param name="criteria">
     * Объект с критериями для поиска публикаций.
     * </param>
     * <return>
     * @return Общее количество найденных публикаций.
     * </return>
     **/
    public Long countAllPosts(final PostSearchCriteria criteria);

    /**
     * <summary>
     * Увеличивает счетчик лайков публикации на единицу.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * Обновленное количество лайков или empty, если публикация не найдена.
     * </return>
     */
    public Optional<Long> incrementLikesCount(final Long postId);

    // endregion
}