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
                    val trans = Transaction(account.id, anotherAccount.id, account.amount, date)
                    if (trans.status == Status.Completed) {
                        println("Деньги были успешно переведены!")
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
            val trans = Transaction(account.id, cash, account.amount, date, true)
            if (trans.status == Status.Completed) {
                println("Деньги (${trans.amount} ${account.currency}) вам выданы, сейчас закроем счет!")
                Bank.allAccounts.removeIf { it.name == name && it.ownerId == id }
                println("Ваш счет был успешно удален! Удачного вам дня!")
            }
        }
        catch (e: Exception) {
            println("Не получилось закрыть счет")
        }
    }
}