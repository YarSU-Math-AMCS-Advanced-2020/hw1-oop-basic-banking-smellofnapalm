import java.math.BigDecimal

enum class Currency { USD, EUR, RUB }

class BankAccount(private val ownerId: Int, private val currency_: Currency, limit_: BigDecimal? = null) {
    val id = this.hashCode()
    var amount = BigDecimal(0)
    val currency get() = currency_.name
    var limit: BigDecimal? = limit_
        set(value) {
            if (value == null)
                field = value
            else if (value.toDouble() > 0)
                field = value
        }
}