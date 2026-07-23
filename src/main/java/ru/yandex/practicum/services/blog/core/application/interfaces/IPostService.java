package ru.yandex.practicum.services.blog.core.application.interfaces;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.*;

import java.util.List;

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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public Boolean deletePostById(final Long id);

    /**
     * <summary>
     * Получение изображения публикации по умолчанию.
     * </summary>
     * <return>
     * @return Изображение публикации по умолчанию.
     * </return>
     **/
    public byte[] getDefaultImage();

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
    public byte[] getPostImageBytesByPostId(Long postId);

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
    @Transactional
    public void updatePostImage(final Long postId, final MultipartFile imageContent);

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
    @Transactional
    public List<CommentResponseDto> getCommentsByPostId(final Long postId);

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
    public CommentResponseDto createComment(final Long postId, final CreateCommentRequestDto comment);

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
    @Transactional
    public CommentResponseDto updateComment(final Long postId, final Long commentId, final UpdateCommentRequestDto commentDto);

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
    @Transactional
    public void deleteComment(final Long postId, final Long commentId);

    // endregion
}