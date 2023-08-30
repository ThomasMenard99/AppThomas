package fr.thomas.menard.appthomas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity{

    private FTP mainClassFTP;
    private static final String host = "192.168.1.102";

    private String patientID;
    private String preset;

    private static final int port = 21;
    private static final String user = "ftp_user";
    private static final String password = "Cefir.2022";

    File dir;
    private String PATH_FILE;
    private static String PATH_NAS = "./videos/";
    private File folderSRC;



    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;

    private static final int REQUEST_PERMISSION = 1;

    private static int CAMERA_PERMISSION = 100;
    private static int VIDEO_RECORD = 101;
    private Uri videoPath;
    private String svideoPath;

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private Uri videoUri;
    private Button startRecordingButton;
    private Button stopRecordingButton;
    private TextView txtTransfer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mainClassFTP = new FTP();

        startRecordingButton = findViewById(R.id.startRecordingButton);
        stopRecordingButton = findViewById(R.id.stopRecordingButton);
        txtTransfer = findViewById(R.id.txtTranfertFTP);

        startVideoRecording();

        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideoRecording();
            }
        });

        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arrêter l'enregistrement de la vidéo
                stopVideoRecording();
            }
        });

        // Demander les autorisations nécessaires
        if (!checkPermissions()) {
            requestPermissions();
        }


        Intent intent = getIntent();
        patientID = intent.getStringExtra("patientID");
        preset = intent.getStringExtra("preset");
    }

    private void startVideoRecording() {
        File videoFile = new File(getApplicationContext().getExternalFilesDir(null), "MyVideo.mp4");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, "MyVideo"); // Nom de la vidéo
        values.put(MediaStore.Video.Media.DESCRIPTION, "Video recorded by my app"); // Description
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath()); // Chemin de stockage personnalisé
        videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        Log.d("TEST", "video recording path" + videoUri);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri); // Spécifie l'URI de la vidéo
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void stopVideoRecording() {
        boolean co = mainClassFTP.connect(host, user, password, port,videoUri, getApplicationContext(), patientID, preset);
        if(co) {
            txtTransfer.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(RecordActivity.this, "Sorry, we got an issue. Check your connection", Toast.LENGTH_SHORT).show();
        }

        //Intent intent = new Intent(getApplicationContext(), TransferActivity.class);
        //startActivity(intent);
        //finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            startRecordingButton.setVisibility(View.INVISIBLE);
            stopRecordingButton.setVisibility(View.VISIBLE);
            Log.d("TEST", "videoURI " + videoUri);
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_VIDEO_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // L'autorisation a été accordée
            } else {
                // L'autorisation a été refusée
            }
        }
    }





}