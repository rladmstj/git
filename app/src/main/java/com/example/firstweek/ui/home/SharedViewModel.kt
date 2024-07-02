package com.example.firstweek.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedContact = MutableLiveData<Contact?>()
    val selectedContact: LiveData<Contact?> get() = _selectedContact

    private val _deletedContact = MutableLiveData<Contact?>()
    val deletedContact: LiveData<Contact?> get() = _deletedContact

    fun selectContact(contact: Contact) {
        _selectedContact.value = contact
    }

    fun deleteContact(contact: Contact) {
        _deletedContact.value = contact
        _selectedContact.value = null
    }

    fun clearDeletedContact() {
        _deletedContact.value = null
    }

    fun updateContact(oldContact: Contact, newContact: Contact) {
        _selectedContact.value = newContact
        // Update the contact in the list in HomeFragment
        _deletedContact.value = oldContact
    }
}
