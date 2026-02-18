package ru.yandex.practicum.services.blog.presentation.rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



    // endregion

    // region Constructors

    public PostController()
    {

    }

    // endregion

    // region Actions

    /**
     * <summary>
     * Тестовый эндпоинт для проверки работоспособности контроллера и корректности роутинга.
     * </summary>
     * @return HTTP-ответ 200 (OK) с простым текстовым сообщением.
     **/
    @GetMapping("/test-message")
    public ResponseEntity<String> getTestMessage()
    {
        return ResponseEntity.ok("Test message");
    }

    // endregion
}