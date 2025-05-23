package com.example.opsc_poe_part_2

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

var ctrText : String = ""

class CameraTextReader : AppCompatActivity() {

    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognizeTextBtn: MaterialButton
    private lateinit var imageIv: ImageView
    private lateinit var recognizedTextEt: EditText

    private lateinit var saveEntryBtn: MaterialButton

    private companion object{
        private const val CAMERA_REQUEST_CODE =100
        private const val STORAGE_REQUEST_CODE = 101
    }
//uri of the image that we will take from camera / gallery

    private var imageUri: Uri? = null
//arrays of perission required to pck image from camera
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions:Array<String>

    private lateinit var progressDialog : ProgressDialog

    private lateinit var textRecognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_text_reader)
//init ui views
        saveEntryBtn = findViewById(R.id.saveTextBtn)
        inputImageBtn = findViewById(R.id.inputImageBtn)
        recognizeTextBtn = findViewById(R.id.recognizeTextBtn)
        imageIv = findViewById(R.id.imageIv)
        recognizedTextEt = findViewById(R.id.recognizeTextEt)
        ctrText = ""

//init arrays of permissions required fr camera
        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog =  ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)



        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

//haandle click show input dialog
        inputImageBtn.setOnClickListener{

            showInputImageDialog()
        }


        recognizeTextBtn.setOnClickListener{
            if(imageUri == null){
                showToast("Pick Image First...")
            }
            else{
                recognizeTextFromImage()
            }
        }

        saveEntryBtn.setOnClickListener {
            ctrText = recognizedTextEt.text.toString()
            val intent = Intent(this@CameraTextReader, AddDiaryEntry::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun recognizeTextFromImage() {

        progressDialog.setMessage("Preparing Image...")
        progressDialog.show()

        try{

            val inputImage = InputImage.fromFilePath(this,imageUri!!)

            progressDialog.setMessage("Recognizing text..")

            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener { text ->
                    progressDialog.dismiss()
                    val recognizedText = text.text
                    recognizedTextEt.setText(recognizedText)
                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    showToast("Failed to recognize text due to ${e.message}")
                }
        }
        catch(e: Exception){
            progressDialog.dismiss()
            showToast("Failed to prepare image due to ${e.message}")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showInputImageDialog() {

    val popupMenu = PopupMenu(this, inputImageBtn)

        popupMenu.menu.add(Menu.NONE, 1 ,1, "CAMERA")
        popupMenu.menu.add(Menu.NONE, 2,2,"GALLERY")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->

            val id = menuItem.itemId
            if(id == 1){

                if(checkCameraPermissions()){

                    pickImageCamera()
                }
                else{
                    requestCameraPermission()
                }
            }
            else if (id == 2){
                pickImageGallery()
            }

            return@setOnMenuItemClickListener true

        }
    }

    private fun pickImageGallery(){
//intent to pick image from ggallery will show all resouces from where we can pick image
        val intent = Intent(Intent.ACTION_PICK)
//set type of file we want to pick i.e image
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)

    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
//here we will recieve the image if picked
            if(result.resultCode == Activity.RESULT_OK){
//image picked
                val data = result.data
                imageUri = data!!.data
//set to imageView i.e. imageiv
                imageIv.setImageURI(imageUri)
            }
            else{
                //cancelled
                showToast("Cancelled...!")
            }
        }

    private fun pickImageCamera(){
        //get the image ready to store in mediiastore
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Sample Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->

            if(result.resultCode == Activity.RESULT_OK){


                imageIv.setImageURI(imageUri)
            }
            else{

                showToast("Cancelled..!")

            }
        }

    private fun checkStoragePermission() : Boolean{

        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermissions() : Boolean{
        val cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return cameraResult && storageResult
    }


    private fun requestStoragePermission(){
        //request storage permissions
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun requestCameraPermission(){
        //request camera permissions
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//handle permissions result
        when(requestCode){
            CAMERA_REQUEST_CODE ->{

                if(grantResults.isNotEmpty()){

                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted || storageAccepted){
                        pickImageCamera()
                    }
                    else{

                        showToast("Camera & Storage permission are required...")
                    }
                }
            }

            STORAGE_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){

                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if(storageAccepted){
                        pickImageGallery()
                    }
                    else{
                        showToast("Storage Permission is required...")
                    }
                }

            }
        }

    }

    private fun showToast(message:String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
}
//camera text reader reference
//youtube video
//author
//Atif Pervaiz
//video name
//Recognize text in images
//https://m.youtube.com/watch?v=VigFgq7h2X0