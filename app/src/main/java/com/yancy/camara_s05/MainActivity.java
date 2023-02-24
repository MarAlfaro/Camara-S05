package com.yancy.camara_s05;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnTomarFoto;
    ImageButton btncorreo, btnwhatsapp;
    ImageView imgFoto;
    String rutaImagenes;
    File EnviarFoto;
    private static final int REQUEST_CODIGO_CAMARA=200;
    private static final int REQUEST_CODIGO_CAPTURAR_IMAGEN=300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgFoto = findViewById(R.id.imgFoto);

        btnwhatsapp = findViewById(R.id.btnWhatsapp);
        btnwhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWs = new Intent();
                intentWs.setAction(Intent.ACTION_SEND);
                Uri imageUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.yancy.camara_s05",
                        EnviarFoto);
                intentWs.putExtra(Intent.EXTRA_STREAM,  Uri.parse(String.valueOf(imageUri)));
                intentWs.setType("image/*");
                intentWs.setPackage("com.whatsapp");
                startActivity(intentWs);
            }
        });
        btncorreo = findViewById(R.id.btnCorreo);
        btncorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentCorreo = new Intent(Intent.ACTION_SEND);
                intentCorreo.setData(Uri.parse("mailto:"));
                intentCorreo.setType("image/*");
                Uri imageUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.yancy.camara_s05",
                        EnviarFoto);
                intentCorreo.putExtra(Intent.EXTRA_STREAM, imageUri);
                intentCorreo.putExtra(Intent.EXTRA_SUBJECT, "Imagen");
                intentCorreo.putExtra(Intent.EXTRA_EMAIL, new String[] {"alfaroyancy0@gmail.com"});
                    intentCorreo.putExtra(Intent.EXTRA_TEXT, "Imagen adjuntada");
                startActivity(intentCorreo);
            }
        });

        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarProcesoFotografia();
            }
        });
    }
    public void realizarProcesoFotografia(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODIGO_CAMARA);
            }
        }else{
            tomarFoto();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults ){
        if (requestCode == REQUEST_CODIGO_CAMARA){
            if(permissions.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                Toast.makeText(MainActivity.this, "Se requiere permisos para la camara", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == REQUEST_CODIGO_CAPTURAR_IMAGEN){
            //Si capturo la foto
            if(resultCode == Activity.RESULT_OK){
                imgFoto.setImageURI(Uri.parse(rutaImagenes));
                //urlEnviar = data.getData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void tomarFoto(){
        Intent  intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamara.resolveActivity(getPackageManager())!=null){
            // startActivityForResult(intentCamara, REQUEST_CODIGO_CAPTURAR_IMAGEN);
            File archivoFoto = null;
            archivoFoto = crearArchivo();
            if(archivoFoto!=null){
                Uri rutaFoto = FileProvider.getUriForFile(
                        MainActivity.this, "com.yancy.camara_s05", archivoFoto);

                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT,rutaFoto);
                startActivityForResult(intentCamara,REQUEST_CODIGO_CAPTURAR_IMAGEN);
            }
        }
    }
    private File crearArchivo(){
        String nomenclatura = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String prefijoArchivo = "APPCAM_"+nomenclatura+"_";
        File directorioImagen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File miImagen = null;
        try {
            miImagen = File.createTempFile(prefijoArchivo, ".jpg", directorioImagen);
            EnviarFoto = miImagen;
            rutaImagenes = miImagen.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return miImagen;
    }

}