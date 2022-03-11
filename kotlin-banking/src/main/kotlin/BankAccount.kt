import java.math.BigDecimal

enum class Currency { USD, EUR, RUB }

class BankAccount(private val owner_id: Long, private val currency_: Currency, limit_: BigDecimal? = null) {
    val currency get() = currency_.name
    var limit: BigDecimal? = limit_
        set(value) {
            if (value!!.toDouble() >= 0)
                field = value
        }
}