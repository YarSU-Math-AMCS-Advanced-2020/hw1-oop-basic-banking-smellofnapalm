import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

enum class Status { ToProcess, InProcess, Completed, Rejected }

class Transaction(val idFrom: Int, val idTo: Int, val amount: BigDecimal, val date_: Calendar, val isCashTransaction: Boolean = false) {
    var status = Status.ToProcess
    val date get() = SimpleDateFormat("d/M/Y").format(date_.time)
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

                if (amount > (from.limit ?: BigDecimal(Int.MAX_VALUE))) {
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

                Bank.allAccounts[indexTo].amount += amount
                status = Status.Completed
            }
            else {
                status = Status.Rejected
            }
        }
        else {
            var from: Int = -1
            var to: Int = -1
            for (i in Bank.allAccounts.indices) {
                if (Bank.allAccounts[i].id == idFrom) from = i
                if (Bank.allAccounts[i].id == idTo) to = i
            }

            if (from == to || (from == -1 && to == -1)) {
                status = Status.Rejected
            } else if (Bank.allAccounts[from].currency != Bank.allAccounts[to].currency) {
                status = Status.Rejected
            } else if (Bank.allAccounts[from].amount < amount || amount > (Bank.allAccounts[from].limit ?: BigDecimal(
                    Int.MAX_VALUE
                ))
            ) {
                status = Status.Rejected
            } else {
                Bank.allAccounts[from].amount -= amount
                Bank.allAccounts[to].amount += amount
                status = Status.Completed
            }
        }
    }

}