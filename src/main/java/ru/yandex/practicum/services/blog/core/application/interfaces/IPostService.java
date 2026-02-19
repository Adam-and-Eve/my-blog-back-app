package ru.yandex.practicum.services.blog.core.application.interfaces;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostsPageResponseDto;

/**
 * <summary>
 * Интерфейс сервиса управления публикациями.
 * </summary>
 **/
public interface IPostService
{
    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Получение постраничного списка публикаций с учетом фильтрации.
     * </summary>
     * <param name="search">
     * Строка для поиска (обязательно).
     * </param>
     * <param name="pageNumber">
     * Номер страницы (обязательно).
     * </param>
     * <param name="pageSize">
     * Размер страницы (обязательно).
     * </param>
     * <return>
     * @return DTO со списком постов и метаданными пагинации.
     * </return>
     **/
    public PostsPageResponseDto getPostsPage(String search, Long pageNumber, Long pageSize);

    // endregion
}