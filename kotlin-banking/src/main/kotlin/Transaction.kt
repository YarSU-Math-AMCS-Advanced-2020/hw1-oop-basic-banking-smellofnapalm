import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

enum class Status { ToProcess, InProcess, Completed, Rejected }

class Transaction(val idFrom: Int, val idTo: Int, val amount: BigDecimal, val date_: Calendar) {
    var status = Status.ToProcess
    val date get() = SimpleDateFormat("d/M/Y").format(date_.time)
    init {
        var from: Int = -1
        var to: Int = -1
        for (i in Bank.allAccounts.indices) {
            if (Bank.allAccounts[i].id == idFrom) from = i
            if (Bank.allAccounts[i].id == idTo) to = i
        }
        status = Status.InProcess
        if (amount.toDouble() <= 0) {
            status = Status.Rejected
        }
        else if (from == to || (from == -1 && to == -1)) {
            status = Status.Rejected
        }
        else if (Bank.allAccounts[from].currency != Bank.allAccounts[to].currency) {
            status = Status.Rejected
        }
        else if (Bank.allAccounts[from].amount < amount || amount > (Bank.allAccounts[from].limit ?: BigDecimal(Int.MAX_VALUE))) {
            status = Status.Rejected
        }
        else {
            Bank.allAccounts[from].amount -= amount
            Bank.allAccounts[to].amount += amount
            status = Status.Completed
        }
    }

}