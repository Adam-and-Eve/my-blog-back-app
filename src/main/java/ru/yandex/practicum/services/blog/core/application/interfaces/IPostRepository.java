package ru.yandex.practicum.services.blog.core.application.interfaces;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;

import java.util.List;

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
     * <param name="titleQuery">
     * Подстрока для поиска в заголовке публикации (может быть пустой).
     * </param>
     * <param name="tagFilters">
     * Список тегов для жесткой фильтрации (логическое "И").
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
            String titleQuery,
            List<String> tagFilters,
            Long offset,
            Long limit);

    /**
     * <summary>
     * Подсчет общего количества публикаций, удовлетворяющих заданным фильтрам.
     * Необходим для корректного расчета метаданных пагинации (общее количество страниц).
     * </summary>
     * <param name="titleQuery">
     * Подстрока для поиска в заголовке публикации (может быть пустой).
     * </param>
     * <param name="tagFilters">
     * Список тегов для жесткой фильтрации (логическое "И").
     * </param>
     * <return>
     * @return Общее количество найденных публикаций.
     * </return>
     **/
    public Long countAllPosts(
            String titleQuery,
            List<String> tagFilters
    );

    // endregion
}