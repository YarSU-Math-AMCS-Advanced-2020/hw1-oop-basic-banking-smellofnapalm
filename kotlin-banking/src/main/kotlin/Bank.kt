
import java.math.BigDecimal
import java.util.*

object Bank {
    var allPersonalClients = mutableListOf<ClientPerson>()
    var allLegalClients = mutableListOf<ClientLegal>()
    var allAccounts = mutableListOf<BankAccount>()
    var allCards = mutableListOf<Card>()
    var allTransactions = mutableListOf<Transaction>()
    var allCashpoints = mutableListOf<Cashpoint>()

    fun addClientPerson(surname_: String, firstName_: String, patronymic_: String?, passport: String, birthDate_: Calendar, sex_: SexEnum, phoneNumber_: String, address_: String) {
        val newPerson = ClientPerson(surname_, firstName_, patronymic_, passport, birthDate_, sex_, phoneNumber_, address_)
        if (newPerson !in allPersonalClients) allPersonalClients.add(newPerson)
    }
    fun addClientLegal(name: String, TIN: String, establishing_date_: Calendar, phoneNumber_: String, address_: String) {
        val newLegal = ClientLegal(name, TIN, establishing_date_, phoneNumber_, address_)
        if (newLegal !in allLegalClients) allLegalClients.add(newLegal)
    }
    fun addBankAccount(name: String, ownerId: Int, currency_: Currency, limit_: BigDecimal? = null) {
        if (allPersonalClients.find {it.id == ownerId} == null &&
            allLegalClients.find { it.id == ownerId } == null) return

        val newBankAccount = BankAccount(name, ownerId, currency_, limit_)
        if (newBankAccount.limit.toDouble() <= 0) newBankAccount.limit = BigDecimal(Int.MAX_VALUE)
        if (newBankAccount !in allAccounts) allAccounts.add(newBankAccount)
    }
    fun addCard(accountId: Int, financialService: Service) {
        val openingDate = GregorianCalendar()
        if (getAccountById(accountId) != null) {
            // К одном счету привязана лишь одна карта
            if (Bank.allCards.find{it.accountId == accountId} != null) return
            val newCard = Card(accountId, financialService, openingDate)
            if (newCard !in allCards) allCards.add(newCard)
        }
    }
    fun addTransaction(fromId: Int, toId: Int, amount: BigDecimal, isCardId: Boolean = false, isCashTransaction: Boolean = false) {
        val currentDate = GregorianCalendar()
        if (isCardId) {
            val newFromId = getCardById(fromId)?.accountId
            val newToId = getCardById(toId)?.accountId
            if (newFromId == null || newToId == null) return
            if (getAccountById(newFromId) == null || getAccountById(newToId) == null) return
            val newTransaction = Transaction(newFromId, newToId, amount, currentDate)
            if (newTransaction !in allTransactions) allTransactions.add(newTransaction)
        }
        if (getAccountById(fromId) == null || getAccountById(toId) == null) return
        val newTransaction = Transaction(fromId, toId, amount, currentDate)
        if (newTransaction !in allTransactions) allTransactions.add(newTransaction)
    }
    fun addCashpoint(isATM: Boolean) {
        val newCashpoint = Cashpoint(isATM)
        if (newCashpoint !in allCashpoints) allCashpoints.add(newCashpoint)
    }
    fun addCashTransaction(fromId: Int, toId: Int, amount: BigDecimal, isCardId: Boolean = false) {
        val currentDate = GregorianCalendar()
        if (isCardId) {
            val newFromId = getCardById(fromId)?.accountId
            val newToId = getCardById(toId)?.accountId
            if (newFromId != null) {
                if (getAccountById(newFromId) != null) {
                    val newTransaction = Transaction(newFromId, toId, amount, currentDate, true)
                    if (newTransaction !in allTransactions) allTransactions.add(newTransaction)
                }
                return
            }
            else if (newToId != null) {
                if (getAccountById(newToId) != null) {
                    val newTransaction = Transaction(fromId, newToId, amount, currentDate, true)
                    if (newTransaction !in allTransactions) allTransactions.add(newTransaction)
                }
            }
        }
        if ((getAccountById(fromId) != null && getCashpointById(toId) != null) || (getCashpointById(fromId) != null && getAccountById(toId) != null)) {
            val newTransaction = Transaction(fromId, toId, amount, currentDate, true)
            if (newTransaction !in allTransactions) allTransactions.add(newTransaction)
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
    fun getCardById(id: Int) = allCards.find { card -> card.id == id }
    fun getCashpointById(id: Int) = allCashpoints.find {cashpoint ->  cashpoint.id == id}
    fun getPersonalClientById(id: Int) = allPersonalClients.find {client -> client.id == id}
    fun getLegalClientById(id: Int) = allLegalClients.find {client -> client.id == id}
    fun getAccountByNameAndOwnerId(name: String, id: Int) = allAccounts.find { account -> account.name == name && account.ownerId == id }
}