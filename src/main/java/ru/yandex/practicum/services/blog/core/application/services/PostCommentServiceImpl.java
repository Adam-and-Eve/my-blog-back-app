package ru.yandex.practicum.services.blog.core.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CommentResponseDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.CreateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.dtos.posts.UpdateCommentRequestDto;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.application.interfaces.CommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostCommentService;
import ru.yandex.practicum.services.blog.core.application.mappers.CommentMapper;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;

import java.util.List;
import java.util.Objects;

/**
 * Сервис управления комментариями к публикациям.
 **/
@Service
@Transactional(readOnly = true)
public class PostCommentServiceImpl implements PostCommentService
{
    // region Fields

    /*
     * Репозиторий публикаций.
     */
    private final PostRepository postRepository;

    /*
     * Репозиторий комментариев.
     */
    private final CommentRepository commentRepository;

    // endregion

    // region Constructors

    public PostCommentServiceImpl(
            final PostRepository postRepository,
            final CommentRepository commentRepository)
    {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    // endregion

    // region Methods

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
    @Override
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

        if (comment.getPostId() == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации в теле запроса не должен быть пустым.");
        }

        if (!Objects.equals(comment.getPostId(), postId))
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации в URL и в теле запроса не совпадают.");
        }

        var postEntity = postRepository.findPostById(postId);

        if (postEntity == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + postId + "'.");
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

        return CommentMapper.mapToResponseDto(postId, commentEntity.get());
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

        if (commentDto.getId() == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор комментария в теле запроса не должен быть пустым.");
        }

        if (commentDto.getPostId() == null)
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации в теле запроса не должен быть пустым.");
        }

        if (!Objects.equals(commentDto.getPostId(), postId))
        {
            throw new ApplicationValidationException(
                    "Идентификатор публикации в URL и в теле запроса не совпадают.");
        }

        if (!Objects.equals(commentDto.getId(), commentId))
        {
            throw new ApplicationValidationException(
                    "Идентификатор комментария в URL и в теле запроса не совпадают.");
        }

        var postEntity = postRepository.findPostById(postId);

        if (postEntity == null)
        {
            throw new EntityObjectNotFoundException(
                    "Не удалось найти публикацию с id '" + postId + "'.");
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

        return CommentMapper.mapToResponseDto(postId, commentEntity.get());
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