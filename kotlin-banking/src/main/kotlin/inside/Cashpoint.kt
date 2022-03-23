package inside

class Cashpoint(private val isATM: Boolean) {
    val id = this.hashCode()
    override fun toString() = "${if (isATM) "Банкомат" else "Отделение банка"} с номером $id"
}