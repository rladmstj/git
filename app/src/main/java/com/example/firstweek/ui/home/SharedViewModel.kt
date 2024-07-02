package com.example.firstweek.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _selectedContact = MutableLiveData<Contact>()
    val selectedContact: LiveData<Contact> get() = _selectedContact

    fun selectContact(contact: Contact) {
        _selectedContact.value = contact
    }
}
