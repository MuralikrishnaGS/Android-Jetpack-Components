package com.samruddhi.qrcodescan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.samruddhi.qrcodescan.model.BarCode
import com.samruddhi.qrcodescan.utils.TimeConverter
import kotlinx.coroutines.CoroutineScope

@Database(entities = [BarCode::class], version = 1)
@TypeConverters(TimeConverter::class)
abstract class BarCodeHistoryDb : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        var INSTANCE: BarCodeHistoryDb? = null

        fun getDatabaseInstance(context: Context, scope: CoroutineScope): BarCodeHistoryDb {
            val temporaryInstance = INSTANCE
            if (temporaryInstance != null) {
                return temporaryInstance
            }

            synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, BarCodeHistoryDb::class.java, "barcodehistorydb")
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}