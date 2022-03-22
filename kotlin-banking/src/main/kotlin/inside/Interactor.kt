package inside

import inside.Bank.Adder.addTransaction
import inside.Bank.Getter.getAccountById
import inside.Bank.Getter.getCardById
import java.math.BigDecimal
import java.util.*

object Interactor {
    fun registerPersonalClient() {
        try {
            println("Введите ваше ФИО:")
            val (surname, name, patronymic) = readln().split(" ", limit = 3)
            println("Введите ваш паспорт:")
            val passport = readln()
            println("Введите дату рождения в формате ДД.ММ.ГГГГ:")
            val date = readln().split(".", limit = 3)
            val calendar = GregorianCalendar(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
            println("Введите ваш пол: Man, Woman или NonBinary")
            val sex = SexEnum.valueOf(readln())
            println("Введите ваш номер телефона (начиная с 8, без скобок и дефисов):")
            val number = readln()
            println("Введите адрес проживания:")
            val address = readln()
            Bank.Adder.addClientPerson(surname, name, patronymic, passport, calendar, sex, number, address)
        }
        catch (e: Exception) {
            println("Не удалось зарегистрировать нового пользователя")
        }
    }
    fun registerLegalClient() {
        try {
            println("Введите названия вашей компании:")
            val name = readln()
            println("Введите ваш ИНН:")
            val tin = readln()
            println("Введите дату основания в формате ДД.ММ.ГГГГ:")
            val date = readln().split(".", limit = 3)
            val calendar = GregorianCalendar(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
            println("Введите номер телефона компании:")
            val number = readln()
            println("Введите юридический адрес:")
            val address = readln()
            Bank.Adder.addClientLegal(name, tin, calendar, number, address)
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
                id = Bank.Getter.getClientByTIN(readln())!!.id
            }
            else if (ans.lowercase() == "p") {
                println("Введите ваш паспорт")
                id = Bank.Getter.getClientByPassport(readln())!!.id
            }
            println("Введите валюту счета (USD, EUR, RUB)")
            val currency = Currency.valueOf(readln())
            println("Вы хотите установить лимит на снятие денег и переводы (единый лимит) (Y/N)")
            var limit: BigDecimal? = null
            if (readln().lowercase() == "y") {
                println("Введите лимит")
                limit = readln().toBigDecimal()
            }
            Bank.Adder.addBankAccount(name, id, currency, limit)
        }
        catch (e: Exception) {
            println("Не удалось открыть новый счет")
        }
    }
    fun closeAccount() {
        try {
            println("Введите название счета, которой вы хотите закрыть")
            val name = readln()
            val account = getAccountByNameAndOwner(name)
            if (account.limit < account.amount) account.limit = account.amount
            println("Вы хотите перевести деньги на другой ваш счет? (Y/N)")
            val ans = readln()
            if (ans.lowercase() == "y") {
                val anotherAccount = Bank.Storage.allAccounts.find { it.name != name && it.ownerId == account.id && it.currency == account.currency}
                if (anotherAccount != null) {
                    println("Сейчас переведем средства (${account.amount} ${account.currency}) на ваш счет ${anotherAccount.name}")
                    Bank.Adder.addTransaction(account.id, anotherAccount.id, account.amount)
                    val trans = Bank.Storage.allTransactions.last()
                    if (trans.status == Status.Completed) {
                        println("Деньги были успешно переведены!")
                        Bank.Deleter.deleteCard(account.name, account.id) // Удаляем привязанную к счету карту
                        Bank.Storage.allAccounts.removeIf { it.name == name && it.id == account.id }
                        println("Ваш счет был успешно удален! Удачного вам дня!")
                        return
                    }
                }
            }
            if (ans.lowercase() == "y") println("Мы не нашли другой счет у вас, выведем всю сумму ${account.amount} наличными")
            println("Введите номер отделения или банкомата, в котором вам удобно забрать наличные (или 0, если хотите забрать в главном отделении банка)")
            var cash = readln().toInt()
            if (Bank.Getter.getCashpointById(cash) == null) cash = Bank.Storage.allCashpoints[0].id
            Bank.Adder.addCashTransaction(account.id, cash, account.amount)
            val trans = Bank.Storage.allTransactions.last()
            if (trans.status == Status.Completed) {
                println("Деньги (${trans.amount} ${account.currency}) вам выданы, сейчас закроем счет!")
                Bank.Deleter.deleteCard(account.name, account.id) // Удаляем привязанную к счету карту
                Bank.Storage.allAccounts.removeIf { it.name == name && it.ownerId == account.id }
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
            val account = getAccountByNameAndOwner(name)
            val i = Bank.Storage.allAccounts.indexOf(account)
            if (!isCard) {
                Bank.Storage.allAccounts[i].limit = limit
                println("Теперь лимит по счету равен ${Bank.Storage.allAccounts[i].limit} ${account.currency}")
            } else {
                val card = Bank.Getter.getCardByAccountId(account.id)!!
                val j = Bank.Storage.allCards.indexOf(card)
                Bank.Storage.allCards[j].limit = limit
                println("Теперь лимит по карте равен ${Bank.Storage.allCards[j].limit} ${account.currency}")
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
            val account = getAccountByNameAndOwner(name)
            if (Bank.Getter.getCardByAccountId(account.id) != null) {
                println("К вашему счету уже привязана карта! Сначала закройте старую")
                return
            }
            println("Введите, какую платежную систему будет использовать ваша карта (HoMir, MasterBart, Lisa)")
            val financialService = Service.valueOf(readln())
            Bank.Adder.addCard(account.id, financialService)
            println("К вашему счету успешно была привязана карта\n${Bank.Storage.allCards.last()}")
        }
        catch(e: Exception) {
            println("Не получилось открыть карту")
        }
    }
    fun closeCard() {
        try {
            println("Введите название счета, для которого вы хотите закрыть карту")
            val name = readln()
            val account = getAccountByNameAndOwner(name)
            if (Bank.Deleter.deleteCard(name, account.id)) println("Карта успешно удалена!")
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
            val id = Bank.Getter.getClientByTIN(s)?.id ?: Bank.Getter.getClientByPassport(s)!!.id
            val idFrom = Bank.Getter.getAccountByNameAndOwnerId(nameFrom, id)!!.id
            val idTo = Bank.Getter.getAccountByNameAndOwnerId(nameTo, id)!!.id
            val card = Bank.Getter.getCardByAccountId(idFrom)!!
            if (!Bank.Deleter.deleteCard(nameFrom, id)) throw Exception()
            println("Карта успешно отвязана от счета $idFrom")
            Bank.Adder.addCard(idTo, card.financialService)
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
            val account = getAccountByNameAndOwner(name)
            println("Введите номер телефона человека, которому хотите перевести деньги (начиная с 8 без скобок и дефисов)")
            val number = readln()
            val accountTo = Bank.Storage.allAccounts.find {
                val a = (it.currency == account.currency)
                val b = (if (it.isPersonalAccount) Bank.Getter.getPersonalClientById(it.ownerId)!! else
                Bank.Getter.getLegalClientById(it.ownerId)!!).phoneNumber == number
                val c = (it.id != account.id)
                a && b && c
            }!!
            println("Введите, сколько ${account.currency} хотите перевести (ваш лимит ${account.limit}, а на счету лежит ${account.amount})")
            val amount = readln().toBigDecimal()
            Bank.Adder.addTransaction(account.id, accountTo.id, amount)
            if (Bank.Storage.allTransactions.last().status == Status.Completed)
                println("Транзакция прошла успешно, $amount ${account.currency} переведены\nТеперь на вашем счету ${account.amount} ${account.currency}")
            else
                println("Транзакция не прошла, извините :(")
        }
        catch(e: Exception) {
            println("Не удалось создать и провести транзакцию")
        }
    }
    fun makeTransactionWithCard() {
        try {
            println("Введите номер карты, с которой хотите списать деньги")
            val card = getCardById(readln().toInt())!!
            println("Введите ИНН или паспорт, чтобы подтвердить владение картой")
            val passOrTin = readln()
            val ownerId = Bank.Getter.getClientByPassport(passOrTin)?.id ?: Bank.Getter.getClientByTIN(passOrTin)!!.id
            if (ownerId != getAccountById(card.accountId)!!.ownerId) throw Exception()
            println("Введите номер карты, на которую хотите перевести")
            val cardTo = getCardById(readln().toInt())!!
            println("Введите сумму, которую хотите перевести")
            val amount = readln().toBigDecimal()
            addTransaction(card.id, cardTo.id, amount, true)
            if (Bank.Storage.allTransactions.last().status == Status.Completed) {
                println("Деньги ($amount ${getAccountById(card.accountId)!!.currency}) были успешно переведены на другую карту!")
                println("Теперь на вашей карте ${getAccountById(card.accountId)!!.amount} ${getAccountById(card.accountId)!!.currency}")
            }
            else println("Не удалось выполнить перевод средств")
        }
        catch (e: Exception) {
            println("Перевод с карты на карту не удался")
        }
    }
    fun cashWithdrawal() {
        try {
            println("Введите название счета, с которого хотите снять деньги")
            val name = readln()
            val account = getAccountByNameAndOwner(name)
            println("Введите в каком банкомате или отделении банка хотите снять деньги (если не знаете его номер, то введит 0, мы выдадим деньги в главном отделении)")
            var cashierId = readln().toInt()
            if (Bank.Getter.getCashpointById(cashierId) == null) cashierId = Bank.Storage.allCashpoints[0].id
            println("Введите сколько денег хотите вывести (у вас лимит ${account.limit}, а всего ${account.amount} ${account.currency})")
            val amount = readln().toBigDecimal()
            Bank.Adder.addCashTransaction(account.id, cashierId, amount)
            if (Bank.Storage.allTransactions.last().status == Status.Completed)
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
            val account = getAccountByNameAndOwner(name)
            println("Введите в каком банкомате или отделении банка хотите положить деньги (если не знаете его номер, то введит 0, мы направим вас в главное отделение)")
            var cashierId = readln().toInt()
            if (Bank.Getter.getCashpointById(cashierId) == null) cashierId = Bank.Storage.allCashpoints[0].id
            println("Введите сколько денег хотите положить на ваш счет (сейчас на нем ${account.amount} ${account.currency})")
            val amount = readln().toBigDecimal()
            Bank.Adder.addCashTransaction(cashierId, account.id, amount)
            if (Bank.Storage.allTransactions.last().status == Status.Completed)
                println("Вы успешно положили себе на счет $amount ${account.currency}! Теперь у вас на счете ${account.amount} ${account.currency}")
            else
                println("Не получилось положить наличные, извините :(")
        }
        catch(e: Exception) {
            println("Не получилось организовать прием наличных")
        }
    }
    fun printAllClients() {
        println("Список всех клиентов:")
        for (client in Bank.Storage.allPersonalClients)
            println("${client.id}: $client")
        println("------------")
        for (client in Bank.Storage.allLegalClients)
            println("${client.id}: $client")
    }
    fun printAllAccount() {
        println("Список всех счетов:")
        for (account in Bank.Storage.allAccounts)
            println("${account.id}: $account")
    }
    fun printAllCards() {
        println("Список всех карт:")
        for (card in Bank.Storage.allCards)
            println("${card.id}: $card")
    }
    fun printAllCashpoints() {
        println("Список всех банкоматов и отделений")
        for (cashpoint in Bank.Storage.allCashpoints)
            println(cashpoint)
    }
    fun printAllTransactions() {
        println("Список всех транзакций")
        for (trans in Bank.Storage.allTransactions)
            println(trans)
    }

    private fun getAccountByNameAndOwner(name: String): BankAccount {
        println("Введите ваш паспорт или ИНН, чтобы подтвердить владение счетом")
        val s = readln()
        val id = Bank.Getter.getClientByTIN(s)?.id ?: Bank.Getter.getClientByPassport(s)!!.id
        return Bank.Getter.getAccountByNameAndOwnerId(name, id)!!
    }
}