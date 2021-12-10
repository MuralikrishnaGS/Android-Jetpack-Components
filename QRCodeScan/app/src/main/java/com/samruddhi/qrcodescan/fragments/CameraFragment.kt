package com.samruddhi.qrcodescan.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.button.MaterialButton
import com.samruddhi.qrcodescan.R
import com.samruddhi.qrcodescan.databinding.FragmentCameraBinding
import com.samruddhi.qrcodescan.model.BarCode
import kotlinx.android.synthetic.main.top_action_bar_in_live_camera.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.reflect.Field
import java.util.*
import kotlin.math.min


class CameraFragment : Fragment(), View.OnClickListener {

    private lateinit var cameraFragmentBinding: FragmentCameraBinding
    private var isFlashOn: Boolean = false

    private val REQUEST_CODE_CAMERA_PERMISSION = 1000

    private var surfaceView: SurfaceView? = null
    private var constraintLayout: ConstraintLayout? = null
    private var textViewTypeValue: TextView? = null
    private var textViewContentValue: TextView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private var displayMetrics: DisplayMetrics? = null

    private var shareButton: MaterialButton? = null
    private var exitButton: MaterialButton? = null

    private var barCode: BarCode? = null

    private var deviceWidth = 0
    private var deviceHeight = 0


    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(CameraFragmentViewModel::class.java) }

    @Suppress("DEPRECATION")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*Hide both Status bar and actionBar*/
