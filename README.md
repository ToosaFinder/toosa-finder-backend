# toosa-finder-backend

Для локального запуска:

- Поставить jdk8(может и на 11 заработает, но на 15 не работает точно)
- Поставить докер
- В идею поставить Kotlin плагин(можно еще Markdown плагин чтобы читать этот документ)
- Поднять базу, т.е в корне проекта выполнить "docker-compose up -d"
- запуск приложения: gradlew server:bootRun
- юнит-тесты: gradlew server:test
- все тесты, в том числе интеграционные: gradlew server:allTest

Интеграционные тесты работают с базой постгресс. Никаких H2 не предусмотрено намеренно

# Руководство по разработке

## Дисклеймер
Нарушения нижеизложенных правил допускаются, но после обсуждения с архитектором.

## Глоссарий
ПР - пул реквест 

## Общие правила
- Цикличиские ссылки между классами запрещены. В том числе в сущностях ORM. Исключения для правильной работы бд возможны, но лучше обойтись однонаправленным связями.
- Интерфейсы с единственной реализацией нежелательны. Случаи когда потенциально появится вторая реализация будут обсуждаться отдельно.
- Разбиение на пакеты по типам бинов(Entity, Exception, Service, Controller, View, Components) запрещено. На пакеты разделять по юзкейсам.
Необходимо для минимизации горизонтальных связей между пакетами и легкости ориентирования по проекту. 
- Модификаторы доступа по умолчанию: internal для JS, package-private для Java, 
по возможности private для Kotlin(затем в приоритете internal. public только для библиотек)
- У модуля/пакета должен быть строго определённый публичный интерфейс, а всё остальное должно быть скрыто.
- В коде/именах пакетов, рабочее название(а по-хорошему никакое) не должно фигурировать

## Для бэка: 

- Архитектура слоёная Controller=>Service=>Repository. Зависимости только в направлении стрелок
    - Controller: валидация входных параметров, маппинг ДТО в сущность домена, единственный вызов метода сервиса и обратный маппинг.
    - Service: слово "Service" в названии класса мало о чем говорит, лучше придумать название соответствующее выполняемой задаче. (туду: описать механизм вынесения хитрой логики в отдельные независящие от спринга классы)
    В идеале бизнес-логика не должна зависеть от Entity-классов и должна быть описана на голом котлине)
    - Repository: Ну тут понятно всем всё должно быть

- Обработка исключительных ситуаций. Использование встроенных в котлин функциий предпочтительнее кастомных исключений: 
    - require - для проверки входных аргументов(кроме валидации HTTP-запроса. Там скорее всего всё будет на аннотациях)
    - check - для проверки ошибок программирования
    - error - для ошибок в окружении
- Все исключения обрабатываются на уровне ExceptionHandler'a. Исключения из правила возможны, обсуждается отдельно

- Все DTO лежат в библиотеке com.toosafinder.api-model, которую импортируют и клиент и сервер.
- Мутабельные поля лучше избегать, особенно в DTO(лучше val чем var). Это решает проблему недоинициализации, генерации ненужных сеттеров.
Мутабельные локальные переменные использовать можно, если это положительно влияет на читаемость/производительность.

### Spring
- Использование @Autowired на полях запрещено, только на конструкторе. И то вроде нет необходимости явно это слово писать.
- У бина не должно быть более трёх зависимостей от других бинов. Это чтобы мегаответственных классов не было.
- Каждое использование @Transactional должно сопровождать явным указанием уровня изоляции.
Если уровень изоляции отличается от RepeatableRead, то должен быть коммент почему именно так.
При вложенных транзакциях обязательно указать propagation. Это нужно чтобы разработчик точно понимал что делает.

###ORM
- auto-ddl запрещен. Пишем лапками скрипты Flyway
- Many-to-many связи нежелательны. Лучше создать вьюшку доступную только в рамках юзкейса
- Изменение существующих скриптов Flyway запрещено
Для внесения изменений в схему БД писать новый скрипт. Формат имени файла миграции: V{предудущее+1}__{Краткое описание изменений или номер тикета}.sql
- Для каждого внешнего ключа должны быть явно определены правила ON DELETE и ON UPDATE
- TODO: ограничить избыточные зависимости энтитей труг от друга, особенно ассоциации-коллекции

#Тестирование
- Все тесты явно(ТОДО: скорее всего по названию пакета) делятся на Юнит-тесты и Интеграционные.
- Юнит-тесты - не требуют спрингового контекста(тестируют голые kotlin-классы)
    - Их пишут разработчики.
    - Тестировать всё подряд не нужно. Нужно вынести логику принятия решений в отдельный класс и протестировать его.
    - Моки не нужны если логика достаточно хорошо отделена от спринга

- Интеграционные тесты для бэка - http-вызовы методов АПИ. RestAssurred или аналог.
    - Это в принципе может делать команда SQA, но если будут писать разработчики, думаю что будет эффективнее
    - Каждый метод апи и каждый возвращаемый статус должны быть покрыты тестами
    - Интеграционные тесты гоняются с СУБД с которой предполагается эксплуатация. Никаких H2

Не подходящие под описанные случаи обсуждаются отдельно

# Правила работы с системой контроля версий
- Разработка ведется в ветке dev. Она периодически сливается в мастер по согласованию с архитектором и PM
- Под каждую задачу в Redmine создается ветка “dev-{номер тикета}/{краткое описание}”
- Когда задача готова, ветка ребейзится на dev или ветку надзадачи, создается пул-руеквест в ветку dev или ветку надзадачи. Ветка задачи после мержа ПР удаляется.
- Более одного открытого ПР от одного разработчика не рекомендуется
- ПР сливается без сквоша
- TODO: Стандартизировать коммит-мессаджи
