package ru.yandex.practicum.services.blog.core.application.interfaces;

import org.springframework.transaction.annotation.Transactional;
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

    /**
     * <summary>
     * Увеличивает количество лайков поста.
     * </summary>
     * <param name="postId"
     * >Идентификатор поста.
     * </param>
     * <return>
     * Новое количество лайков.
     * </return>
     */
    @Transactional
    public Long likePost(final Long postId);

    // endregion
}