package ru.yandex.practicum.services.blog.presentation.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.*;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostImageService;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostService;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostCommentService;

import java.util.List;

/**
 * <summary>
 * REST-контроллер для управления постами блога.
 * Обрабатывает входящие HTTP-запросы, делегирует бизнес-логику слою сервисов (Application layer)
 * и формирует стандартизированные ответы для клиента.
 * </summary>
 **/
@RestController
@RequestMapping("/posts")
public final class PostController
{
    // region Fields

    /**
     * Сервис для выполнения операций над публикациями.
     **/
    private final PostService postService;

    /**
     * Сервис для выполнения операций над комментариями публикаций.
     **/
    private final PostCommentService postCommentService;

    /**
     * Сервис для выполнения операций над изображениями публикаций.
     **/
    private final PostImageService postImageService;

    // endregion

    // region Constructors

    @Autowired
    public PostController(
            final PostService postService,
            final PostCommentService postCommentService,
            final PostImageService postImageService)
    {
        this.postService = postService;
        this.postCommentService = postCommentService;
        this.postImageService = postImageService;
    }

    // endregion

    // region Actions

    /**
     * <summary>
     * Получение постраничного списка публикаций.
     * Все параметры являются обязательными согласно спецификации задания.
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
     * @return Ответ со списком постов и метаданными пагинации.
     * </return>
     **/
    @GetMapping()
    public ResponseEntity<PostsPageResponseDto> getPostsPage(
            @RequestParam("search") final String search,
            @RequestParam("pageNumber") final Long pageNumber,
            @RequestParam("pageSize") final Long pageSize
    )
    {
        // Делегируем выполнение бизнес-логики сервису слоя Application.
        var responseDto = postService.getPostsPage(search, pageNumber, pageSize);

        // Возвращаем результат клиенту.
        return ResponseEntity.ok(responseDto);
    }

    /**
     * <summary>
     * Получение объекта публикации.
     * Все параметры являются обязательными согласно спецификации задания.
     * </summary>
     * <param name="id">
     * Идентификатор публикации (обязательно).
     * </param>
     * <return>
     * @return Ответ с объектом публикации.
     * </return>
     **/
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(
            @PathVariable("id") final Long id)
    {
        var responseDto = postService.getPostById(id);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * <summary>
     * Увеличивает количество лайков публикации.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     * <return>
     * Обновленное количество лайков.
     * </return>
     */
    @PostMapping("/{id}/likes")
    public ResponseEntity<Long> likePost(
            @PathVariable("id") final Long id)
    {
        final var updatedLikes = postService.likePost(id);

        return ResponseEntity.ok(updatedLikes);
    }

    /**
     * <summary>
     * Создает новый экземпляр публикации.
     * </summary>
     * <param name="request">
     * Объект с информацей публикации.
     * </param>
     * <return>
     * Информация о публикации из базы данных сервиса.
     * </return>
     **/
    @PostMapping()
    public ResponseEntity<PostResponseDto> createPost(@RequestBody CreatePostRequestDto request)
    {
        /*
         * Считываем информацию о публикации.
         */
        var postResponseDto = postService.createPost(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDto);
    }

    /**
     * <summary>
     * Обновляет публикацию.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable("id") Long id,
            @RequestBody UpdatePostRequestDto request
    )
    {
        var response = postService.updatePostById(id, request);

        return response == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(response);
    }

    /**
     * <summary>
     * Удаляет публикацию.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") final Long id)
    {
        postService.deletePostById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * <summary>
     * Получение изображения публикации.
     * Возвращает массив байт изображения для отображения в ленте или на странице поста.
     * </summary>
     * <param name="id">
     * Уникальный идентификатор публикации.
     * </param>
     * <return>
     * @return Массив байт изображения с заголовком Content-Type: image/png.
     * </return>
     **/
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPostImage(
            @PathVariable("id") final Long id
    )
    {
        var imageContent = postImageService.getPostImageBytesByPostId(id);

        if (imageContent == null)
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
               .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(imageContent);
    }

    /**
     * <summary>
     * Обновление изображения публикации.
     * </summary>
     * <param name="id">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="image">
     * Изображение публикации.
     * </param>
     * <return>
     * @return Статус выполнения операции.
     * </return>
     **/
    @PutMapping(value = "/{id}/image")
    public ResponseEntity<Void> updatePostImage(
            @PathVariable("id") final Long id,
            @RequestParam("image") final MultipartFile image
    )
    {
        if (image == null || image.isEmpty())
        {
            return ResponseEntity.badRequest().build();
        }

        if (!MediaType.IMAGE_PNG_VALUE.equalsIgnoreCase(image.getContentType()))
        {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        try
        {
            postImageService.updatePostImage(id, image.getBytes());
        }
        catch (Exception ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * <summary>
     * Получение комментариев публикации.
     * </summary>
     * <param name="id">
     * Уникальный идентификатор публикации.
     * </param>
     * <return>
     * @return Список комментариев публикации.
     * </return>
     **/
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponseDto>> getPostComments(
            @PathVariable("id") final Long id)
    {
        if (postService.getPostById(id)  == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var comments = postCommentService.getCommentsByPostId(id);

        return ResponseEntity.ok(comments);
    }

    /**
     * <summary>
     * Добавление нового комментария к публикации.
     * </summary>
     * <param name="id">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="comment">
     * Данные для создания комментария.
     * </param>
     * @return Созданный комментарий.
     **/
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable("id") final Long id,
            @RequestBody final CreateCommentRequestDto comment)
    {
        final var response = postCommentService.createComment(id, comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * <summary>
     * Добавление нового комментария к публикации.
     * </summary>
     * <param name="id">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="comment">
     * Данные для создания комментария.
     * </param>
     * @return Созданный комментарий.
     **/
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable("postId") final Long postId,
            @PathVariable("commentId") final Long commentId,
            @RequestBody final UpdateCommentRequestDto commentDto)
    {
        if (postService.getPostById(postId)  == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (postCommentService.getCommentsByPostId(postId) == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final var response = postCommentService.updateComment(postId, commentId, commentDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * <summary>
     * Удаление комментария публикации.
     * </summary>
     * <param name="postId">
     * Уникальный идентификатор публикации.
     * </param>
     * <param name="commentId">
     * Уникальный идентификатор комментария.
     * </param>
     * @return Статус выполнения операции.
     **/
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("postId") final Long postId,
            @PathVariable("commentId") final Long commentId)
    {
        postCommentService.deleteComment(postId, commentId);

        return ResponseEntity.noContent().build();
    }

    // endregion
}