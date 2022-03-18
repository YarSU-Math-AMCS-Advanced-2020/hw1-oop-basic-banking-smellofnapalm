package inside

import java.text.SimpleDateFormat
import java.util.*

class ClientLegal (internal val name: String,
                   internal val TIN: String,
                   private val establishing_date_: Calendar,
                   phoneNumber: String,
                   address: String) : Client(phoneNumber, address) {
    private val establishingDate: String get() = SimpleDateFormat("d/M/Y").format(establishing_date_.time)
    override fun toString() = "Компания называется $name, ее дата основания $establishingDate, телефонный номер $phoneNumber и адрес $address"
}