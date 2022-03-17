import inside.Client
import java.text.SimpleDateFormat
import java.util.*

enum class SexEnum { Man, Woman, NonBinary }

class ClientPerson (private val surname: String,
                    private val firstName: String,
                    private val patronymic: String?,
                    internal val passport: String,
                    private val birthDate_: Calendar,
                    private val sex_: SexEnum,
                    phoneNumber: String,
                    address: String) : Client(phoneNumber, address) {
    val name get() = "$firstName ${patronymic ?: ""} ${surname[0]}."
    val birthDate: String get() = SimpleDateFormat("d/M/Y").format(birthDate_.time)
    val sex get() = sex_.name
    override fun toString() = "Человека зовут $name, он родился $birthDate, его пол $sex, номер телефона $phoneNumber и адрес $address"
}