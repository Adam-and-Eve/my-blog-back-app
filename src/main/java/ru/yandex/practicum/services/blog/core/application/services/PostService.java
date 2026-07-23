package ru.yandex.practicum.services.blog.core.application.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.*;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.*;
import ru.yandex.practicum.services.blog.core.application.mappers.CommentMapper;
import ru.yandex.practicum.services.blog.core.application.mappers.PostMapper;
import ru.yandex.practicum.services.blog.core.application.queries.PostSearchCriteria;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    /*
     * Репозиторий тегов.
     */
    private final ITagRepository tagRepository;

    /*
     * Репозиторий изображений.
     */
    private final IImageRepository imageRepository;

    /*
     * Репозиторий комментариев.
     */
    private final ICommentRepository commentRepository;

    // endregion

    // region Constructors

    public PostService(
        final IPostRepository postRepository,
        final ITagRepository tagRepository,
        final IImageRepository imageRepository,
        final ICommentRepository commentRepository)
    {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
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
    @Transactional
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

            tagRepository.enrichPostsWithTags(posts);

            commentRepository.enrichPostsWithComments(posts);

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
    @Transactional
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

        tagRepository.enrichPostsWithTags(List.of(post));

        var comments = commentRepository.findCommentsByPostId(id);

        post.initializeComments(comments);

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
    @Override
    @Transactional
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

        if (request.getTags() != null &&
            !request.getTags().isEmpty())
        {
            var tagIds = tagRepository.getOrCreateTags(request.getTags());

            tagRepository.updatePostTags(postEntity.getId(), tagIds);

            tagRepository.enrichPostsWithTags(List.of(postEntity));
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
    @Override
    @Transactional
    public PostResponseDto updatePostById(final Long id, final UpdatePostRequestDto request)
    {
        if (id == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не должен быть пустым");
        }

        if (request == null)
        {
            throw new ApplicationValidationException(
                    "Объект публикации не должен быть пустым");
        }

        var tempPostEntity = postRepository.findPostById(id);

        if (tempPostEntity == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + id + "' в базе данных сервиса"
            );
        }

        tempPostEntity.changeTitle(new PostTitleValueObject(request.getTitle()));

        tempPostEntity.changeText(new PostTextValueObject(request.getText()));

        var operationStatus = postRepository.updatePostById(tempPostEntity);

        if (!operationStatus)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось обновить данные публикации с id '" + id + "' в базе данных сервиса");
        }

        var updatedPostEntity = postRepository.findPostById(id);

        if (updatedPostEntity == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + id + "' в базе данных сервиса"
            );
        }

        if (request.getTags() != null &&
            !request.getTags().isEmpty())
        {
            var tagIds = tagRepository.getOrCreateTags(request.getTags());

            tagRepository.updatePostTags(updatedPostEntity.getId(), tagIds);
        }

        tagRepository.clearOrphanedTags();

        return new PostResponseDto(
                updatedPostEntity.getId(),
                updatedPostEntity.getTitle().getValue(),
                updatedPostEntity.getText().getValue(),
                updatedPostEntity.getTags().stream().map(tag -> tag.getName().getValue()).toList(),
                updatedPostEntity.getLikesCount(),
                updatedPostEntity.getCommentsCount()
        );
    }

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
    @Override
    @Transactional
    public Boolean deletePostById(final Long id)
    {
        if (id == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не может быть пустым"
            );
        }

        var operationStatus = postRepository.deletePostById(id);

        if (!operationStatus)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось удалить данные публикации с id '" + id + "' из бызы данных сервиса"
            );
        }

        tagRepository.clearOrphanedTags();

        return true;
    }

    /**
     * <summary>
     * Получение изображения публикации по умолчанию.
     * </summary>
     * <return>
     * @return Изображение публикации по умолчанию.
     * </return>
     **/
    public byte[] getDefaultImage()
    {
        var resource = new ClassPathResource("static/images/default-post.png");

        try (var inputStream = resource.getInputStream())
        {
            return inputStream.readAllBytes();
        }
        catch (IOException ex)
        {
            throw new IllegalStateException(
                    "Не удалось прочитать default-post.png",
                    ex);
        }
    }

    /**
     * <summary>
     * Поиск изображения публикации в базе данных сервиса.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Содержимое изображения публикации.
     * </param>
     **/
    @Override
    @Transactional
    public byte[] getPostImageBytesByPostId(Long postId)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не должен быть пустым");
        }

        if (postRepository.findPostById(postId) == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + postId + "'.");
        }

        var imageContent = imageRepository.findPostImageBytesByPostId(postId);

        if (imageContent == null)
        {
            return getDefaultImage();
        }

        return imageContent;
    }

    /**
     * <summary>
     * Сохранение и изменение изображения публикации в базе данных сервиса.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <param name="imageContent">
     * Содержимое изображения публикации.
     * </param>
     **/
    @Override
    @Transactional
    public void updatePostImage(final Long postId, final MultipartFile imageContent)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не должен быть пустым.");
        }

        if (imageContent == null || imageContent.isEmpty())
        {
            throw new ApplicationValidationException(
                    "Изображение не может быть пустым.");
        }

        if (postRepository.findPostById(postId) == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + postId + "'.");
        }

        try
        {
            imageRepository.saveOrUpdatePostImage(postId, imageContent.getBytes());
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Не удалось прочитать изображение", ex);
        }
    }

    /**
     * <summary>
     * Получение комментариев по идентификатору публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Комментарии публикации.
     * </return>
     **/
    @Override
    @Transactional
    public List<CommentResponseDto> getCommentsByPostId(final Long postId)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор поста не может быть пустым.");
        }

        return commentRepository.findCommentsByPostId(postId)
                .stream()
                .map(comment -> CommentMapper.mapToResponseDto(postId, comment))
                .toList();
    }

    /**
     * <summary>
     * Добавление комментария к публикации.
     * </summary>
     * <param name="postId">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="comment">
     * Новый комментарий к публикации.
     * </param>
     * <return>
     * @return Комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    @Transactional
    public CommentResponseDto createComment(final Long postId, final CreateCommentRequestDto comment)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не должен быть пустым.");
        }

        if (comment == null)
        {
            throw new ApplicationValidationException(
                    "Информация о комментарии не должна отсутствовать.");
        }

        var postEntity = postRepository.findPostById(comment.getPostId());

        if (postEntity == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + comment.getPostId() + "'.");
        }

        var commentEntity = commentRepository.saveComment(
                new CommentEntityObject(
                        new CommentTextValueObject(
                                comment.getText()
                        )
                ),
                postEntity.getId()
        );

        if (commentEntity.isEmpty())
        {
            throw new IllegalStateException(
                    "Не удалось сохранить комментарий в базе данных сервиса");
        }

        return CommentMapper.mapToResponseDto(comment.getPostId(), commentEntity.get());
    }

    /**
     * <summary>
     * Обновления комментария публикации.
     * </summary>
     * <param name="postId">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="comment">
     * Обновленный комментарий к публикации.
     * </param>
     * <return>
     * @return Обновленный комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    @Override
    @Transactional
    public CommentResponseDto updateComment(final Long postId, final Long commentId, final UpdateCommentRequestDto commentDto)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации не должен быть пустым.");
        }

        if (commentId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор комментария не должен быть пустым.");
        }

        if (commentDto == null)
        {
            throw new ApplicationValidationException(
                    "Информация о комментарии не должна отсутствовать.");
        }

        var postEntity = postRepository.findPostById(commentDto.getPostId());

        if (postEntity == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + commentDto.getPostId() + "'.");
        }

        commentRepository.enrichPostsWithComments(List.of(postEntity));

        var commentExists = postEntity.getComments().stream()
                .filter(comment ->
                        Objects.equals(comment.getId(), commentDto.getId()))
                .findFirst();

        if (commentExists.isEmpty())
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти комментарий с id '" + commentDto.getId() + "'.");
        }

        var commentExtracted = commentExists.get();

        commentExtracted.changeText(
                new CommentTextValueObject(
                        commentDto.getText()));

        var commentEntity = commentRepository.updateComment(
                commentExtracted,
                postEntity.getId()
        );

        if (commentEntity.isEmpty())
        {
            throw new IllegalStateException(
                    "Не удалось обновить комментарий в базе данных сервиса");
        }

        return CommentMapper.mapToResponseDto(commentDto.getPostId(), commentEntity.get());
    }

    /**
     * <summary>
     * Удаления комментария публикации.
     * </summary>
     * <param name="postId">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="commentId">
     * Уникальный идентификатор комментария.
     * </param>
     **/
    @Override
    @Transactional
    public void deleteComment(final Long postId, final Long commentId)
    {
        if (postId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор поста не должен быть пустым."
            );
        }

        if (commentId == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор комментария не должен быть пустым."
            );
        }

        var postExists = postRepository.findPostById(postId);

        if (postExists == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + postId + "'.");
        }

        commentRepository.enrichPostsWithComments(List.of(postExists));

        var commentExists = postExists.getComments().stream()
                .anyMatch(comment -> Objects.equals(comment.getId(), commentId));

        if (!commentExists)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти комментарий с id '" + commentId + "'.");
        }

        var deleted = commentRepository.deleteComment(commentId, postId);

        if (!deleted)
        {
            throw new IllegalStateException(
                    "Не удалось удалить комментарий с id '" + commentId + "' из базы данных сервиса.");
        }
    }

    // endregion
}