package com.samruddhi.qrcodescan.database

import androidx.lifecycle.LiveData
import com.samruddhi.qrcodescan.model.BarCode

class BarcodeHistoryRepository(private val dao: HistoryDao) {
    val allHistoryItems:LiveData<List<BarCode>> = dao.getAllBarCodesFromDb()

    suspend fun insertBarCodeToDb(barCode: BarCode){
        dao.addBarcodeToDb(barCode)
    }

    suspend fun deleteBarCodeToDb(barCode: BarCode){
        dao.deleteBarCode(barCode)
    }
}