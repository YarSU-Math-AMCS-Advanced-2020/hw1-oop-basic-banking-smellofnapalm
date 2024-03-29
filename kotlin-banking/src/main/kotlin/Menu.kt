import inside.Interactor

object Menu {
    private val listOfOptions = listOf(
        "Зарегистрировать человека в банке",
        "Зарегистрировать компанию в банке",
        "Открыть счет",
        "Закрыть счет",
        "Установить лимит на счет или карту (единый на снятие и переводы)",
        "Открыть карту",
        "Закрыть карту",
        "Привязать карту к другому счету",
        "Совершить перевод (по номеру телефона)",
        "Совершить перевод (по номеру карты)",
        "Снять деньги",
        "Положить деньги",
        "Вывести список всех клиентов",
        "Вывести список всех счетов",
        "Вывести список всех карт",
        "Вывести список всех банкоматов",
        "Вывести список всех транзакций")
    private fun chooser(index: Int) {
        if (index < 1 || index > listOfOptions.size) return
        when(index) {
            1 -> Interactor.registerPersonalClient()
            2 -> Interactor.registerLegalClient()
            3 -> Interactor.openAccount()
            4 -> Interactor.closeAccount()
            5 -> Interactor.setLimit()
            6 -> Interactor.openCard()
            7 -> Interactor.closeCard()
            8 -> Interactor.rebindCard()
            9 -> Interactor.makeTransaction()
            10 -> Interactor.makeTransactionWithCard()
            11 -> Interactor.cashWithdrawal()
            12 -> Interactor.cashIn()
            13 -> Interactor.printAllClients()
            14 -> Interactor.printAllAccount()
            15 -> Interactor.printAllCards()
            16 -> Interactor.printAllCashpoints()
            17 -> Interactor.printAllTransactions()
        }
    }
    private fun printMenu() {
        println("0. Выйти из банка")
        for (i in listOfOptions.indices) {
            println("${i+1}. ${listOfOptions[i]}")
        }
    }
    fun mainCycle() {
        println("Добро пожаловать в банк \"Две копейки\"! Здесь ваши средства будут в безопасности!")
        while (true) {
            printMenu()
            val key = readln().toInt()
            if (key == 0) break
            chooser(key)
        }
    }
}