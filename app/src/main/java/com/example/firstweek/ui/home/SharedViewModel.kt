package com.example.firstweek.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    private val _selectedContact = MutableLiveData<Contact>()
    val selectedContact: LiveData<Contact> get() = _selectedContact

    fun setContacts(contacts: List<Contact>) {
        _contacts.value = contacts
    }

    fun selectContact(contact: Contact) {
        _selectedContact.value = contact
    }
}

