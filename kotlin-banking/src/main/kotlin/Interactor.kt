
import java.math.BigDecimal
import java.util.*

object Interactor {
    fun registerPersonalClient() {
        try {
            println("Введите ваше ФИО:")
            val (surname, name, patronymic) = scanner.nextLine().split(" ", limit = 3)
            println("Введите ваш паспорт:")
            val passport = scanner.nextLine()
            println("Введите дату рождения в формате ДД.ММ.ГГГГ:")
            val date = scanner.nextLine().split(".", limit = 3)
            val calendar = GregorianCalendar(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
            println("Введите ваш пол: Man, Woman или NonBinary")
            val sex = SexEnum.valueOf(scanner.nextLine())
            println("Введите ваш номер телефона:")
            val number = scanner.nextLine()
            println("Введите адрес проживания:")
            val address = scanner.nextLine()
            Bank.addClientPerson(surname, name, patronymic, passport, calendar, sex, number, address)
        }
        catch (e: Exception) {
            println("Не удалось зарегистрировать нового пользователя")
        }
    }
    fun registerLegalClient() {
        try {
            println("Введите названия вашей компании:")
            val name = scanner.nextLine()
            println("Введите ваш ИНН:")
            val tin = scanner.nextLine()
            println("Введите дату основания в формате ДД.ММ.ГГГГ:")
            val date = scanner.nextLine().split(".", limit = 3)
            val calendar = GregorianCalendar(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
            println("Введите номер телефона компании:")
            val number = scanner.nextLine()
            println("Введите юридический адрес:")
            val address = scanner.nextLine()
            Bank.addClientLegal(name, tin, calendar, number, address)
        }
        catch (e: Exception) {
            println("Не удалось зарегистрировать компанию")
        }
    }
    fun openAccount() {
        try {
            println("Введите название для вашего счета")
            val name = readln()
            println("Вы компания или человек? (C/P)")
            val ans = readln()
            var id = 0
            if (ans.lowercase() == "c") {
                println("Введите ваш ИНН")
                id = Bank.getClientByTIN(readln())!!.id
            }
            else if (ans.lowercase() == "p") {
                println("Введите ваш паспорт")
                id = Bank.getClientByPassport(readln())!!.id
            }
            println("Введите валюту счета (USD, EUR, RUB)")
            val currency = Currency.valueOf(readln())
            println("Вы хотите установить лимит на снятие денег и переводы (единый лимит) (Y/N)")
            var limit: BigDecimal? = null
            if (readln().lowercase() == "y") {
                println("Введите лимит")
                limit = scanner.nextLine().toBigDecimal()
            }
            Bank.addBankAccount(name, id, currency, limit)
        }
        catch (e: Exception) {
            println("Не удалось открыть новый счет")
        }
    }
    fun closeAccount() {
        try {
            println("Введите название счета, которой вы хотите закрыть")
            val name = readln()
            println("Подтвердите, что вы являетесь владельцем - введите ваш паспорт или ИНН")
            var ans = readln()
            val id = Bank.getClientByTIN(ans)?.id ?: Bank.getClientByPassport(ans)!!.id
            val account = Bank.getAccountByNameAndOwnerId(name, id)!!
            val date = GregorianCalendar()
            println("Вы хотите перевести деньги на другой ваш счет? (Y/N)")
            ans = readln()
            if (ans.lowercase() == "y") {
                val anotherAccount = Bank.allAccounts.find { it.name != name && it.ownerId == id && it.currency == account.currency}
                if (anotherAccount != null) {
                    println("Сейчас переведем средства (${account.amount} ${account.currency}) на ваш счет ${anotherAccount.name}")
                    Bank.addTransaction(account.id, anotherAccount.id, account.amount)
                    val trans = Bank.allTransactions.last()
                    if (trans.status == Status.Completed) {
                        println("Деньги были успешно переведены!")
                        Bank.deleteCard(account.name, id) // Удаляем привязанную к счету карту
                        Bank.allAccounts.removeIf { it.name == name && it.id == id }
                        println("Ваш счет был успешно удален! Удачного вам дня!")
                        return
                    }
                }
            }
            if (ans.lowercase() == "y") println("Мы не нашли другой счет у вас, выведем всю сумму ${account.amount} наличными")
            println("Введите номер отделения или банкомата, в котором вам удобно забрать наличные (или 0, если хотите забрать в главном отделении банка)")
            var cash = readln().toInt()
            if (Bank.getCashpointById(cash) == null) cash = Bank.allCashpoints[0].id
            Bank.addCashTransaction(account.id, cash, account.amount)
            val trans = Bank.allTransactions.last()
            if (trans.status == Status.Completed) {
                println("Деньги (${trans.amount} ${account.currency}) вам выданы, сейчас закроем счет!")
                Bank.deleteCard(account.name, id) // Удаляем привязанную к счету карту
                Bank.allAccounts.removeIf { it.name == name && it.ownerId == id }
                println("Ваш счет был успешно удален! Удачного вам дня!")
            }
        }
        catch (e: Exception) {
            println("Не получилось закрыть счет")
        }
    }
    fun setLimit() {
        try {
            println("Вы хотите поставить лимит на карту или счет? (C/A)")
            val isCard = readln().lowercase() == "c"
            println("Введите новый лимит (положительное вещественное число)")
            val limit = readln().toBigDecimal()
            println("Введите название вашего счета")
            val name = readln()
            println("Введите ваш ИНН или паспорт")
            val res = readln()
            val id = Bank.getClientByTIN(res)?.id ?: Bank.getClientByPassport(res)!!.id
            val account = Bank.getAccountByNameAndOwnerId(name, id)!!
            val i = Bank.allAccounts.indexOf(account)
            if (!isCard) {
                Bank.allAccounts[i].limit = limit
                println("Теперь лимит по счету равен ${Bank.allAccounts[i].limit} ${account.currency}")
            } else {
                val card = Bank.getCardByAccountId(account.id)!!
                val j = Bank.allCards.indexOf(card)
                Bank.allCards[j].limit = limit
                println("Теперь лимит по карте равен ${Bank.allCards[j].limit} ${account.currency}")
            }
        }
        catch (e: Exception) {
            println("Не получилось обновить лимит")
        }
    }
    fun openCard() {
        try {
            println("Введите название счета, для которого вы хотите открыть карту")
            val name = readln()
            println("Введите ваш паспорт или ИНН")
            val s = readln()
            val id = Bank.getClientByTIN(s)?.id ?: Bank.getClientByPassport(s)!!.id
            val account = Bank.getAccountByNameAndOwnerId(name, id)!!
            if (Bank.getCardByAccountId(account.id) != null) {
                println("К вашему счету уже привязана карта! Сначала закройте старую")
                return
            }
            println("Введите, какую платежную систему будет использовать ваша карта (HoMir, MasterBart, Lisa)")
            val financialService = Service.valueOf(readln())
            Bank.addCard(account.id, financialService)
            println("К вашему счету успешно была привязана карта\n${Bank.allCards.last()}")
        }
        catch(e: Exception) {
            println("Не получилось открыть карту")
        }
    }
    fun closeCard() {
        try {
            println("Введите название счета, для которого вы хотите закрыть карту")
            val name = readln()
            println("Введите ваш паспорт или ИНН")
            val s = readln()
            val id = Bank.getClientByTIN(s)?.id ?: Bank.getClientByPassport(s)!!.id
            if (Bank.deleteCard(name, id)) println("Карта успешно удалена!")
        }
        catch (e: Exception) {
            println("Не удалось закрыть карту")
        }
    }
    fun rebindCard() {
        try {
            println("Введите название счета, для которого вы хотите закрыть карту")
            val nameFrom = readln()
            println("Введите название счета, для которого вы хотите открыть карту")
            val nameTo = readln()
            println("Введите ваш паспорт или ИНН")
            val s = readln()
            val id = Bank.getClientByTIN(s)?.id ?: Bank.getClientByPassport(s)!!.id
            val idFrom = Bank.getAccountByNameAndOwnerId(nameFrom, id)!!.id
            val idTo = Bank.getAccountByNameAndOwnerId(nameTo, id)!!.id
            val card = Bank.getCardByAccountId(idFrom)!!
            if (!Bank.deleteCard(nameFrom, id)) throw Exception()
            println("Карта успешно отвязана от счета $idFrom")
            Bank.addCard(idTo, card.financialService)
            println("Карта успешно привязана к счету $idTo")
        }
        catch (e: Exception) {
            println("Не удалось перепривязать карту")
        }
    }
    fun makeTransaction() {
        try {
            println("Введите название счета, с которого вы хотите перевести деньги")
            val name = readln()
            println("Введите ваш паспорт или ИНН")
            val s = readln()
            val id = Bank.getClientByTIN(s)?.id ?: Bank.getClientByPassport(s)!!.id
            val account = Bank.getAccountByNameAndOwnerId(name, id)!!
            println("Введите номер телефона человека, которому хотите перевести деньги (начиная с 8 без скобок и дефисов)")
            val number = readln()
            val accountTo = Bank.allAccounts.find {
                val a = (it.currency == account.currency)
                val b = (if (it.isPersonalAccount) Bank.getPersonalClientById(it.ownerId)!! else
                Bank.getLegalClientById(it.ownerId)!!).phoneNumber == number
                val c = (it.id != account.id)
                a && b && c
            }!!
            println("Введите, сколько ${account.currency} хотите перевести (ваш лимит ${account.limit}, а на счету лежит ${account.amount})")
            val amount = readln().toBigDecimal()
            Bank.addTransaction(account.id, accountTo.id, amount)
            if (Bank.allTransactions.last().status == Status.Completed)
                println("Транзакция прошла успешно, $amount ${account.currency} переведены\nТеперь на вашем счету ${account.amount} ${account.currency}")
            else
                println("Транзакция не прошла, извините :(")
        }
        catch(e: Exception) {
            println("Не удалось создать и провести транзакцию")
        }
    }
    fun cashWithdrawal() {
        try {
            println("Введите название счета, с которого хотите снять деньги")
            val name = readln()
            println("Введите ваш паспорт или ИНН")
            val s = readln()
            val id = Bank.getClientByTIN(s)?.id ?: Bank.getClientByPassport(s)!!.id
            val account = Bank.getAccountByNameAndOwnerId(name, id)!!
            println("Введите в каком банкомате или отделении банка хотите снять деньги (если не знаете его номер, то введит 0, мы выдадим деньги в главном отделении)")
            var cashierId = readln().toInt()
            if (Bank.getCashpointById(cashierId) == null) cashierId = Bank.allCashpoints[0].id
            println("Введите сколько денег хотите вывести (у вас лимит ${account.limit}, а всего ${account.amount} ${account.currency})")
            val amount = readln().toBigDecimal()
            Bank.addCashTransaction(account.id, cashierId, amount)
            if (Bank.allTransactions.last().status == Status.Completed)
                println("Выдача $amount ${account.currency} прошла успешно! Теперь у вас на счете ${account.amount} ${account.currency}")
            else
                println("Не получилось выдать наличные, извините :(")
        }
        catch(e: Exception) {
            println("Не получилось организовать выдачу наличных")
        }
    }
    fun cashIn() {
        try {
            println("Введите название счета, на который хотите положить деньги")
            val name = readln()
            println("Введите ваш паспорт или ИНН")
            val s = readln()
            val id = Bank.getClientByTIN(s)?.id ?: Bank.getClientByPassport(s)!!.id
            val account = Bank.getAccountByNameAndOwnerId(name, id)!!
            println("Введите в каком банкомате или отделении банка хотите положить деньги (если не знаете его номер, то введит 0, мы направим вас в главное отделение)")
            var cashierId = readln().toInt()
            if (Bank.getCashpointById(cashierId) == null) cashierId = Bank.allCashpoints[0].id
            println("Введите сколько денег хотите положить на ваш счет (сейчас на нем ${account.amount} ${account.currency})")
            val amount = readln().toBigDecimal()
            Bank.addCashTransaction(cashierId, account.id, amount)
            if (Bank.allTransactions.last().status == Status.Completed)
                println("Вы успешно положили себе на счет $amount ${account.currency}! Теперь у вас на счете ${account.amount} ${account.currency}")
            else
                println("Не получилось положить наличные, извините :(")
        }
        catch(e: Exception) {
            println("Не получилось организовать прием наличных")
        }
    }
}