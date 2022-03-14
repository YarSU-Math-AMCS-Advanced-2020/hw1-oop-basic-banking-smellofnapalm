import java.text.SimpleDateFormat
import java.util.*

class ClientLegal (val name: String,
                   internal val TIN: String,
                   private val establishing_date_: Calendar,
                   phoneNumber_: String,
                   address_: String) : Client(phoneNumber_, address_) {
    val establishingDate: String get() = SimpleDateFormat("d/M/Y").format(establishing_date_.time)
    override fun toString() = "Кампания называется $name, ее TIN $TIN, дата основания $establishingDate, телефонный номер $phoneNumber, адрес $address_"
}