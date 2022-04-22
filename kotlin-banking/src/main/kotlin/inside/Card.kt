package inside

import java.text.SimpleDateFormat
import java.util.*

enum class Service { Lisa, MasterBart, HoMir }

class Card(internal val accountId: Int, internal val financialService: Service, private val _openingDate: Calendar) {
    private val termInDays = 365
    internal val id = this.hashCode()
    private val openingDate: String get() = SimpleDateFormat("d/M/Y").format(_openingDate.time)
    internal val _endingDate = GregorianCalendar(_openingDate[Calendar.YEAR], _openingDate[Calendar.MONTH], _openingDate[Calendar.DATE])
    internal val endingDate: String get() = SimpleDateFormat("d/M/Y").format(_endingDate.time)
    override fun toString() = "Карта принадлежит счету $accountId (${Bank.Getter.getAccountById(accountId)!!.amount} ${Bank.Getter.getAccountById(accountId)!!.currency}), банковская система $financialService и дата окончания $endingDate"
    internal var limit = Bank.Getter.getAccountById(accountId)!!.limit
        set(value) {
            if (value.toDouble() >= 0)
                field = value
        }

    init {
        _endingDate.add(Calendar.DATE, termInDays)
    }
}