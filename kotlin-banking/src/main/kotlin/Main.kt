import java.util.GregorianCalendar

fun main() {
    Bank.addClientPerson("Чирков", "Михаил", "Анатольевич", "1234 567890", GregorianCalendar(2002, GregorianCalendar.JANUARY, 30), SexEnum.Man,"89101234567", "Ярославль")
    Bank.addClientLegal("Рога и Копыта", "12345678", GregorianCalendar(2020, GregorianCalendar.JANUARY, 1), "89101234567", "Ярославль")
    println(Bank.allLegalClients[0].phoneNumber)
    println(Bank.allPersonalClients[0].birthDate)
    println(Bank.allPersonalClients[0].sex)
}