import java.text.SimpleDateFormat
import java.util.Calendar

enum class SexEnum { Man, Woman, NonBinary }

class ClientPerson (private val surname_: String,
                    private val firstName_: String,
                    private val patronymic_: String?,
                    internal val passport: String,
                    private val birthDate_: Calendar,
                    private val sex_: SexEnum,
                    phoneNumber_: String,
                    address_: String) : Client(phoneNumber_, address_) {
    val name get() = "$firstName_ ${patronymic_ ?: ""} ${surname_[0]}."
    val birthDate: String get() = SimpleDateFormat("d/M/Y").format(birthDate_.time)
    val sex get() = sex_.name
}