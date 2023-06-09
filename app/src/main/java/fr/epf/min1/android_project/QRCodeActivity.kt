package fr.epf.min1.android_project


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import fr.epf.min1.android_project.databinding.PreviewViewBinding
import fr.epf.min1.android_project.home.MoviesFavoriteActivity
import fr.epf.min1.android_project.home.MyMainActivity
import fr.epf.min1.android_project.home.PAGE_FAVORITES
import fr.epf.min1.android_project.home.PAGE_MAIN
import fr.epf.min1.android_project.home.PAGE_QR_CODE
import fr.epf.min1.android_project.home.PAGE_SEARCH
import java.util.concurrent.Executors


private const val CAMERA_PERMISSION_REQUEST_CODE = 1
val Page = PAGE_QR_CODE
@ExperimentalGetImage
class QRCodeActivity : AppCompatActivity() {

    private lateinit var binding: PreviewViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreviewViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (hasCameraPermission()) bindCameraUseCases()
        else requestPermission()
    }

    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindCameraUseCases()
        } else {
            Toast.makeText(this,
                "Camera permission required",
                Toast.LENGTH_LONG
            ).show()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
                }

            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_PDF417
            ).build()

            val scanner = BarcodeScanning.getClient(options)

            val analysisUseCase = ImageAnalysis.Builder()
                .build()

            analysisUseCase.setAnalyzer(
                Executors.newSingleThreadExecutor()
            ) { imageProxy ->
                processImageProxy(scanner, imageProxy)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    analysisUseCase)
            } catch (illegalStateException: IllegalStateException) {
                Log.e(TAG, illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                Log.e(TAG, illegalArgumentException.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {

        imageProxy.image?.let { image ->
            val inputImage =
                InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodeList ->
                    val barcode = barcodeList.getOrNull(0)

                    barcode?.rawValue?.let { value ->
                        val intent = Intent(this, DetailsFilmActivity::class.java)
                        Toast.makeText(this@QRCodeActivity, value, Toast.LENGTH_SHORT).show()
                        intent.putExtra("film_id", value.toInt())
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {


                    Log.e(TAG, it.message.orEmpty())
                }.addOnCompleteListener {

                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }

    companion object {
        val TAG: String = "EPF"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        updateMenuItems(menu);
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateMenuItems(menu: Menu) {
        val homeItem = menu.findItem(R.id.home)
        val searchItem = menu.findItem(R.id.search_movie)
        val favItem = menu.findItem(R.id.fav_movies)
        val qrCodeItem = menu.findItem(R.id.action_scan_qr_code)
        when (Page) {

            PAGE_MAIN -> {
                homeItem.isVisible = false
                searchItem.isVisible = true
                favItem.isVisible = true
                qrCodeItem.isVisible = true
            }

            PAGE_SEARCH -> {
                homeItem.isVisible = true
                searchItem.isVisible = false
                favItem.isVisible = true
                qrCodeItem.isVisible = true
            }

            PAGE_FAVORITES -> {
                homeItem.isVisible = true
                searchItem.isVisible = true
                favItem.isVisible = false
                qrCodeItem.isVisible = true
            }

            PAGE_QR_CODE -> {
                homeItem.isVisible = true
                searchItem.isVisible = true
                favItem.isVisible = true
                qrCodeItem.isVisible = false
            }
        }
    }
    @ExperimentalGetImage
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.home -> {
                Toast.makeText(this,"Accueil",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MyMainActivity::class.java)
                startActivity(intent)
            }
            R.id.search_movie -> {
                Toast.makeText(this,"Recherche",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ListFilmActivity::class.java)
                startActivity(intent)
            }
            R.id.fav_movies -> {
                Toast.makeText(this,"Favoris",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MoviesFavoriteActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
