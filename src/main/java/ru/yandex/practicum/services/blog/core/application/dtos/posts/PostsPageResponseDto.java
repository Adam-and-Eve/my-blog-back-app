package ru.yandex.practicum.services.blog.core.application.dtos.posts;

import java.util.List;

/**
 * <summary>
 * Объект передачи данных (DTO) для постраничного вывода публикаций.
 * Включает в себя список постов текущей страницы и метаданные для
 * управления пагинацией на стороне клиента (фронтенда).
 * </summary>
 **/
public final class PostsPageResponseDto
{
    // region Fields

    /**
     * <summary>
     * Список публикаций для текущей страницы.
     * </summary>
     **/
    private final List<PostResponseDto> posts;

    /**
     * <summary>
     * Флаг, указывающий на наличие предыдущей страницы.
     * </summary>
     **/
    private final Boolean hasPrev;

    /**
     * <summary>
     * Флаг, указывающий на наличие следующей страницы.
     * </summary>
     **/
    private final Boolean hasNext;

    /**
     * <summary>
     * Номер последней доступной страницы (общее количество страниц).
     * </summary>
     **/
    private final Long lastPage;

    // endregion

    // region Constructors

    public PostsPageResponseDto(
            List<PostResponseDto> posts,
            Boolean hasPrev,
            Boolean hasNext,
            Long lastPage
    )
    {
        this.posts = posts;

        this.hasPrev = hasPrev;

        this.hasNext = hasNext;

        this.lastPage = lastPage;
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает список публикаций.
     * </summary>
     * <return>
     * @return Список объектов PostResponseDto.
     * </return>
     **/
    public final List<PostResponseDto> getPosts()
    {
        return posts;
    }

    /**
     * <summary>
     * Возвращает признак наличия предыдущей страницы.
     * </summary>
     * <return>
     * @return Значение true, если предыдущая страница существует, иначе false.
     * </return>
     **/
    public final Boolean getHasPrev()
    {
        return hasPrev;
    }

    /**
     * <summary>
     * Возвращает признак наличия следующей страницы.
     * </summary>
     * <return>
     * @return Значение true, если следующая страница существует, иначе false.
     * </return>
     **/
    public final Boolean getHasNext()
    {
        return hasNext;
    }

    /**
     * <summary>
     * Возвращает номер последней страницы (общее количество).
     * </summary>
     * <return>
     * @return Номер последней страницы.
     * </return>
     **/
    public final Long getLastPage()
    {
        return lastPage;
    }

    // endregion
}