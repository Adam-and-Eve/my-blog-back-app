package ru.yandex.practicum.services.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
/**
 * Базовый класс для всех тестов
 * Использует полноценный контекст Spring Boot и управляет общим жизненным циклом
 * контейнера MS SQL Server, гарантируя кэширование контекста между тестами.
 **/
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MyBlogBackAppApplicationTests
{
	private static final MSSQLServerContainer<?> mssqlContainer;

	static
	{
		var imageName = "";

		try
		{
			var resource = new ClassPathResource("application-test.properties");

			var properties = PropertiesLoaderUtils.loadProperties(resource);

			imageName = properties.getProperty("app.testcontainers.mssql-image");

			if (imageName == null || imageName.isBlank())
			{
				throw new IllegalArgumentException("Свойство 'app.testcontainers.mssql-image' не задано или пустое.");
			}

			mssqlContainer = new MSSQLServerContainer<>(imageName);

			mssqlContainer.start();
		}
		catch (Exception ex)
		{
			throw new RuntimeException("Критическая ошибка при инициализации тестового контейнера базы данных.'.", ex);
		}
	}

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry)
	{
		registry.add("spring.datasource.url", mssqlContainer::getJdbcUrl);

		registry.add("spring.datasource.username", mssqlContainer::getUsername);

		registry.add("spring.datasource.password", mssqlContainer::getPassword);
	}

	@Test
	void contextLoads()
	{
	}
}