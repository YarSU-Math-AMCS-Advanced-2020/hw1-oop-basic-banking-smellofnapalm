import java.math.BigDecimal
import java.util.*

fun main() {
    Bank.addClientPerson("Чирков", "Михаил", "Анатольевич", "1234 567890", GregorianCalendar(2002, GregorianCalendar.JANUARY, 30), SexEnum.Man,"89101234567", "Ярославль")
    Bank.addClientLegal("Рога и Копыта", "12345678", GregorianCalendar(2020, GregorianCalendar.JANUARY, 1), "89101234567", "Ярославль")

    Bank.addBankAccount(Bank.allPersonalClients[0].id, Currency.RUB, BigDecimal(-10))
    Bank.addBankAccount(Bank.allLegalClients[0].id, Currency.RUB)
    Bank.addCard(Bank.allAccounts[0].id, Service.HoMir)

    Bank.allAccounts[0].amount = BigDecimal(15)
    Bank.addTransaction(Bank.allAccounts[0].id, Bank.allAccounts[1].id, BigDecimal(10))

    println(Bank.allLegalClients[0].phoneNumber)
    println(Bank.allPersonalClients[0].birthDate)
    println(Bank.allPersonalClients[0].sex)
    println(Bank.allAccounts[0].limit)
    println(Bank.allCards[0].openingDate)
    println(Bank.allCards[0].endingDate)
    println("${Bank.allAccounts[0].amount}, ${Bank.allAccounts[1].amount}")
}