package inside

import java.text.SimpleDateFormat
import java.util.*

enum class Service { Lisa, MasterBart, HoMir }

class Card(internal val accountId: Int, internal val financialService: Service, private val openingDate_: Calendar) {
    private val termInDays = 365
    internal val id = this.hashCode()
    private val openingDate: String get() = SimpleDateFormat("d/M/Y").format(openingDate_.time)
    private val endingDate_ = GregorianCalendar(openingDate_.get(Calendar.YEAR), openingDate_.get(Calendar.MONTH), openingDate_.get(Calendar.DATE))
    private val endingDate: String get() = SimpleDateFormat("d/M/Y").format(endingDate_.time)
    override fun toString() = "Карта принадлежит счету $accountId, банковская система $financialService и дата окончания $endingDate"
    internal var limit = Bank.getAccountById(accountId)!!.limit
        set(value) {
            if (value.toDouble() >= 0)
                field = value
        }

    init {
        endingDate_.add(Calendar.DATE, termInDays)
    }
}