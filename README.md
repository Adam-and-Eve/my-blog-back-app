# my-blog-back-app

**Бэкенд приложения-блога**

---

## 🚀 О проекте

Бэкенд-приложение для платформы блога, разработанное на **Java 21** с использованием **Spring Boot Framework**.

Проект следует принципам **Clean Architecture** / многослойной архитектуры:
- **Presentation** → **Application** → **Domain** → **Infrastructure**

Приложение упаковывается в единый **Executable JAR**-архив и запускается во встроенном сервлет-контейнере **Tomcat**.

---

## 🛠 Технологический стек

- **Язык:** Java 21
- **Фреймворк:** Spring Boot Framework (Starter Web Starter Data, JDBC, MVC)
- **Сборка:** Maven
- **База данных:** Microsoft SQL Server
- **Деплой:** Executable JAR (Docker-контейнеризация с разделением слоёв сборки)
- **Тестирование:** JUnit 5, Spring Boot Test, Testcontainers (MS SQL Server), Mockito
- **Дополнительно:** Jackson

---

## 📂 Структура проекта

```bash
my-blog-back-app/
├── src/
│   ├── main/
│   │   ├── java/ru/yandex/practicum/services/blog/
│   │   │   ├── core/
│   │   │   │   ├── application/          # DTOs, Services, Interfaces
│   │   │   │   └── domain/               # Entity Objects, Value Objects, Exceptions
│   │   │   ├── infrastructure/persistence/
│   │   │   │   ├── repositories/
│   │   │   │   ├── mappers/
│   │   │   │   ├── projections/
│   │   │   │   └── configuration/
│   │   │   └── presentation/rest/
│   │   │       ├── controllers/
│   │   │       └── configuration/
│   │   └── resources/
│   │       ├── database/schema.sql
│   │       └── application.properties
│   └── test/java/...                     # Unit + Integration тесты
├── pom.xml
├── Dockerfile
├── application.properties.env
├── .gitignore
```

---

## ⚙️ Конфигурация и запуск

### Переменные окружения (application.properties.env)

```bash
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.url=jdbc:sqlserver://host.docker.internal:1433;databaseName=BlogDb;encrypt=true;trustServerCertificate=true;
spring.datasource.username=
spring.datasource.password=
```

### Запуск через Docker

```bash
docker build -t my-blog-back-app .
```

```bash
docker run --name my-blog-back-service -p 8080:8080 --env-file application.properties.env --rm my-blog-back-app
```

---

## 🏗 Локальная сборка и запуск проекта

**# Сборка исполняемого JAR-файла**

```bash
./mvnw clean package
```

**# Локальный запуск бэкенда (без Docker)**

```bash
java -jar target/my-blog-back-app-0.0.1-SNAPSHOT.jar
```

---

## 📋 Основные возможности API

PostController (/api/posts)

| Метод   | Эндпоинт                                      | Описание                          |
|---------|-----------------------------------------------|-----------------------------------|
| `GET`   | `/posts?search=...&pageNumber=...&pageSize=...` | Постраничный поиск постов        |
| `GET`   | `/posts/{id}`                                 | Получение поста                   |
| `POST`  | `/posts`                                      | Создание поста                    |
| `PUT`   | `/posts/{id}`                                 | Обновление поста                  |
| `DELETE`| `/posts/{id}`                                 | Удаление поста                    |
| `POST`  | `/posts/{id}/likes`                           | Поставить лайк                    |
| `GET`   | `/posts/{id}/image`                           | Получение изображения             |
| `PUT`   | `/posts/{id}/image`                           | Обновление изображения            |
| `GET`   | `/posts/{id}/comments`                        | Получение комментариев            |
| `POST`  | `/posts/{id}/comments`                        | Добавление комментария            |
| `PUT`   | `/posts/{postId}/comments/{commentId}`        | Обновление комментария            |
| `DELETE`| `/posts/{postId}/comments/{commentId}`        | Удаление комментария              |

---

## 🗄 Схема базы данных

Основные таблицы:
- `Posts`
- `PostImages`
- `Tags` + `PostTags` (многие-ко-многим)
- `PostComments`

Скрипт инициализации: `src/main/resources/database/schema.sql`

---

## 🧪 Тестирование

```bash
./mvnw test
```

- `Unit-тесты Value Objects`
- `Unit-тесты Entity Objects`
- `Unit-тесты сервисов`
- `Integration-тесты репозиториев (*IT)`
- `Integration-тесты контроллеров`

---

## 📌 Особенности архитектуры

- **Чёткое разделение по DDD (Entity Objects, Value Objects)**
- **Использование Projection для оптимизации запросов**
- **RowMapper для работы с Data JDBC**
- **Интерфейсы для всех важных компонентов**
- **Поддержка multipart-запросов для изображений**

---

## 🔧 Как внести изменения

- **Создайте новую ветку: git checkout -b feature/название**
- **Внесите изменения**
- **Запустите тесты: ./mvnw test** 
- **Соберите проект: ./mvnw clean package**
- **Создайте Pull Request**

---

## ⚠️ Ответы на популярные вопросы

- **Не подключается к БД в Docker → используйте host.docker.internal**
- **Проблемы со сборкой → выполните ./mvnw clean package**
- **SQL Server → убедитесь, что база BlogDb существует и у пользователя есть права**
