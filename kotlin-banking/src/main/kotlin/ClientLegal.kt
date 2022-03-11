import java.text.SimpleDateFormat
import java.util.Calendar

class ClientLegal (val name: String,
                   internal val TIN: String,
                   private val establishing_date_: Calendar,
                   phoneNumber_: String,
                   address_: String) : Client(phoneNumber_, address_) {
    val establishingDate: String get() = SimpleDateFormat("d/M/Y").format(establishing_date_.time)

}