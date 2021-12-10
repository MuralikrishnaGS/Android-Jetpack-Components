package com.samruddhi.qrcodescan.fragments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.vision.barcode.Barcode
import com.samruddhi.qrcodescan.database.BarCodeHistoryDb
import com.samruddhi.qrcodescan.database.BarcodeHistoryRepository
import com.samruddhi.qrcodescan.model.BarCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraFragmentViewModel(application: Application):AndroidViewModel(application) {
    private val repository: BarcodeHistoryRepository

    init {
        val historyDao =
            BarCodeHistoryDb.getDatabaseInstance(application.applicationContext, viewModelScope)
                .historyDao()
        repository = BarcodeHistoryRepository(historyDao)
    }

    fun insertBarCode(barCode: BarCode) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                Log.e("barCode", "barCode :" + barCode.value)
                repository.insertBarCodeToDb(barCode)
            }
        }
    }

    fun getResult(barCode: Barcode): Array<String> {
        val qrCodeInfo: String
        val qrCodeType: String
        val qrCodeValue: Int
        when (barCode.valueFormat) {
            Barcode.CALENDAR_EVENT -> {
                qrCodeInfo =
                    "Summary: " + barCode.calendarEvent.summary +
                            "\nDescription: " + barCode.calendarEvent.description +
                            "\nOrganizer: " + barCode.calendarEvent.organizer +
                            "\nStatus: " + barCode.calendarEvent.status +
                            "\nLocation: " + barCode.calendarEvent.location +
                            "\nStarts On: " + barCode.calendarEvent.start +
                            "\nEnds On: " + barCode.calendarEvent.end
                qrCodeType = "Calendar Event"
                qrCodeValue = Barcode.CALENDAR_EVENT
            }
            Barcode.CODE_128 or Barcode.CONTACT_INFO -> {
                qrCodeInfo =
                    "Title: " + barCode.contactInfo.title +
                            "\nName: " + barCode.contactInfo.name +
                            "\nEMail ID: " + barCode.contactInfo.emails +
                            "\nPhone: " + barCode.contactInfo.phones +
                            "\nOrganisation: " + barCode.contactInfo.organization +
                            "\nAddress: " + barCode.contactInfo.addresses +
                            "\nURL: " + barCode.contactInfo.urls
                qrCodeType = "Contact Info"
                qrCodeValue = Barcode.CODE_128 or Barcode.CONTACT_INFO
            }
            Barcode.DRIVER_LICENSE -> {
                qrCodeInfo =
                    "Document Type: " + barCode.driverLicense.documentType +
                            "\nFirst Name: " + barCode.driverLicense.firstName +
                            "\nMiddle Name: " + barCode.driverLicense.middleName +
                            "\nLast Name: " + barCode.driverLicense.lastName +
                            "\nGender: " + barCode.driverLicense.gender +
                            "\nStreet: " + barCode.driverLicense.addressStreet +
                            "\nCity: " + barCode.driverLicense.addressCity +
                            "\nState: " + barCode.driverLicense.addressState +
                            "\nZIP code: " + barCode.driverLicense.addressZip +
                            "\nLicense Num: " + barCode.driverLicense.licenseNumber +
                            "\nIssued On: " + barCode.driverLicense.issueDate +
                            "\nExpires On: " + barCode.driverLicense.expiryDate +
                            "\nBirth Date: " + barCode.driverLicense.birthDate +
                            "\nIssued By: " + barCode.driverLicense.issuingCountry
                qrCodeType = "Document"
                qrCodeValue = Barcode.DRIVER_LICENSE
            }
            Barcode.PRODUCT -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "Product"
                qrCodeValue = Barcode.PRODUCT
            }
            Barcode.ISBN -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "ISBN"
                qrCodeValue = Barcode.ISBN
            }
            Barcode.CODE_39 or Barcode.EMAIL -> {
                qrCodeInfo = ("Mail ID: " + barCode.email.address +
                        "\nSubject: " + barCode.email.subject +
                        "\nBody: " + barCode.email.body)
                qrCodeType = "EMail"
                qrCodeValue = Barcode.CODE_39 or Barcode.EMAIL
            }
            Barcode.GEO -> {
                qrCodeInfo = ("Lat: " + barCode.geoPoint.lat +
                        "\nLng: " + barCode.geoPoint.lng)
                qrCodeType = "Geo Location"
                qrCodeValue = Barcode.GEO
            }
            Barcode.CODE_93 or Barcode.PHONE -> {
                qrCodeInfo = barCode.phone.number
                qrCodeType = "Phone"
                qrCodeValue = Barcode.CODE_93 or Barcode.PHONE
            }
            Barcode.SMS -> {
                qrCodeInfo = ("Number: " + barCode.sms.phoneNumber +
                        "\nMessage: " + barCode.sms.message)
                qrCodeType = "SMS"
                qrCodeValue = Barcode.SMS
            }
            Barcode.TEXT -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "Text"
                qrCodeValue = Barcode.TEXT
            }
            Barcode.CODABAR or Barcode.URL -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "URL"
                qrCodeValue = Barcode.CODABAR or Barcode.URL
            }
            Barcode.WIFI -> {
                val encryptionType: String = when(barCode.wifi.encryptionType) {
                    1 -> "NONE"
                    2 -> "WPA/WPA2"
                    3 -> "WEP"
                    else -> "NONE"
                }
                qrCodeInfo = ("SSID: " + barCode.wifi.ssid +
                        "\nPassword: " + barCode.wifi.password +
                        "\nEncryption: " + encryptionType)
                qrCodeType = "WiFi"
                qrCodeValue = Barcode.WIFI
            }
            Barcode.DATA_MATRIX -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "Data Matrix"
                qrCodeValue = Barcode.DATA_MATRIX
            }
            Barcode.EAN_13 -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "EAN 13"
                qrCodeValue = Barcode.EAN_13
            }
            Barcode.EAN_8 -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "EAN 8"
                qrCodeValue = Barcode.EAN_8
            }
            Barcode.ITF -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "ITF"
                qrCodeValue = Barcode.ITF
            }
            Barcode.QR_CODE -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "QR Code"
                qrCodeValue = Barcode.QR_CODE
            }
            Barcode.UPC_A -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "UPC A"
                qrCodeValue = Barcode.UPC_A
            }
            Barcode.UPC_E -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "UPC E"
                qrCodeValue = Barcode.UPC_E
            }
            Barcode.PDF417 -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "PDF 417"
                qrCodeValue = Barcode.PDF417
            }
            Barcode.AZTEC -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "AZTEC"
                qrCodeValue = Barcode.AZTEC
            }
            else -> {
                qrCodeInfo = barCode.displayValue
                qrCodeType = "Other"
                qrCodeValue = Barcode.ALL_FORMATS
            }
        }
        return arrayOf(qrCodeValue.toString(), qrCodeType, qrCodeInfo)
    }
}