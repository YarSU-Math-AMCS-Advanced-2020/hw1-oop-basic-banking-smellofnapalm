package inside

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

enum class Status { ToProcess, InProcess, Completed, Rejected }

class Transaction(private val idFrom: Int, private val idTo: Int, internal val amount: BigDecimal, private val _date: Calendar, isCashTransaction: Boolean = false) {
    internal var status = Status.ToProcess
    private val date get() = SimpleDateFormat("h:m:s d/M/Y").format(_date.time)
    private var currency: Currency = Currency.RUB
    override fun toString() = "Транзакция $idFrom --> $idTo в размере $amount $currency, ее статус $status, время проведения $date"

    init {
        status = Status.InProcess
        if (amount.toDouble() < 0) status = Status.Rejected
        else if (isCashTransaction) makeCashTransaction(idFrom, idTo, amount)
        // Перевод между двумя счетами
        else {
            var from: Int = -1
            var to: Int = -1
            for (i in Bank.Storage.allAccounts.indices) {
                if (Bank.Storage.allAccounts[i].id == idFrom) from = i
                if (Bank.Storage.allAccounts[i].id == idTo) to = i
            }

            currency = Currency.valueOf(Bank.Storage.allAccounts[from].currency)
            if (from == to || (from == -1 && to == -1)) {
                status = Status.Rejected
            } else if (Bank.Storage.allAccounts[from].currency != Bank.Storage.allAccounts[to].currency) {
                status = Status.Rejected
            } else if (Bank.Storage.allAccounts[from].amount < amount || amount > Bank.Storage.allAccounts[from].limit) {
                status = Status.Rejected
            } else {
                Bank.Storage.allAccounts[from].amount -= amount
                Bank.Storage.allAccounts[to].amount += amount
                status = Status.Completed
            }
        }
    }

    private fun makeCashTransaction(idFrom: Int, idTo: Int, amount: BigDecimal) {
        // Снятие наличных (т.е. деньги снимаются со счета человека и поступают на фиктивный счет банкомата)
        if (Bank.Getter.getAccountById(idFrom) != null && Bank.Getter.getCashpointById(idTo) != null) {
            val from = Bank.Getter.getAccountById(idFrom)!!
            val indexFrom = Bank.Storage.allAccounts.indexOf(from)
            currency = Currency.valueOf(from.currency)

            if (amount > from.limit) {
                status = Status.Rejected
            }
            else if (amount > from.amount) {
                status = Status.Rejected
            }
            else {
                Bank.Storage.allAccounts[indexFrom].amount -= amount
                status = Status.Completed
            }
        }
        // Внесение наличных (т.е. от фиктивного счета банкомата на счет человека)
        else if (Bank.Getter.getCashpointById(idFrom) != null && Bank.Getter.getAccountById(idTo) != null) {
            val to = Bank.Getter.getAccountById(idTo)!!
            val indexTo = Bank.Storage.allAccounts.indexOf(to)
            currency = Currency.valueOf(to.currency)

            Bank.Storage.allAccounts[indexTo].amount += amount
            status = Status.Completed
        }
        else status = Status.Rejected
    }

}