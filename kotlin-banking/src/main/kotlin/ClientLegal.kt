import java.text.SimpleDateFormat
import java.util.*

class ClientLegal (val name: String,
                   internal val TIN: String,
                   private val establishing_date_: Calendar,
                   phoneNumber: String,
                   address: String) : Client(phoneNumber, address) {
    val establishingDate: String get() = SimpleDateFormat("d/M/Y").format(establishing_date_.time)
    override fun toString() = "Компания называется $name, ее TIN $TIN, дата основания $establishingDate, телефонный номер $phoneNumber, адрес $address"
}