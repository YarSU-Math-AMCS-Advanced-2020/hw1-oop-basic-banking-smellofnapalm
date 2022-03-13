import java.math.BigDecimal

open class Client (private val phoneNumber_: String, private val address_: String) {
    val id = this.hashCode()
    val phoneNumber get() = "+7(***)***-**-${phoneNumber_.takeLast(4)}"
    fun createBankAccount(currency: Currency, limit_: BigDecimal? = null) = BankAccount(id, currency, limit_)
}