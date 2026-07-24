package ru.yandex.practicum.services.blog.presentation.rest.controllers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import ru.yandex.practicum.services.blog.core.application.dtos.posts.*;
import ru.yandex.practicum.services.blog.infrastructure.persistence.BaseIntegrationTest;
import ru.yandex.practicum.services.blog.presentation.rest.configuration.RestConfiguration;

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
     * Тестирование получения страницы публикаций с фильтрацией и обязательными параметрами пагинации.
     * </summary>
     **/
    @Test
    public void shouldGetPostsPage() throws Exception
    {
        var createDto = new CreatePostRequestDto(
                "Интеграция",
                "Текст постов",
                List.of("java", "spring"));

        createPostInDb(createDto);

        var responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/posts")
                        .param("search", "Интеграция")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        var postsNode = rootNode.get("posts");

        Assertions.assertTrue(postsNode.isArray(), "Ожидался массив публикаций");

        Assertions.assertEquals("Интеграция", postsNode.get(0).get("title").asString());
    }

    /**
     * <summary>
     * Тестирование успешного получения конкретной публикации по её уникальному идентификатору.
     * </summary>
     **/
    @Test
    public void shouldGetPostById() throws Exception
    {
        var createDto = new CreatePostRequestDto("Поиск по ID", "Контент", List.of("test"));

        var generatedId = createPostInDb(createDto);

        var responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/posts/{id}", generatedId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals(generatedId, rootNode.get("id").asLong());

        Assertions.assertEquals("Поиск по ID", rootNode.get("title").asString());
    }

    /**
     * <summary>
     * 3. Тестирование добавления лайка к публикации и возврата обновлённого счётчика.
     * </summary>
     **/
    @Test
    public void shouldLikePost() throws Exception
    {
        var createDto = new CreatePostRequestDto("Лайкаемый пост", "Контент", List.of());

        var generatedId = createPostInDb(createDto);

        var responseContent = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/posts/{id}/likes", generatedId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals("1", responseContent.trim());
    }

    /**
     * <summary>
     * Тестирование создания новой публикации через передачу валидного объекта CreatePostRequestDto.
     * </summary>
     **/
    @Test
    public void shouldCreatePost() throws Exception
    {
        var requestDto = new CreatePostRequestDto("Новый пост", "Содержимое Markdown", List.of("блог"));

        var jsonRequest = objectMapper.writeValueAsString(requestDto);

        var responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertNotNull(rootNode.get("id"), "Идентификатор публикации не должен быть null");

        Assertions.assertEquals("Новый пост", rootNode.get("title").asString());
    }

    /**
     * <summary>
     * Тестирование обновления данных существующей публикации через метод PUT.
     * </summary>
     **/
    @Test
    public void shouldUpdatePost() throws Exception
    {
        var createDto = new CreatePostRequestDto("Старый заголовок", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        var updateDto = new UpdatePostRequestDto("Обновленный заголовок", "Новый текст", List.of("updated"));

        var jsonRequest = objectMapper.writeValueAsString(updateDto);

        var responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/posts/{id}", generatedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals("Обновленный заголовок", rootNode.get("title").asString());

        Assertions.assertEquals("Новый текст", rootNode.get("text").asString());
    }

    /**
     * <summary>
     * Тестирование успешного удаления публикации по её идентификатору.
     * </summary>
     **/
    @Test
    public void shouldDeletePost() throws Exception
    {
        var createDto = new CreatePostRequestDto("Пост под удаление", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/posts/{id}", generatedId))
                .andExpect(status().isNoContent());
    }

    /**
     * <summary>
     * Тестирование получения бинарного содержимого изображения публикации с проверкой заголовков.
     * </summary>
     **/
    @Test
    public void shouldGetPostImage() throws Exception
    {
        var createDto = new CreatePostRequestDto("Пост с картинкой", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        var result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/posts/{id}/image", generatedId))
                .andExpect(status().isOk())
                .andReturn();

        var response = result.getResponse();

        Assertions.assertEquals(MediaType.IMAGE_PNG_VALUE, response.getContentType());

        Assertions.assertEquals("no-store", response.getHeader(HttpHeaders.CACHE_CONTROL));

        Assertions.assertNotNull(response.getContentAsByteArray(), "Массив байт изображения не должен быть null");
    }

    /**
     * <summary>
     * Тестирование успешного обновления изображения публикации через Multipart-запрос (PUT).
     * </summary>
     **/
    @Test
    public void shouldUpdatePostImage() throws Exception
    {
        var createDto = new CreatePostRequestDto("Пост для загрузки фото", "Текст", List.of());

        var generatedId = createPostInDb(createDto);

        var mockFile = new MockMultipartFile(
                "image",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{1, 2, 3, 4}
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/posts/{id}/image", generatedId)
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    /**
     * <summary>
     * Тестирование получения списка комментариев, привязанных к конкретной публикации, с валидацией CommentResponseDto.
     * </summary>
     **/
    @Test
    public void shouldGetPostComments() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост для комментов", "Текст", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Тестовый комментарий", generatedPostId);

        createCommentInDb(generatedPostId, createCommentDto);

        String responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/posts/{id}/comments", generatedPostId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertTrue(rootNode.isArray(), "Ожидался массив комментариев");

        Assertions.assertFalse(rootNode.isEmpty(), "Массив комментариев не должен быть пустым");

        var firstCommentNode = rootNode.get(0);

        Assertions.assertNotNull(firstCommentNode.get("id"), "Идентификатор комментария не должен быть null");

        Assertions.assertEquals("Тестовый комментарий", firstCommentNode.get("text").asString());

        Assertions.assertEquals(generatedPostId, firstCommentNode.get("postId").asLong());
    }

    /**
     * <summary>
     * Тестирование успешного создания нового комментария к публикации.
     * </summary>
     **/
    @Test
    public void shouldCreateComment() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост для нового коммента", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Свежий комментарий", generatedPostId);

        var jsonRequest = objectMapper.writeValueAsString(createCommentDto);

        var responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/posts/{id}/comments", generatedPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertNotNull(rootNode.get("id"), "Идентификатор созданного комментария не должен быть null");

        Assertions.assertEquals("Свежий комментарий", rootNode.get("text").asString());

        Assertions.assertEquals(generatedPostId, rootNode.get("postId").asLong());
    }

    /**
     * <summary>
     * Тестирование успешного изменения существующего комментария публикации.
     * </summary>
     **/
    @Test
    public void shouldUpdateComment() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост для апдейта коммента", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Старый комментарий", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        var updateCommentDto = new UpdateCommentRequestDto(generatedCommentId, "Обновленный текст комментария", generatedPostId);

        var jsonRequest = objectMapper.writeValueAsString(updateCommentDto);

        var responseJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{commentId}", generatedPostId, generatedCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var rootNode = objectMapper.readTree(responseJson);

        Assertions.assertEquals(generatedCommentId, rootNode.get("id").asLong());

        Assertions.assertEquals("Обновленный текст комментария", rootNode.get("text").asString());

        Assertions.assertEquals(generatedPostId, rootNode.get("postId").asLong());
    }

    /**
     * <summary>
     * Тестирование успешного удаления комментария публикации.
     * </summary>
     **/
    @Test
    public void shouldDeleteComment() throws Exception
    {
        var createPostDto = new CreatePostRequestDto("Пост для удаления коммента", "Контент", List.of());

        var generatedPostId = createPostInDb(createPostDto);

        var createCommentDto = new CreateCommentRequestDto("Комментарий на удаление", generatedPostId);

        var initialCommentJson = createCommentInDb(generatedPostId, createCommentDto);

        var generatedCommentId = objectMapper.readTree(initialCommentJson).get("id").asLong();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/posts/{postId}/comments/{commentId}", generatedPostId, generatedCommentId))
                .andExpect(status().isOk());
    }

    /**
     * <summary>
     * Вспомогательный метод для сохранения публикации в БД через вызов REST-эндпоинта.
     * Использование полного имени класса MockMvcRequestBuilders предотвращает ошибки вывода типов компилятором Java.
     * </summary>
     **/
    private Long createPostInDb(CreatePostRequestDto dto) throws Exception
    {
        String response = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/posts")
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
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}