package pe.edu.idat.imagefire.jeremy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    private var tvImagenSeleccionada: TextView? = null
    private var btnSeleccionarImagen: Button? = null
    private var uri: Uri? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var alertDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvImagenSeleccionada = findViewById(R.id.tvImagenSeleccionada)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        btnSeleccionarImagen?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 101)
        }
    }

    private fun subirimage() {
        val reference = firebaseStorage!!.reference.child("Images").child(System.currentTimeMillis().toString() + "")
        reference.putFile(uri!!).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener { uri ->
                val model = Model()
                model.image = uri.toString()
                firebaseDatabase!!.reference.child("Imagenes").push()
                    .setValue(model).addOnSuccessListener {
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@MainActivity, "Error al Subir Imagen...", Toast.LENGTH_SHORT).show()
                    }
                alertDialog?.dismiss() // Cerrar el AlertDialog después de subir la imagen
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data?.data
            tvImagenSeleccionada?.text = "Imagen: ${uri?.path}"
            subirimage()
            alertdialog()
        }
    }

    private fun alertdialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Subiendo Imagen")
        builder.setMessage("Espere un momento por favor...")
        alertDialog = builder.create() // Asignar el AlertDialog a la variable alertDialog
        alertDialog?.setCancelable(false)
        alertDialog?.show()
    }


}