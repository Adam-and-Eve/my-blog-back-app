package ru.yandex.practicum.services.blog.core.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostResponseDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostsPageResponseDto;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostService;
import ru.yandex.practicum.services.blog.core.application.mappers.PostMapper;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTagValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <summary>
 * Основной сервис для работы с публикациями.
 * </summary>
 **/
public final class PostService implements IPostService
{
    // region Fields

    /*
     * Репозиторий публикаций.
     */
    private final IPostRepository postRepository;

    // endregion

    // region Constructors

    public PostService(final IPostRepository postRepository)
    {
        this.postRepository = postRepository;
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
        /*
         * Проверяем корректность номера страницы.
         */
        if (pageNumber == null ||
            pageNumber < 1)
        {
            throw new ApplicationValidationException(
                    "Номер страницы должен быть больше или равен 1",
                    "pageNumber"
            );
        }

        /*
         * Проверяем корректность размера страницы.
         */
        if (pageSize == null ||
            pageSize < 1)
        {
            throw new ApplicationValidationException(
                    "Размер страницы должен быть больше или равен 1",
                    "pageSize"
            );
        }

        /*
         * Нормализуем строку поиска.
         */
        search = search == null ? "" : search.trim();

        /*
         * Генерируем тестовые данные (имитация базы данных).
         */
        var posts = generateAllTestPosts();

        var check = postRepository.countAllPosts(search, List.of());

        System.out.println("База ответила: " + check);

        /*
         * Разделяем строку поиска по любому количеству пробелов
         * и удаляем пустые элементы.
         */
        var searchParts = search.isEmpty()
                ? List.<String>of()
                : Arrays.stream(search.split("\\s+"))
                        .filter(s -> !s.isBlank())
                        .toList();

        /*
         * Формируем список тегов для фильтрации.
         * Теги начинаются с '#'.
         */
        var tagFilters = searchParts
                .stream()
                .filter(s -> s.startsWith("#") && s.length() > 1)
                .map(s -> s.substring(1).toLowerCase())
                .toList();


        /*
         * Формируем строки поиска по названию.
         * Все слова без '#' объединяются через пробел.
         */
        var titleQuery = searchParts
                .stream()
                .filter(s -> !s.startsWith("#"))
                .collect(Collectors.joining(" "))
                .toLowerCase();

        /*
         * Фильтруем посты:
         * - по тегам (логическое "И");
         * - по подстроке в названии (логическое "И").
         */
        var filteredPosts = posts
                .stream()
                .filter(post ->
                {
                    /*
                     * Получаем множество тегов поста.
                     */
                    var postTags = post.getTags()
                            .stream()
                            .map(PostTagValueObject::getValue)
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet());

                    /*
                     * Проверяем наличие всех тегов из запроса.
                     */
                    var matchesTags = tagFilters.isEmpty() ||
                            postTags.containsAll(tagFilters);

                    /*
                     * Проверяем вхождение строки из запроса в название поста.
                     */
                    var matchesTitle = titleQuery.isEmpty() ||
                            post.getTitle()
                                .getValue()
                                .toLowerCase()
                                .contains(titleQuery);

                    return matchesTags && matchesTitle;
                })
                .toList();

        /*
         * Вычисляем общее количество найденных постов.
         */
        var totalPosts = filteredPosts.size();

        /*
         * Вычисляем количество страниц.
         * Если результатов нет - страниц 0.
         */
        var lastPage = totalPosts == 0
                ? 0
                : (totalPosts + pageSize.intValue() - 1) / pageSize.intValue();

        /*
         * Определяем объект для хранения постов на странице.
         */
        var pageContent = new ArrayList<PostResponseDto>();

        /*
         * Формируем содержимое страницы,
         * если запрошенная страница входит в допустимый диапазон.
         */
        if (lastPage > 0 &&
            pageNumber <= lastPage)
        {
            var fromIndex = (int) ((pageNumber - 1) * pageSize);
            var toIndex = Math.min(fromIndex + pageSize.intValue(), totalPosts);

            pageContent = filteredPosts.subList(fromIndex, toIndex)
                    .stream()
                    .map(PostMapper::mapToResponseDto)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /*
         * Формируем итоговый DTO.
         */
        return new PostsPageResponseDto(
                pageContent,
                pageNumber > 1 && lastPage > 0,
                pageNumber < lastPage,
                (long) lastPage
        );
    }

    /**
     * <summary>
     * Вспомогательный метод для генерации тестовых данных.
     * </summary>
     **/
    private List<PostEntityObject> generateAllTestPosts()
    {
        var result = new ArrayList<PostEntityObject>();

        for (var postId = 1L; postId <= 100L; postId++)
        {
            var post = new PostEntityObject(
                    new PostTitleValueObject(
                            "Пост номер " +
                                   postId +
                                   (postId % 2 == 0
                                           ? " важный"
                                           : "")),
                    new PostTextValueObject(
                            (postId % 2 == 0
                                    ? "Это очень длинный текст поста, который должен " +
                                      "быть обрезан маппером, потому что в нем явно " +
                                      "больше сто двадцати восьми символов для " +
                                      "проверки корректности работы логики " +
                                      "обрезки строк..."
                                    : "Это обычный текст поста, который не " +
                                      "должен быть обрезан."))
            );

            post.addTag(
                    new PostTagValueObject(
                            postId % 2 == 0
                                    ? "java"
                                    : "spring"));

            if (postId % 3 == 0)
            {
                post.addTag(
                        new PostTagValueObject(
                                "news"));
            }

            post.changeId(postId);

            result.add(post);
        }

        return result;
    }

    // endregion
}