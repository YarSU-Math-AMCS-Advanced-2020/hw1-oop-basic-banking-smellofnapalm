package inside

open class Client (val phoneNumber: String, val address: String) {
    val id = this.hashCode()
}