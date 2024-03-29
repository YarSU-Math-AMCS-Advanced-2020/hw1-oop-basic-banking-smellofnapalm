package inside

import inside.Bank.Getter.getAccountById
import inside.Bank.Getter.getAccountByNameAndOwnerId
import inside.Bank.Getter.getCardById
import inside.Bank.Getter.getCashpointById

import inside.Bank.Storage.allAccounts
import inside.Bank.Storage.allCards
import inside.Bank.Storage.allCashpoints
import inside.Bank.Storage.allLegalClients
import inside.Bank.Storage.allPersonalClients
import inside.Bank.Storage.allTransactions

import java.math.BigDecimal
import java.util.*

object Bank {
    object Storage {
        var allPersonalClients = mutableListOf<ClientPerson>()
        var allLegalClients = mutableListOf<ClientLegal>()
        var allAccounts = mutableListOf<BankAccount>()
        var allCards = mutableListOf<Card>()
        var allTransactions = mutableListOf<Transaction>()
        var allCashpoints = mutableListOf(Cashpoint(false))
    }

    object Adder {
        fun addClientPerson(
            surname: String,
            firstName: String,
            patronymic: String?,
            passport: String,
            birthDate_: Calendar,
            sex_: SexEnum,
            phoneNumber: String,
            address: String
        ) {
            val newPerson =
                ClientPerson(surname, firstName, patronymic, passport, birthDate_, sex_, phoneNumber, address)
            if (allPersonalClients.find { (it.name == newPerson.name && it.phoneNumber == newPerson.phoneNumber) || (it.passport == passport) } == null) allPersonalClients.add(
                newPerson
            )
        }

        fun addClientLegal(
            name: String,
            TIN: String,
            establishing_date_: Calendar,
            phoneNumber: String,
            address: String
        ) {
            val newLegal = ClientLegal(name, TIN, establishing_date_, phoneNumber, address)
            if (allLegalClients.find { (it.name == newLegal.name && it.phoneNumber == newLegal.phoneNumber) || (it.TIN == TIN) } == null) allLegalClients.add(
                newLegal
            )
        }

        fun addBankAccount(name: String, ownerId: Int, currency_: Currency, limit_: BigDecimal? = null) {
            if (allPersonalClients.find { it.id == ownerId } == null &&
                allLegalClients.find { it.id == ownerId } == null) return

            val newBankAccount = BankAccount(name, ownerId, currency_, limit_)
            if (newBankAccount.limit.toDouble() <= 0) newBankAccount.limit = BigDecimal(Int.MAX_VALUE)
            if (allAccounts.find { it.name == name && it.ownerId == ownerId } == null) allAccounts.add(newBankAccount)
        }

        fun addCard(accountId: Int, financialService: Service) {
            val openingDate = GregorianCalendar()
            if (getAccountById(accountId) != null) {
                // К одном счету привязана лишь одна карта
                if (allCards.find { it.accountId == accountId } != null) return
                val newCard = Card(accountId, financialService, openingDate)
                if (newCard !in allCards) allCards.add(newCard)
            }
        }

        fun addTransaction(fromId: Int, toId: Int, amount: BigDecimal) {
            val currentDate = GregorianCalendar()
            val newFromId = getCardById(fromId)?.accountId ?: fromId
            val newToId = getCardById(toId)?.accountId ?: toId
            if (getAccountById(newFromId) == null || getAccountById(newToId) == null) return
            val newTransaction = Transaction(newFromId, newToId, amount, currentDate)
            if (newTransaction !in allTransactions) allTransactions.add(newTransaction)
        }

        fun addCashpoint(isATM: Boolean) {
            val newCashpoint = Cashpoint(isATM)
            if (newCashpoint !in allCashpoints) allCashpoints.add(newCashpoint)
        }

        fun addCashTransaction(fromId: Int, toId: Int, amount: BigDecimal) {
            val currentDate = GregorianCalendar()
            val newFromId = getCardById(fromId)?.accountId ?: fromId
            val newToId = getCardById(toId)?.accountId ?: toId
            if ((getAccountById(newFromId) != null && getCashpointById(newToId) != null)
                || (getCashpointById(newFromId) != null && getAccountById(newToId) != null)) {
                val newTransaction = Transaction(newFromId, newToId, amount, currentDate, true)
                if (newTransaction !in allTransactions) allTransactions.add(newTransaction)
            }
        }
    }

    object Getter {
        fun getClientByPassport(passport: String) = allPersonalClients.find { client -> client.passport == passport }
        fun getClientByTIN(TIN: String) = allLegalClients.find { client -> client.TIN == TIN }
        fun getAccountById(id: Int) = allAccounts.find { account -> account.id == id }
        fun getCardById(id: Int) = allCards.find { card -> card.id == id }
        fun getCashpointById(id: Int) = allCashpoints.find { cashpoint -> cashpoint.id == id }
        fun getPersonalClientById(id: Int) = allPersonalClients.find { client -> client.id == id }
        fun getLegalClientById(id: Int) = allLegalClients.find { client -> client.id == id }
        fun getAccountByNameAndOwnerId(name: String, id: Int) =
            allAccounts.find { account -> account.name == name && account.ownerId == id }

        fun getCardByAccountId(id: Int) = allCards.find { card -> card.accountId == id }

        fun checkCardValidity(cardId: Int): Boolean {
            val currentDate = GregorianCalendar()
            val card = getCardById(cardId)!!
            if (card._endingDate.after(currentDate)) return !Deleter.deleteCard(cardId)
            return true
        }
    }

    object Deleter {
        fun deleteCard(accountName: String, ownerId: Int): Boolean {
            val accountId = getAccountByNameAndOwnerId(accountName, ownerId)?.id ?: return false
            if (allCards.find { it.accountId == accountId } == null) return false
            allCards.removeIf { it.accountId == accountId }
            return true
        }
        fun deleteCard(cardId: Int): Boolean {
            if (getCardById(cardId) == null) return false
            allCards.removeIf { it.id == cardId }
            return true
        }
    }
}