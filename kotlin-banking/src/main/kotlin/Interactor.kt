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
            println("Вы компания или человек? (C/P)")
            val ans = scanner.nextLine()
            var id = 0
            if (ans.lowercase() == "c") {
                println("Введите ваш ИНН")
                id = Bank.getClientByTIN(scanner.nextLine())!!.id
            }
            else if (ans.lowercase() == "p") {
                println("Введите ваш паспорт")
                id = Bank.getClientByPassport(scanner.nextLine())!!.id
            }
            println("Введите валюту счета (USD, EUR, RUB)")
            val currency = Currency.valueOf(scanner.nextLine())
            println("Вы хотите установить лимит на снятие денег и переводы (единый лимит) (Y/N)")
            var limit: BigDecimal? = null
            if (scanner.nextLine().lowercase() == "y") {
                println("Введите лимит")
                limit = scanner.nextLine().toBigDecimal()
            }
            Bank.addBankAccount(id, currency, limit)
        }
        catch (e: Exception) {
            println("Не удалось открыть новый счет")
        }
    }
}