
import java.math.BigDecimal
import java.util.*

object Bank {
    var allPersonalClients = mutableListOf<ClientPerson>()
    var allLegalClients = mutableListOf<ClientLegal>()
    var allAccounts = mutableListOf<BankAccount>()
    var allCards = mutableListOf<Card>()
    var allTransactions = mutableListOf<Transaction>()
    var allCashpoints = mutableListOf<Cashpoint>()

    fun addClientPerson(surname: String, firstName: String, patronymic: String?, passport: String, birthDate_: Calendar, sex_: SexEnum, phoneNumber: String, address: String) {
        val newPerson = ClientPerson(surname, firstName, patronymic, passport, birthDate_, sex_, phoneNumber, address)
        if (allPersonalClients.find {(it.name == newPerson.name && it.phoneNumber == newPerson.phoneNumber) || (it.passport == passport)} == null) allPersonalClients.add(newPerson)
    }
    fun addClientLegal(name: String, TIN: String, establishing_date_: Calendar, phoneNumber: String, address: String) {
        val newLegal = ClientLegal(name, TIN, establishing_date_, phoneNumber, address)
        if (allLegalClients.find {(it.name == newLegal.name && it.phoneNumber == newLegal.phoneNumber) || (it.TIN == TIN)} == null) allLegalClients.add(newLegal)
    }
    fun addBankAccount(name: String, ownerId: Int, currency_: Currency, limit_: BigDecimal? = null) {
        if (allPersonalClients.find {it.id == ownerId} == null &&
            allLegalClients.find { it.id == ownerId } == null) return

        val newBankAccount = BankAccount(name, ownerId, currency_, limit_)
        if (newBankAccount.limit.toDouble() <= 0) newBankAccount.limit = BigDecimal(Int.MAX_VALUE)
        if (allAccounts.find {it.name == name && it.ownerId == ownerId} == null) allAccounts.add(newBankAccount)
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
    fun addTransaction(fromId: Int, toId: Int, amount: BigDecimal, isCardId: Boolean = false) {
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

    fun getClientByPassport(passport: String) = allPersonalClients.find {client -> client.passport == passport}
    fun getClientByTIN(TIN: String) = allLegalClients.find { client -> client.TIN == TIN }
    fun getAccountById(id: Int) = allAccounts.find { account -> account.id == id }
    fun getCardById(id: Int) = allCards.find { card -> card.id == id }
    fun getCashpointById(id: Int) = allCashpoints.find {cashpoint ->  cashpoint.id == id}
    fun getPersonalClientById(id: Int) = allPersonalClients.find {client -> client.id == id}
    fun getLegalClientById(id: Int) = allLegalClients.find {client -> client.id == id}
    fun getAccountByNameAndOwnerId(name: String, id: Int) = allAccounts.find { account -> account.name == name && account.ownerId == id }
    fun getCardByAccountId(id: Int) = allCards.find {card -> card.accountId == id}

    fun deleteCard(accountName: String, ownerId: Int): Boolean {
        val accountId = getAccountByNameAndOwnerId(accountName, ownerId)?.id ?: return false
        if (allCards.find {it.accountId == accountId} == null) return false
        allCards.removeIf { it.accountId == accountId }
        return true
    }
}