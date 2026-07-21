package ru.yandex.practicum.services.blog.core.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CreatePostRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostResponseDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostsPageResponseDto;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostService;
import ru.yandex.practicum.services.blog.core.application.mappers.PostMapper;
import ru.yandex.practicum.services.blog.core.application.queries.PostSearchCriteria;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * <summary>
 * Основной сервис для работы с публикациями.
 * </summary>
 **/
public class PostService implements IPostService
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
         * Считываем критерии для поиска постов.
         */
        var searchCriteria = PostSearchCriteria.fromRawSearch(search);

        /*
         * Вычисляем общее количество найденных постов.
         */
        var totalPosts = postRepository.countAllPosts(searchCriteria);

        /*
         * Вычисляем количество страниц.
         * Если результатов нет - страниц 0.
         */
        var lastPage = totalPosts == 0
                ? 0
                : (totalPosts + pageSize - 1L) / pageSize;

        /*
         * Определяем объект для хранения постов на странице.
         */
        var pageContent = new ArrayList<PostResponseDto>();

        /*
         * Считываем нужную страницу данных из репозитория,
         * только если запрошенная страница существует.
         */
        if (lastPage > 0L &&
            pageNumber <= lastPage)
        {
            /*
             * Вычисляем смещение (offset) для базы данных.
             */
            var offset = (pageNumber - 1) * pageSize;

            /*
             * Запрашиваем ровно одну страницу из репозитория.
             */
            var posts = postRepository.findAllPosts(
                    searchCriteria,
                    offset,
                    pageSize);

            /*
             * Преобразуем сущности в DTO для ответа.
             */
            pageContent = posts.stream()
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
                lastPage
        );
    }

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
    @Override
    public PostResponseDto getPostById(final Long id)
    {
        /*
         * Проверяем корректность идентификатора страницы.
         */
        if (id == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор страницы не должен быть равен null",
                    "id"
            );
        }

        /*
         * Выполняем поиск публикации в репозитории.
         */
        var post = postRepository.findPostById(id);

        return PostMapper.mapToResponseDto(post);
    }

    /**
     * <summary>
     * Увеличивает количество лайков поста.
     * </summary>
     * <param name="postId">
     * Идентификатор поста.
     * </param>
     * <return>
     * Новое количество лайков.
     * </return>
     */
    @Override
    @Transactional
    public Long likePost(final Long postId)
    {
        return postRepository
                .incrementLikesCount(postId)
                .orElseThrow(() ->
                        new EntityObjectNotFoundException(
                                "Публикация с ID " + postId + " не найдена.",
                                "postId"
                        ));
    }

    @Override
    public PostResponseDto createPost(final CreatePostRequestDto request)
    {
        if (request == null)
        {
            throw new ApplicationValidationException(
                    "Объект публикации не должен быть пустым");
        }

        var tempPostEntity = new PostEntityObject(
                new PostTitleValueObject(request.getTitle()),
                new PostTextValueObject(request.getText())
        );

        var postId = postRepository.createPost(tempPostEntity);

        if (postId.isEmpty())
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось создать объект публикации в базе данных сервиса.");
        }

        var postEntity = postRepository.findPostById(postId.get());

        if (postEntity == null)
        {
            throw new EntityObjectNotFoundException(
                    "Созданный объект публикации не найден.");
        }

        return new PostResponseDto(
                postEntity.getId(),
                postEntity.getTitle().getValue(),
                postEntity.getText().getValue(),
                postEntity.getTags().stream().map(tag -> tag.getName().getValue()).toList(),
                postEntity.getLikesCount(),
                postEntity.getCommentsCount()
        );
    }

    // endregion
}