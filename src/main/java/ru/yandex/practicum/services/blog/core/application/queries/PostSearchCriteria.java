package ru.yandex.practicum.services.blog.core.application.queries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <summary>
 * Объект критериев поиска публикаций уровня application-слоя.
 * Инкапсулирует параметры фильтрации, полученные из входной строки поиска,
 * и преобразует их в структурированный вид.
 * Используется для передачи параметров фильтрации в репозиторий.
 * </summary>
 **/
public final class PostSearchCriteria
{
    // region Fields

    /**
     * Строка поиска по заголовку публикации (в нижнем регистре).
     **/
    private final String titleQuery;

    /**
     * Список тегов для фильтрации (в нижнем регистре, без символа '#').
     **/
    private final List<String> tagFilters;

    // endregion

    // region Constructors

    private PostSearchCriteria(
            final String titleQuery,
            final List<String> tagFilters)
    {
        this.titleQuery = titleQuery;

        this.tagFilters = tagFilters;
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает строку поиска по заголовку.
     * </summary>
     **/
    public String getTitleQuery()
    {
        return this.titleQuery;
    }

    /**
     * <summary>
     * Возвращает список тегов для фильтрации.
     * </summary>
     **/
    public List<String> getTagFilters()
    {
        return this.tagFilters;
    }

    // endregion

    // region Methods

    public static PostSearchCriteria fromRawSearch(final String search)
    {
        /*
         * Нормализуем строку поиска.
         */
        var normalizedSearch = search == null
                ? ""
                : search.trim();

        /*
         * Проверяем наличие значения в строке поиска.
         */
        if (normalizedSearch.isEmpty())
        {
            return new PostSearchCriteria(
                    "",
                    List.of()
            );
        }

        /*
         * Разделяем строку поиска по любому количеству пробелов
         * и удаляем пустые элементы.
         */
        var parts = Arrays
                .stream(normalizedSearch.split("\\s+"))
                .filter(s -> !s.isBlank())
                .toList();

        /*
         * Формируем список тегов для фильтрации.
         * Теги начинаются с '#'.
         */
        var tags = parts
                .stream()
                .filter(s -> s.startsWith("#") && s.length() > 1)
                .map(s -> s.substring(1).toLowerCase())
                .toList();

        /*
         * Формируем строки поиска по названию.
         * Все слова без '#' объединяются через пробел.
         */
        var title = parts
                .stream()
                .filter(s -> !s.startsWith("#"))
                .collect(Collectors.joining(" "))
                .toLowerCase();

        return new  PostSearchCriteria(title, tags);
    }

    // endregion
}