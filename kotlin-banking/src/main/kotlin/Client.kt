open class Client (private val phoneNumber_: String, val address_: String) {
    val id = this.hashCode()
    val phoneNumber get() = "+7(***)***-**-${phoneNumber_.takeLast(4)}"
}