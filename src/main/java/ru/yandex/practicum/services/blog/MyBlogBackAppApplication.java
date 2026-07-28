package ru.yandex.practicum.services.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Spring Boot.
 * Запускает встроенный сервлет-контейнер и включает автоконфигурацию.
 */
@SpringBootApplication
public class MyBlogBackAppApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(MyBlogBackAppApplication.class, args);
	}

}
