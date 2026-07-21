package ru.yandex.practicum.services.blog.presentation.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CreatePostRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostResponseDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.PostsPageResponseDto;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostService;

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
    private final IPostService postService;

    // endregion

    // region Constructors

    @Autowired
    public PostController(
            IPostService postService
    )
    {
        this.postService = postService;
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
        // Делегируем выполнение бизнес-логики сервису слоя Application.
        var responseDto = postService.getPostById(id);

        // Возвращаем результат клиенту.
        return ResponseEntity.ok(responseDto);
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
     * @return Массив байт изображения с заголовком Content-Type: image/jpeg.
     * </return>
     **/
    @GetMapping("/{id}/image")
    public ResponseEntity<Byte[]> getPostImage(
            @PathVariable("id") final Long id
    )
    {
        // Тестовой байтовый массив для прозрачного пикселя (PNG).
        var mockImage = new Byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
                (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00,
                0x00, 0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
                (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(mockImage);
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

        return ResponseEntity.ok(postResponseDto);
    }

    // endregion
}