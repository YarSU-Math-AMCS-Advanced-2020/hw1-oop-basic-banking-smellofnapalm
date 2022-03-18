package inside

import java.text.SimpleDateFormat
import java.util.*

enum class SexEnum { Man, Woman, NonBinary }

class ClientPerson (private val surname: String,
                    private val firstName: String,
                    private val patronymic: String?,
                    internal val passport: String,
                    private val _birthDate: Calendar,
                    private val _sex: SexEnum,
                    phoneNumber: String,
                    address: String) : Client(phoneNumber, address) {
    internal val name get() = "$firstName ${patronymic ?: ""} ${surname[0]}."
    private val birthDate: String get() = SimpleDateFormat("d/M/Y").format(_birthDate.time)
    private val sex get() = _sex.name
    override fun toString() = "Человека зовут $name, он родился $birthDate, его пол $sex, номер телефона $phoneNumber и адрес $address"
}