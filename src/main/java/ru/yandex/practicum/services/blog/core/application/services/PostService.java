package ru.yandex.practicum.services.blog.core.application.services;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostResponseDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostsPageResponseDto;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostService;
import ru.yandex.practicum.services.blog.core.application.mappers.PostMapper;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTagValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <summary>
 * Основной сервис для работы с публикациями.
 * </summary>
 **/
public final class PostService implements IPostService
{
    // region Fields



    // endregion

    // region Constructors

    public PostService()
    {
    }

    // endregion

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
    @Override
    public PostsPageResponseDto getPostsPage(
            String search,
            Long pageNumber,
            Long pageSize
    )
    {
        // Получаем доменные сущности (пока моки, позже тут будет вызов репозитория).
        var posts = getTestPosts();

        // Рассчитываем метаданные пагинации.
        var lastPage = 3L;
        var hasPrev = pageNumber > 1;
        var hasNext = pageNumber < lastPage;

        // Формируем и возвращаем итоговый DTO.
        return new PostsPageResponseDto(
                posts,
                hasPrev,
                hasNext,
                lastPage
        );
    }

    /**
     * <summary>
     * Вспомогательный метод для генерации тестовых данных.
     * </summary>
     **/
    private List<PostResponseDto> getTestPosts()
    {
        var result = new ArrayList<PostResponseDto>();

        for (var postId = 0L; postId < 10L; postId++)
        {
            var tempPost = new PostEntityObject(
                    new PostTitleValueObject("Название поста " + postId),
                    new PostTextValueObject("Текст поста в формате markdown")
            );

            tempPost.addTag(new PostTagValueObject("tag" + postId));
            tempPost.changeId(postId);

            result.add(PostMapper.mapToResponseDto(tempPost));
        }

        return result;
    }

    // endregion
}