//        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN
//        flags =
//            flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        requireActivity().window.decorView.systemUiVisibility = flags

        /*Hide Status bar but not actionBar*/
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    @Suppress("DEPRECATION")
    override fun onDetach() {
        super.onDetach()

        /*Shows both but adds extra status bar*/
//        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        /*Shows both but screen conetent alignemt mismatch*/
//        requireActivity().window.decorView.systemUiVisibility =
//            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        /*Show Status bar and actionBar*/
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_VISIBLE)
        requireActivity().window.decorView.invalidate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_camera,
            container,
            false
        )
        cameraFragmentBinding.actionBarCamera.close_button.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_cameraFragment_to_mainFragment)
        }
        cameraFragmentBinding.actionBarCamera.flash_button.setOnClickListener { toggleFlash() }
        cameraFragmentBinding.actionBarCamera.gallery_button.setOnClickListener { loadBarCodeFromGallery() }

        surfaceView = cameraFragmentBinding.surfaceView
        constraintLayout = cameraFragmentBinding.constraintLayout
        textViewTypeValue = cameraFragmentBinding.textViewTypeValue
        textViewContentValue = cameraFragmentBinding.textViewContentValue
        displayMetrics = resources.displayMetrics

        shareButton = cameraFragmentBinding.shareButton
        exitButton = cameraFragmentBinding.exitButton

        shareButton!!.setOnClickListener(this)
        exitButton!!.setOnClickListener(this)

        getDeviceWidthHeight()

        return cameraFragmentBinding.root
    }

    private fun getDeviceWidthHeight() {
        deviceHeight = displayMetrics!!.heightPixels
        deviceWidth = displayMetrics!!.widthPixels
    }

    @Suppress("DEPRECATION")
    private fun toggleFlash() {
        val camera = getCamera(cameraSource!!)
        if (camera != null) {
            try {
                val param: Camera.Parameters = camera.parameters
                param.flashMode =
                    if (!isFlashOn) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF
                camera.parameters = param
                isFlashOn = !isFlashOn
                if (isFlashOn) {
                    Toast.makeText(context, "Flash Switched ON", Toast.LENGTH_SHORT).show()
                    cameraFragmentBinding.actionBarCamera.flash_button.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_flash_on,
                            null
                        )
                    )
                } else {
                    Toast.makeText(context, "Flash Switched Off", Toast.LENGTH_SHORT).show()
                    cameraFragmentBinding.actionBarCamera.flash_button.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_flash_off,
                            null
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadBarCodeFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(intent, "Select QR Code"), 6)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6 && data != null) {
            launchMediaScanIntent(data.data!!)
        }
    }

    private fun launchMediaScanIntent(imageUri: Uri) {
        val bitmap = decodeBitmapUri(imageUri)

        if (barcodeDetector!!.isOperational && bitmap != null) {
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val barCode = barcodeDetector!!.detect(frame)

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val barCodeResult = viewModel.getResult(barCode.valueAt(0))
                    showResult(barCodeResult[0], barCodeResult[1], barCodeResult[2])
                }
            }
        }
    }

    private fun decodeBitmapUri(uri: Uri): Bitmap? {
        val targetW = 600
        val targetH = 600
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            requireContext().contentResolver.openInputStream(uri),
            null,
            bmOptions
        )
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight
        val scaleFactor = min(photoW / targetW, photoH / targetH)
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        return BitmapFactory.decodeStream(
            requireContext().contentResolver
                .openInputStream(uri), null, bmOptions
        )
    }

    @Suppress("DEPRECATION")
    private fun getCamera(cameraSource: CameraSource): Camera? {
        val declaredFields: Array<Field> = CameraSource::class.java.declaredFields
        for (field in declaredFields) {
            if (field.type === Camera::class.java) {
                field.isAccessible = true
                try {
                    return field.get(cameraSource) as Camera
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
                break
            }
        }
        return null
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.CAMERA
        )

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CAMERA_PERMISSION -> cameraSource!!.start(surfaceView!!.holder)
        }
    }

    override fun onResume() {
        super.onResume()
        setBarcodeDetector()
        setCameraSource()
        setSurfaceView()
        setBarcodeProcessor()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource!!.release()
    }

    private fun setBarcodeDetector() {
        barcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
    }

    private fun setCameraSource() {
        cameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
            .setRequestedPreviewSize(deviceHeight, deviceWidth)
            .setAutoFocusEnabled(true)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun setSurfaceView() {
        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    when {
                        isPermissionsGranted() -> cameraSource!!.start(surfaceView!!.holder)

                        shouldShowRequestPermissionRationale() -> Toast.makeText(
                            context,
                            getString(R.string.permission_request),
                            Toast.LENGTH_LONG
                        ).show()

                        else -> {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.CAMERA),
                                REQUEST_CODE_CAMERA_PERMISSION
                            )
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })
    }

    private fun setBarcodeProcessor() {
        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    context,
                    "Stopped scanning to avoid memory leaks!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barCodes = detections.detectedItems
                if (barCodes.size() != 0) {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            cameraSource!!.stop()
                            val barCodeResult = viewModel.getResult(barCodes.valueAt(0))
                            showResult(barCodeResult[0], barCodeResult[1], barCodeResult[2])
                        }
                    }
                }
            }
        })
    }

    private suspend fun showResult(
        barCodeTypeIntValue: String,
        barCodeType: String,
        barCodeValue: String
    ) {
        withContext(Dispatchers.Main) {
            cameraFragmentBinding.apply {
                constraintLayout.visibility = View.VISIBLE
                textViewTypeValue.text = barCodeType
                textViewContentValue.text = barCodeValue

                barCode = BarCode(barCodeTypeIntValue.toInt(), barCodeValue, Date())
                viewModel.insertBarCode(barCode!!)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onClick(view: View) {
        when(view.id) {
            cameraFragmentBinding.shareButton.id -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_TEXT, barCode!!)
                intent.type = "*/*"
                startActivity(Intent.createChooser(intent, "Share with"))
            }
            cameraFragmentBinding.exitButton.id -> {
                constraintLayout!!.visibility = View.GONE
                try {
                    when {
                        isPermissionsGranted() -> cameraSource!!.start(surfaceView!!.holder)

                        shouldShowRequestPermissionRationale() -> Toast.makeText(
                            context,
                            getString(R.string.permission_request),
                            Toast.LENGTH_LONG
                        ).show()

                        else -> {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.CAMERA),
                                REQUEST_CODE_CAMERA_PERMISSION
                            )
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}