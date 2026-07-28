package ru.yandex.practicum.services.blog.presentation.rest.controllers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.*;
import ru.yandex.practicum.services.blog.infrastructure.persistence.BaseIntegrationTest;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <summary>
 * Интеграционные тесты для проверки сквозной работы PostController -> PostService -> Repository.
 * Наследуется от BaseIntegrationTest для автоматического управления транзакциями (откат после каждого теста).
 * Переопределяет контекст до RestConfiguration для поднятия полного веб-слоя приложения.
 * </summary>
 **/
@ContextConfiguration(classes = {RestConfiguration.class})
@WebAppConfiguration
public final class PostControllerIT extends BaseIntegrationTest
{
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    /**
     * <summary>
     * Тестирование получения страницы публикаций с фильтрацией.
     * </summary>
     **/
    @Test
    public void shouldGetPostsPage() throws Exception
    {
        var createDto = new CreatePostRequestDto("Интеграция", "Текст постов", List.of("java"));

        createPostInDb(createDto);

        var responseJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .param("search", "Интеграция")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        var postsNode = rootNode.get("posts");

        Assertions.assertTrue(postsNode.isArray());

        Assertions.assertEquals("Интеграция", postsNode.get(0).get("title").asString());
    }

    /**
     * <summary>
     * Тестирование успешного получения публикации по ID.
     * </summary>
     **/
    @Test
    public void shouldGetPostById() throws Exception
    {
        var createDto = new CreatePostRequestDto("Поиск по ID", "Контент", List.of("test"));

        var generatedId = createPostInDb(createDto);

        var responseJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", generatedId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals(generatedId, rootNode.get("id").asLong());
    }

    /**
     * <summary>
     * Негативный сценарий: запрос несуществующей публикации должен возвращать 404 Not Found.
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenPostNotFound() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", 999L)).andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Тестирование инкремента лайков на существующий пост.
     * </summary>
     **/
    @Test
    public void shouldLikePost() throws Exception
    {
        var createDto = new CreatePostRequestDto("Лайки", "Контент", List.of());

        var generatedId = createPostInDb(createDto);

        var responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{id}/likes", generatedId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals("1", responseContent.trim());
    }

    /**
     * <summary>
     * Негативный сценарий: лайк на несуществующий пост должен возвращать 404 Not Found.
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenLikingNonExistentPost() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{id}/likes", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Тестирование успешного создания публикации.
     * </summary>
     **/
    @Test
    public void shouldCreatePost() throws Exception
    {
        var requestDto = new CreatePostRequestDto("Новый пост", "Содержимое", List.of("блог"));

        var jsonRequest = objectMapper.writeValueAsString(requestDto);

        var responseJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals("Новый пост", rootNode.get("title").asString());
    }

