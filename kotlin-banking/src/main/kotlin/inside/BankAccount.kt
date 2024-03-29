package inside

import java.math.BigDecimal

enum class Currency { USD, EUR, RUB }

class BankAccount(internal val name: String, internal val ownerId: Int, private val currency_: Currency, limit_: BigDecimal? = null) {
    internal val id = this.hashCode()
    internal var amount = BigDecimal(0)
    internal val currency get() = currency_.name
    internal val isPersonalAccount = Bank.Getter.getPersonalClientById(ownerId) != null
    override fun toString() = "Счет $name принадлежит ${if (isPersonalAccount) "человеку" else "компании"} с id $ownerId (${if (isPersonalAccount) Bank.Getter.getPersonalClientById(ownerId)!!.name else Bank.Getter.getLegalClientById(ownerId)!!.name}), на нем лежит $amount $currency, его лимит $limit $currency"
    internal var limit: BigDecimal = limit_ ?: BigDecimal(Int.MAX_VALUE)
        set(value) {
            if (value.toDouble() > 0)
                field = value
        }
}