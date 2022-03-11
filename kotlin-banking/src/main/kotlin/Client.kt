import java.math.BigDecimal

var last_id = 0L
open class Client (private val phoneNumber_: String, private val address_: String) {
    val id = ++last_id
    val phoneNumber get() = "+7(***)***-**-${phoneNumber_.takeLast(4)}"
    fun createBankAccount(currency: Currency, limit_: BigDecimal? = null) = BankAccount(id, currency, limit_)
}