    /**
     * <summary>
     * Негативный сценарий: передача невалидных данных (например, пустой заголовок) при создании поста должна возвращать 400 Bad Request.
     * </summary>
     **/
    @Test
    public void shouldReturn400WhenCreatePostWithInvalidData() throws Exception
    {
        var requestDto = new CreatePostRequestDto("", "Контент без заголовка", List.of());

        var jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * <summary>
     * Тестирование успешного обновления публикации.
     * </summary>
     **/
    @Test
    public void shouldUpdatePost() throws Exception
    {
        var createDto = new CreatePostRequestDto("Старый заголовок", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        var updateDto = new UpdatePostRequestDto("Обновленный заголовок", "Новый текст", List.of("updated"));

        var jsonRequest = objectMapper.writeValueAsString(updateDto);

        var responseJson = mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", generatedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals("Обновленный заголовок", rootNode.get("title").asString());
    }

    /**
     * <summary>
     * Негативный сценарий: попытка обновления несуществующего поста должна возвращать 404 Not Found.
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenUpdateNonExistentPost() throws Exception
    {
        var updateDto = new UpdatePostRequestDto("Заголовок", "Текст", List.of());

        var jsonRequest = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Тестирование успешного удаления публикации.
     * </summary>
     **/
    @Test
    public void shouldDeletePost() throws Exception
    {
        var createDto = new CreatePostRequestDto("Пост под удаление", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}", generatedId))
                .andExpect(status().isNoContent());
    }

    /**
     * <summary>
     * Негативный сценарий: удаление несуществующего поста должно отдавать 404 Not Found.
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenDeleteNonExistentPost() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Тестирование получения байтов изображения поста.
     * </summary>
     **/
    @Test
    public void shouldGetPostImage() throws Exception
    {
        var createDto = new CreatePostRequestDto("Пост с картинкой", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}/image", generatedId))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(MediaType.IMAGE_PNG_VALUE, result.getResponse().getContentType());
    }

    /**
     * <summary>
     * Тестирование успешного обновления изображения через Multipart PUT.
     * </summary>
     **/
    @Test
    public void shouldUpdatePostImage() throws Exception
    {
        var createDto = new CreatePostRequestDto("Пост для фото", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        var mockFile = new MockMultipartFile("image", "avatar.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/posts/{id}/image", generatedId)
                        .file(mockFile)
                        .with(request ->
                        {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    /**
     * <summary>
     * Негативный сценарий: отправка пустого файла изображения (0 байт) должна приводить к ошибке 400 Bad Request.
     * </summary>
     **/
    @Test
    public void shouldReturn400WhenImageFileIsEmpty() throws Exception
    {
        var createDto = new CreatePostRequestDto("Пост для пустого фото", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        var emptyFile = new MockMultipartFile("image", "empty.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/posts/{id}/image", generatedId)
                        .file(emptyFile)
                        .with(request ->
                        {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest());
    }

    /**
     * <summary>
     * Тестирование успешного получения комментариев публикации.
     * </summary>
     **/
    @Test
    public void shouldGetPostComments() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Текст", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Комментарий", generatedPostId);

        createCommentInDb(generatedPostId, createCommentDto);

        var responseJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}/comments", generatedPostId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertTrue(rootNode.isArray());

        Assertions.assertEquals("Комментарий", rootNode.get(0).get("text").asString());
    }

    /**
     * <summary>
     * Попытка получить комментарии для несуществующего поста возвращает 404 Not Found.
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenGetCommentsForNonExistentPost() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}/comments", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Тестирование успешного создания комментария.
     * </summary>
     **/
    @Test
    public void shouldCreateComment() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Новый коммент", generatedPostId);

        var jsonRequest = objectMapper.writeValueAsString(createCommentDto);

        var responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/posts/{id}/comments", generatedPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals("Новый коммент", rootNode.get("text").asString());
    }

    /**
     * <summary>
     * Создание комментария, когда postId в body отсутствует (null), должно приводить к 400 Bad Request.
     * </summary>
     **/
    @Test
    public void shouldReturn400WhenCreateCommentWithMissingPostIdInBody() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Коммент без postId", null);

        var jsonRequest = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{id}/comments", generatedPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * <summary>
     * Создание комментария, когда postId в URL отличается от postId в RequestBody, должно возвращать 400 Bad Request.
     * </summary>
     **/
    @Test
    public void shouldReturn400WhenCreateCommentWithMismatchedPostId() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var differentPostId = generatedPostId + 99L;

        var createCommentDto = new CreateCommentRequestDto("Коммент с левым ID", differentPostId);

        var jsonRequest = objectMapper.writeValueAsString(createCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{id}/comments", generatedPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * <summary>
     * Тестирование успешного обновления существующего комментария.
     * </summary>
     **/
    @Test
    public void shouldUpdateComment() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Старый коммент", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        var updateCommentDto = new UpdateCommentRequestDto(generatedCommentId, "Обновленный коммент", generatedPostId);

        var jsonRequest = objectMapper.writeValueAsString(updateCommentDto);

        var responseJson = mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{commentId}", generatedPostId, generatedCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals("Обновленный коммент", rootNode.get("text").asString());
    }

    /**
     * <summary>
     * Обновление комментария, когда в body отсутствуют id или postId, должно приводить к 400 Bad Request.
     * </summary>
     **/
    @Test
    public void shouldReturn400WhenUpdateCommentWithMissingIdsInBody() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Старый коммент", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        var updateCommentDto = new UpdateCommentRequestDto(null, "Апдейт без ID", null);

        var jsonRequest = objectMapper.writeValueAsString(updateCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{commentId}", generatedPostId, generatedCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * <summary>
     * Обновление комментария, когда переданный в body postId отличается от postId в URL.
     * </summary>
     **/
    @Test
    public void shouldReturn400WhenUpdateCommentWithMismatchedPostId() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Старый коммент", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        var updateCommentDto = new UpdateCommentRequestDto(generatedCommentId, "Мисматч по postId", generatedPostId + 50L);

        var jsonRequest = objectMapper.writeValueAsString(updateCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{commentId}", generatedPostId, generatedCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * <summary>
     * Обновление комментария, когда переданный в body commentId отличается от commentId в URL.
     * </summary>
     **/
    @Test
    public void shouldReturn400WhenUpdateCommentWithMismatchedCommentId() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Старый коммент", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        var updateCommentDto = new UpdateCommentRequestDto(generatedCommentId + 100L, "Мисматч по commentId", generatedPostId);

        var jsonRequest = objectMapper.writeValueAsString(updateCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{commentId}", generatedPostId, generatedCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * <summary>
     * Негативный сценарий: попытка обновить существующий комментарий, но на несуществующем postId в URL (404 Not Found).
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenUpdateCommentOnNonExistentPost() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Старый коммент", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        var updateCommentDto = new UpdateCommentRequestDto(generatedCommentId, "Текст", generatedPostId);

        var jsonRequest = objectMapper.writeValueAsString(updateCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{commentId}", 999L, generatedCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Попытка обновить несуществующий комментарий (404 Not Found).
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenUpdateNonExistentComment() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var updateCommentDto = new UpdateCommentRequestDto(999L, "Текст", generatedPostId);

        var jsonRequest = objectMapper.writeValueAsString(updateCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{commentId}", generatedPostId, 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Тестирование успешного удаления комментария.
     * </summary>
     **/
    @Test
    public void shouldDeleteComment() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("На удаление", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{postId}/comments/{commentId}", generatedPostId, generatedCommentId))
                .andExpect(status().isNoContent());
    }

    /**
     * <summary>
     * Негативный сценарий: удаление несуществующего комментария должно возвращать 404 Not Found.
     * </summary>
     **/
    @Test
    public void shouldReturn404WhenDeleteNonExistentComment() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{postId}/comments/{commentId}", 1L, 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * <summary>
     * Вспомогательный метод для сохранения публикации в БД через вызов REST-эндпоинта.
     * Использование полного имени класса MockMvcRequestBuilders предотвращает ошибки вывода типов компилятором Java.
     * </summary>
     **/
    private Long createPostInDb(CreatePostRequestDto dto) throws Exception
    {
        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    /**
     * <summary>
     * Вспомогательный метод для сохранения комментария к публикации в БД через вызов REST-эндпоинта.
     * </summary>
     **/
    private String createCommentInDb(Long postId, CreateCommentRequestDto dto) throws Exception
    {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}