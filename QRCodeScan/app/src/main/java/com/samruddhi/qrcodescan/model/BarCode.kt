package com.samruddhi.qrcodescan.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "barcode_table")
data class BarCode(val type: Int?, val value: String?, @PrimaryKey val timeTaken: Date): Serializable {
    //, val scanTime:Long
}