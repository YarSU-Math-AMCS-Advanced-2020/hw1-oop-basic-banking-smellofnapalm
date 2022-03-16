import java.math.BigDecimal

enum class Currency { USD, EUR, RUB }

class BankAccount(private val ownerId: Int, val currency_: Currency, limit_: BigDecimal? = null) {
    val id = this.hashCode()
    var amount = BigDecimal(0)
    val currency get() = currency_.name
    val isPersonalAccount = Bank.getPersonalClientById(ownerId) != null
    override fun toString() = "Счет принадлежит ${if (isPersonalAccount) "человеку" else "компании"} с id $ownerId, на нем лежит $amount $currency, его лимит $limit $currency"
    var limit: BigDecimal = limit_ ?: BigDecimal(Int.MAX_VALUE)
        set(value) {
            if (value.toDouble() > 0)
                field = value
        }
}