import java.util.Calendar

object Bank {
    var allPersonalClients = mutableListOf<ClientPerson>()
    var allLegalClients = mutableListOf<ClientLegal>()
    var allAccounts = mutableListOf<BankAccount>()
    fun addClientPerson(surname_: String, firstName_: String, patronymic_: String?, passport: String, birthDate_: Calendar, sex_: SexEnum, phoneNumber_: String, address_: String) {
        val newPerson = ClientPerson(surname_, firstName_, patronymic_, passport, birthDate_, sex_, phoneNumber_, address_)
        if (newPerson !in allPersonalClients) allPersonalClients.add(newPerson)
    }
    fun addClientLegal(name: String, TIN: String, establishing_date_: Calendar, phoneNumber_: String, address_: String) {
        val newLegal = ClientLegal(name, TIN, establishing_date_, phoneNumber_, address_)
        if (newLegal !in allLegalClients) allLegalClients.add(newLegal)
    }
    fun deleteClientById(id: Long) {
        allPersonalClients.removeIf {client -> client.id == id}
        allLegalClients.removeIf {client -> client.id == id}
        // TODO: Remove all connected bank accounts and cards
    }

    fun getClientByPassport(passport: String) = allPersonalClients.find {client -> client.passport == passport}
    fun getClientByTIN(TIN: String) = allLegalClients.find { client -> client.TIN == TIN }
}