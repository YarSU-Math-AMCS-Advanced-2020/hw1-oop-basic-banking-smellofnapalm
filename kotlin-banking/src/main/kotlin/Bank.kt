import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

object Bank {
    var allPersonalClients = mutableListOf<ClientPerson>()
    var allLegalClients = mutableListOf<ClientLegal>()
    var allAccounts = mutableListOf<BankAccount>()
    var allCards = mutableListOf<Card>()

    fun addClientPerson(surname_: String, firstName_: String, patronymic_: String?, passport: String, birthDate_: Calendar, sex_: SexEnum, phoneNumber_: String, address_: String) {
        val newPerson = ClientPerson(surname_, firstName_, patronymic_, passport, birthDate_, sex_, phoneNumber_, address_)
        if (newPerson !in allPersonalClients) allPersonalClients.add(newPerson)
    }
    fun addClientLegal(name: String, TIN: String, establishing_date_: Calendar, phoneNumber_: String, address_: String) {
        val newLegal = ClientLegal(name, TIN, establishing_date_, phoneNumber_, address_)
        if (newLegal !in allLegalClients) allLegalClients.add(newLegal)
    }
    fun addBankAccount(ownerId: Int, currency_: Currency, limit_: BigDecimal? = null) {
        if (allPersonalClients.find {it.id == ownerId} == null &&
            allLegalClients.find { it.id == ownerId } == null) return

        val newBankAccount = BankAccount(ownerId, currency_, limit_)
        if ((newBankAccount.limit != null) && (newBankAccount.limit!!.toDouble() <= 0)) newBankAccount.limit = null
        if (newBankAccount !in allAccounts) allAccounts.add(newBankAccount)
    }
    fun addCard(accountId: Int, financialService: Service) {
        val openingDate = GregorianCalendar()
        println(SimpleDateFormat("d/M/Y").format(openingDate.time))

        if (getAccountById(accountId) != null) {
            val newCard = Card(accountId, financialService, openingDate)
            if (newCard !in allCards) allCards.add(newCard)
        }
    }


    fun deleteClientById(id: Int) {
        allPersonalClients.removeIf {client -> client.id == id}
        allLegalClients.removeIf {client -> client.id == id}
        // TODO: Remove all connected bank accounts and cards
    }

    fun getClientByPassport(passport: String) = allPersonalClients.find {client -> client.passport == passport}
    fun getClientByTIN(TIN: String) = allLegalClients.find { client -> client.TIN == TIN }
    fun getAccountById(id: Int) = allAccounts.find { account -> account.id == id }
}