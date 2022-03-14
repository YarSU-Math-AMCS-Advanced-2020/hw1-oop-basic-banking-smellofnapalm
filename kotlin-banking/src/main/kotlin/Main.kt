import java.math.BigDecimal
import java.util.*

fun main() {
    Bank.addClientPerson("Чирков", "Михаил", "Анатольевич", "1234 567890", GregorianCalendar(2002, GregorianCalendar.JANUARY, 30), SexEnum.Man,"89101234567", "Ярославль")
    Bank.addClientLegal("Рога и Копыта", "12345678", GregorianCalendar(2020, GregorianCalendar.JANUARY, 1), "89101234567", "Ярославль")

    Bank.addBankAccount(Bank.allPersonalClients[0].id, Currency.RUB, BigDecimal(-10))
    Bank.addBankAccount(Bank.allLegalClients[0].id, Currency.RUB)
    Bank.addCard(Bank.allAccounts[0].id, Service.HoMir)
    Bank.addCashpoint(true)

    Bank.addCashTransaction(Bank.allCashpoints[0].id, Bank.allAccounts[0].id, BigDecimal(100))
    Bank.addCashTransaction(Bank.allAccounts[0].id, Bank.allCashpoints[0].id, BigDecimal(50))
    Bank.addTransaction(Bank.allAccounts[0].id, Bank.allAccounts[1].id, BigDecimal(10))

    println("${Bank.allAccounts[0].amount}, ${Bank.allAccounts[1].amount}")

    println(Bank.allPersonalClients[0])
    println(Bank.allLegalClients[0])
    println(Bank.allAccounts[0])
    println(Bank.allCards[0])
    println(Bank.allTransactions[0])
    println(Bank.allCashpoints[0])
}