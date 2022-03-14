import java.text.SimpleDateFormat
import java.util.*

enum class Service { Lisa, MasterBart, HoMir }

class Card(val accountId: Int, val financialService: Service, private val openingDate_: Calendar) {
    private val termInDays = 365
    val id = this.hashCode()
    val openingDate: String get() = SimpleDateFormat("d/M/Y").format(openingDate_.time)
    private val endingDate_ = GregorianCalendar(openingDate_.get(Calendar.YEAR), openingDate_.get(Calendar.MONTH), openingDate_.get(Calendar.DATE))
    val endingDate: String get() = SimpleDateFormat("d/M/Y").format(endingDate_.time)
    override fun toString() = "Карта принадлежит счету $accountId, банковская система $financialService и дата окончания $endingDate"
    init {
        endingDate_.add(Calendar.DATE, termInDays)
    }
    var limit = Bank.getAccountById(accountId)!!.limit
        set(value) {
            if (value.toDouble() >= 0)
                field = value
        }
}