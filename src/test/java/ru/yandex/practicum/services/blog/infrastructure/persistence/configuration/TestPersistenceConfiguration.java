package ru.yandex.practicum.services.blog.infrastructure.persistence.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.MSSQLServerContainer;
import ru.yandex.practicum.services.blog.core.application.interfaces.CommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.ImageRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.TagRepository;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.CommentJdbcRepositoryImpl;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.ImageJdbcRepositoryImpl;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.PostJdbcRepositoryImpl;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.TagJdbcRepositoryImpl;

import javax.sql.DataSource;

/**
 * <summary>
 * Тестовая конфигурация уровня доступа к данным с поддержкой Testcontainers.
 * Запускает изолированный контейнер Microsoft SQL Server в Docker и подменяет
 * основной DataSource для выполнения интеграционных тестов в воспроизводимом окружении.
 * </summary>
 **/
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application-test.properties")
@Profile("test")
public class TestPersistenceConfiguration
{
    private static final MSSQLServerContainer<?> mssqlContainer;

    static
    {
        var imageName = "";

        try
        {
            var resource = new ClassPathResource("application-test.properties");

            var properties = PropertiesLoaderUtils.loadProperties(resource);

            imageName = properties.getProperty("blog.datasource.mssql-image");

            if (imageName == null || imageName.isBlank())
            {
                throw new IllegalArgumentException("Свойство 'blog.datasource.mssql-image' не задано или пустое.");
            }

            mssqlContainer = new MSSQLServerContainer<>(imageName);

            mssqlContainer.start();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Критическая ошибка при инициализации тестового контейнера базы данных.'.", ex);
        }
    }

    /**
     * Создает и настраивает объект параметров подключения к БД.
     * Все значения внедряются исключительно из application-test.properties.
     */
    @Bean
    public PersistenceOptions persistenceOptions(
            @Value("${blog.datasource.driver-class-name}") final String driverClassName,
            @Value("${blog.datasource.url}") final String url,
            @Value("${blog.datasource.username}") final String username,
            @Value("${blog.datasource.password}") final String password
    )
    {
        return new PersistenceOptions(
                driverClassName,
                url,
                username,
                password
        );
    }

    /**
     * Создает и настраивает источник данных (DataSource).
     * @param options конфигурация тестовой базы данных.
     * @return настроенный экземпляр {@link DataSource} для подключения к тестовой БД
     */
    @Bean
    public DataSource dataSource(final PersistenceOptions options)
    {
        var dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(options.getDriverClassName());

        dataSource.setUrl(mssqlContainer.getJdbcUrl());

        dataSource.setUsername(mssqlContainer.getUsername());

        dataSource.setPassword(mssqlContainer.getPassword());

        return dataSource;
    }

    /**
     * Инициализирует базу данных, автоматически выполняя SQL-скрипты при старте.
     * Путь к файлу схемы загружается из свойств приложения. При возникновении ошибок
     * во время выполнения скрипта инициализация прерывается.
     * @param dataSource источник данных для применения скриптов
     * @param schemaPath путь к SQL-скрипту схемы базы данных
     * @return настроенный экземпляр {@link DataSourceInitializer}
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(
            final DataSource dataSource,
            @Value("${blog.datasource.schema-path}") final String schemaPath
    )
    {
        var populator = new ResourceDatabasePopulator(new ClassPathResource(schemaPath));

        populator.setContinueOnError(false);

        var initializer = new DataSourceInitializer();

        initializer.setDataSource(dataSource);

        initializer.setDatabasePopulator(populator);

        return initializer;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(final DataSource dataSource)
    {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(final DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public PostRepository postRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new PostJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }

    @Bean
    public TagRepository tagRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new TagJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }

    @Bean
    public ImageRepository imageRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new ImageJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }

    @Bean
    public CommentRepository commentRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new CommentJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }
}