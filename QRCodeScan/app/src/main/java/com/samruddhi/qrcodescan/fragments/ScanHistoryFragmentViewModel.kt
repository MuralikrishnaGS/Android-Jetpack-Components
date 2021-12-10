package com.samruddhi.qrcodescan.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.samruddhi.qrcodescan.database.BarCodeHistoryDb
import com.samruddhi.qrcodescan.database.BarcodeHistoryRepository
import com.samruddhi.qrcodescan.model.BarCode
import kotlinx.coroutines.launch

class ScanHistoryFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BarcodeHistoryRepository

    val allHistoryItems: LiveData<List<BarCode>>
    var isEmpty: Boolean?

    init {
        val historyDao =
            BarCodeHistoryDb.getDatabaseInstance(application.applicationContext, viewModelScope)
                .historyDao()
        repository = BarcodeHistoryRepository(historyDao)
        allHistoryItems = repository.allHistoryItems
        isEmpty = allHistoryItems.value?.isEmpty()

    }

    fun deleteBarCode(barcode: BarCode) {
        viewModelScope.launch {
            repository.deleteBarCodeToDb(barcode)
        }
    }
}