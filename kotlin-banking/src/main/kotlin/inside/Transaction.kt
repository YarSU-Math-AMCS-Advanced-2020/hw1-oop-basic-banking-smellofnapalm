package inside

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

enum class Status { ToProcess, InProcess, Completed, Rejected }

class Transaction(private val idFrom: Int, private val idTo: Int, internal val amount: BigDecimal, private val date_: Calendar, private val isCashTransaction: Boolean = false) {
    internal var status = Status.ToProcess
    private val date get() = SimpleDateFormat("h:m:s d/M/Y").format(date_.time)
    private var currency: Currency = Currency.RUB
    override fun toString() = "Транзакция $idFrom --> $idTo в размере $amount $currency, ее статус $status, время проведения $date"

    init {
        status = Status.InProcess
        if (amount.toDouble() <= 0) {
            status = Status.Rejected
        }
        else if (isCashTransaction) {
            // Снятие наличных (т.е. деньги снимаются со счета человека и поступают на фиктивный счет банкомата)
            if (Bank.getAccountById(idFrom) != null && Bank.getCashpointById(idTo) != null) {
                val from = Bank.getAccountById(idFrom)!!
                val to = Bank.getCashpointById(idTo)!!
                val indexFrom = Bank.allAccounts.indexOf(from)
                val indexTo = Bank.allCashpoints.indexOf(to)
                currency = from.currency_

                if (amount > from.limit) {
                    status = Status.Rejected
                }
                else if (amount > from.amount) {
                    status = Status.Rejected
                }
                else {
                    Bank.allAccounts[indexFrom].amount -= amount
                    status = Status.Completed
                }
            }
            // Внесение наличных (т.е. от фиктивного счета банкомата на счет человека)
            else if (Bank.getCashpointById(idFrom) != null && Bank.getAccountById(idTo) != null) {
                val from = Bank.getCashpointById(idFrom)!!
                val to = Bank.getAccountById(idTo)!!
                val indexFrom = Bank.allCashpoints.indexOf(from)
                val indexTo = Bank.allAccounts.indexOf(to)
                currency = to.currency_

                Bank.allAccounts[indexTo].amount += amount
                status = Status.Completed
            }
            else {
                status = Status.Rejected
            }
        }
        // Перевод между двумя счетами
        else {
            var from: Int = -1
            var to: Int = -1
            for (i in Bank.allAccounts.indices) {
                if (Bank.allAccounts[i].id == idFrom) from = i
                if (Bank.allAccounts[i].id == idTo) to = i
            }

            currency = Bank.allAccounts[from].currency_
            if (from == to || (from == -1 && to == -1)) {
                status = Status.Rejected
            } else if (Bank.allAccounts[from].currency != Bank.allAccounts[to].currency) {
                status = Status.Rejected
            } else if (Bank.allAccounts[from].amount < amount || amount > Bank.allAccounts[from].limit) {
                status = Status.Rejected
            } else {
                Bank.allAccounts[from].amount -= amount
                Bank.allAccounts[to].amount += amount
                status = Status.Completed
            }
        }
    }

}