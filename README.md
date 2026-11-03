# E-Commerce Microservices

Backend интернет-магазина на Java и Spring Boot, разделённый на три независимо запускаемых микросервиса: пользователи, каталог товаров и заказы.

## Архитектура

```text
ecom-application/
├── user/       # пользователи и адреса
├── product/    # каталог товаров
├── order/      # корзина и оформление заказов
├── pom.xml     # Maven reactor для общей сборки
└── docker-compose.yml
```

| Сервис | Порт | Ответственность | Основной API |
| --- | ---: | --- | --- |
| `user-service` | 8082 | Пользователи и адреса | `/api/users` |
| `product-service` | 8081 | Товары, остатки и поиск | `/api/products` |
| `order-service` | 8083 | Корзина и заказы | `/api/cart`, `/api/orders` |

Каждый сервис содержит собственные исходники, `pom.xml`, Spring Boot конфигурацию и отдельную H2 базу. Корневой `pom.xml` используется только как Maven-агрегатор и не объединяет сервисы обратно в монолит.

## Технологии

- Java 17
- Spring Boot 3.2.5
- Spring Web MVC
- Spring Data JPA и Hibernate
- H2 Database
- Spring Boot Actuator
- Maven Wrapper
- Docker и Docker Compose
- Lombok

## Запуск проекта

### Локальная сборка

Требуется JDK 17. Из корня репозитория выполните:

```powershell
.\mvnw.cmd clean verify
```

Запустите сервисы в отдельных терминалах:

```powershell
.\user\mvnw.cmd -f user\pom.xml spring-boot:run
.\product\mvnw.cmd -f product\pom.xml spring-boot:run
.\order\mvnw.cmd -f order\pom.xml spring-boot:run
```

### Docker Compose

```powershell
docker compose up --build
```

Остановка контейнеров:

```powershell
docker compose down
```

## REST API

### User service — `http://localhost:8082`

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| `GET` | `/api/users` | Получить всех пользователей |
| `GET` | `/api/users/{id}` | Получить пользователя по ID |
| `POST` | `/api/users` | Создать пользователя |
| `PUT` | `/api/users/{id}` | Обновить пользователя |

Пример создания пользователя:

```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Assanali",
    "lastName": "User",
    "email": "user@example.com",
    "phone": "+70000000000",
    "address": {
      "street": "Abay street",
      "city": "Almaty",
      "state": "Almaty",
      "country": "Kazakhstan",
      "zipCode": "050000"
    }
  }'
```

### Product service — `http://localhost:8081`

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| `GET` | `/api/products` | Получить активные товары |
| `GET` | `/api/products/search?keyword=...` | Найти товары |
| `POST` | `/api/products` | Создать товар |
| `PUT` | `/api/products/{id}` | Обновить товар |
| `DELETE` | `/api/products/{id}` | Деактивировать товар |

### Order service — `http://localhost:8083`

Запросы корзины и заказов используют заголовок `X-User-ID`.

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| `GET` | `/api/cart` | Получить корзину пользователя |
| `POST` | `/api/cart` | Добавить товар в корзину |
| `DELETE` | `/api/cart/items/{productId}` | Удалить товар из корзины |
| `POST` | `/api/orders` | Оформить заказ |

Пример добавления товара:

```bash
curl -X POST http://localhost:8083/api/cart \
  -H "Content-Type: application/json" \
  -H "X-User-ID: 1" \
  -d '{"productId": 1, "quantity": 2}'
```

## Мониторинг

В каждом сервисе доступны Actuator endpoints:

- `/actuator/health`
- `/actuator/info`

Например: `http://localhost:8081/actuator/health`.

## Текущий этап

Структурное разделение монолита завершено. `order-service` хранит внешние `userId` и `productId`, поэтому JPA-модели разных сервисов больше не связаны общей базой данных. Межсервисные HTTP-клиенты, API Gateway, service discovery и постоянная PostgreSQL база могут быть добавлены на следующих этапах.

