package ru.yandex.practicum.services.blog.core.application.interfaces;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.*;

/**
 * <summary>
 * Интерфейс сервиса управления публикациями.
 * </summary>
 **/
public interface PostService
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
     * Получение объекта публикации.
     * </summary>
     * <param name="id">
     * Идентификатор публикации (обязательно).
     * </param>
     * <return>
     * @return DTO с данными поста.
     * </return>
     **/
    public PostResponseDto getPostById(Long id);

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
    public Long likePost(final Long postId);

    /**
     * <summary>
     * Создание объекта публикации.
     * </summary>
     * <param name="title">
     * Название публикации (обязательно).
     * </param>
     * <param name="text">
     * Текст публикации (обязательно).
     * </param>
     * <return>
     * @return Объект публикации.
     * </return>
     **/
    public PostResponseDto createPost(final CreatePostRequestDto request);

    /**
     * <summary>
     * Обновление публикации.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Статус обновления публикации.
     * </return>
     **/
    public PostResponseDto updatePostById(final Long id, final UpdatePostRequestDto request);

    /**
     * <summary>
     * Удаление публикации.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Статус удаления публикации.
     * </return>
     **/
    public Boolean deletePostById(final Long id);

    // endregion
}