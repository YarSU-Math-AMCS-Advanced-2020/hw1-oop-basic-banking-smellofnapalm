package inside

open class Client (internal val phoneNumber: String, internal val address: String) {
    internal val id = this.hashCode()
}