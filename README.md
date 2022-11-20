# _REST API для проекта "Explore With Me"_

### _Язык реализации Java_

Pull request: : &nbsp; https://github.com/alex-pergushev/java-explore-with-me/pull/1   </h3>

### **Инструкция по развертыванию проекта:**

1. [Скачать данный репозиторий](https://github.com/alex-pergushev/java-explore-with-me)
2. mvn clean package
3. docker-compose up
4. основной сервис доступен по адресу: http://localhost:8080
5. сервис статистики доступен по адресу: http://stats-server:9090  

Фича: Добавление комментариев к событиям.  

Эндпоинты  

Зарегистрированные пользователи: 
```
Post  /users/{userId}/comments              Добавить коммент
GET   /users/{userId}/comments              Получить все комменты пользователя
Patch /users/{userId}/comments/{commentId}  Изменить коммент (доступно автору)
GET   /users/{userId}/comments/{commentId}  Получить коммент пользователя по его id
````

Администраторы:
````
Get   /admin/comments             Получить все коменты пользователей
Patch /admin/comments/{commentId} Изменить коммент по его id
````