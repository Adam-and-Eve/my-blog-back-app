package ru.yandex.practicum.services.blog.presentation.rest.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.services.blog.core.application.exceptions.ApplicationValidationException;
import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectNotFoundException;
import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <summary>
 * Централизованная конфигурация обработки исключений для REST-контроллеров.
 * Перехватывает доменные и прикладные ошибки и сопоставляет их с HTTP-статусами.
 * </summary>
 **/
@ControllerAdvice
public class ExceptionConfiguration
{
    /**
     * <summary>
     * Обработка ошибок валидации данных и некорректных Value Object.
     * </summary>
     * <param name="ex">Перехваченное исключение валидации.</param>
     * <return>Ответ со статусом 400 Bad Request и описанием ошибки.</return>
     **/
    @ExceptionHandler({ApplicationValidationException.class, ValueObjectIsInvalidException.class})
    public ResponseEntity<Object> handleBadRequestExceptions(final RuntimeException ex)
    {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * <summary>
     * Обработка ошибок отсутствия запрашиваемых сущностей.
     * </summary>
     * <param name="ex">Перехваченное исключение отсутствия сущности.</param>
     * <return>Ответ со статусом 404 Not Found и описанием ошибки.</return>
     **/
    @ExceptionHandler(EntityObjectNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(final EntityObjectNotFoundException ex)
    {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * <summary>
     * Обработка непредвиденных системных ошибок сервера.
     * </summary>
     * <param name="ex">Перехваченное базовое исключение.</param>
     * <return>Ответ со статусом 500 Internal Server Error.</return>
     **/
    @ExceptionHandler({IllegalStateException.class, Exception.class})
    public ResponseEntity<Object> handleInternalServerError(final Exception ex)
    {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла внутренняя ошибка сервера: " + ex.getMessage());
    }

    /**
     * <summary>
     * Вспомогательный метод для сборки унифицированного формата ответа об ошибке.
     * </summary>
     * <param name="status">Целевой статус HTTP-ответа.</param>
     * <param name="message">Сообщение об ошибке для клиента.</param>
     * <return>Объект ResponseEntity с заполненным телом ошибки.</return>
     **/
    private ResponseEntity<Object> buildResponse(final HttpStatus status, final String message)
    {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}