package com.samruddhi.qrcodescan.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.samruddhi.qrcodescan.model.BarCode

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBarcodeToDb(barcode: BarCode)

    @Delete
    fun deleteBarCode(barcode: BarCode)

    @Query("SELECT * from barcode_table ORDER BY timeTaken")
    fun getAllBarCodesFromDb(): LiveData<List<BarCode>>

}