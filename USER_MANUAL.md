# Руководство пользователя банком

##  Архитектура проекта 

Вам вовсе не обязательно знать реализацию -
весь (*почти*) функционал нашего банка доступен через меню.
Но кратко расскажу про архитектуру:
вся внутренняя логика банка спрятана в пакете `inside`,
реализован синглтон `Bank` и его дочерние синглтоны `Storage`,
`Getter`, `Adder`, `Deleter` - они управляют хранением объектов,
получением внешнего доступа к ним,
добавление новых и удалением соответственно.
Объектами являются:

+ `Client` и его потомки `ClientPerson` и `ClientLegal` (пользователь банка)
+ `BankAccount` (банковский счет)
+ `Card` (банковская карта)
+ `Cashpoint` (банкомат или офис)
+ `Transaction` (транзакция, в том числе и с наличными)

Следующий слой абстракции представляет синглтон `Interactor`.
Он инкапсулируют всю логику Банка, позволяет управлять объектами через консоль.

Но конечный пользователь видит только `Menu` - обертку над классом `Interactor`

## Тестовые примеры

Для примера, вы можете зарегистрировать человека `Иванов Иван Иванович`,
с паспортными данными `1234 567890`, который родился `01.01.2002`,
пол - `Man`, номер телефона - `89101234567`, адрес - `Ярославль`.
Потом, выведите список всех пользователей, убедить, что наш товарищ добавлен.

Побробуйте создать человека с такими же паспортными данные - убедитесь в том,
что `Bank` охраняет единственность и действительность всех объектов в системе.

Далее создайте для него счет. Название - `Копилка`,
валюта - `RUB`, лимит - `1000`.
Выведите список всех счетов, убедитесь, что он добавлен.
Заметьте, что у каждого объекта написан его `id`, а счет ссылается на `id`
своего владельца.

Затем закройте счет, убедитесь, что он действительно закрыт и откройте снова.

Зарегистрируйте компанию `Рога и Копыта`, с ИНН `12345678`,
с номером `89201234567` и адресом `Ярославль`. Подмечу, что в моей модели банка
ИНН и паспорт являются секретными данными, которые позволяют делать со счетом
владельца все что угодно. Поэтому они не выводятся, их знает только создатель.
Конечно, это не соответствует действительности, так как ИНН публично известен
(для юр. лиц), но оставим данное допущение.

Зарегистрируйте счет `Сейф` для этой компании, тоже в рублях, без лимита
(можете попробовать отрицательный лимит - тогда он станет максимальным).

Далее, внесите на счет `Копилка` 150 рублей (пока что добавление банкоматов
не реализовано, но всегда по-умолчанию есть главное отделение банка, где
вы можете снимать и класть деньги)

Попробуйте снять 160 рублей, увидите что транзакция не пройдет,
тогда снимите 50 рублей.

Далее откройте новый счет в `EUR` для человека, назовите его `Брокерский`.
Внесите 50 `EUR` в банке. Закройте его с переводом средств на ваш другой счет.
Но так как переводы между разными валюта запрещены,
то вы получите свои 50 `EUR` наличными.

Теперь переведите компании `Рога и Копыта` 200 рублей (пока что есть
только переводы по номеру телефона), перевод не пройдет.
Тогда переведите 40 рублей. На `Копилке` должны остаться 60, а на `Сейфе` 40.

Поменяйте лимит с 1000 рублей на -100 (на самом он станет `Int.MAX_VALUE`)
на счете `Копилка`.

Отройте счет `Брокерский` снова, теперь в рублях, без лимита.

Теперь привяжите карту на счет `Копилка`, затем перепривяжите на счет
`Брокерский` и закройте. Отройте карту на `Копилку` и `Сейф`. Выполните перевод
40 рублей с `Сейфа` на `Копилку` теперь уже при помощи карт.
