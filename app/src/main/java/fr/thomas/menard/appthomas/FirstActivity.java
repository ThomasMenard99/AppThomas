package fr.thomas.menard.appthomas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class FirstActivity extends AppCompatActivity {

    Button btnFirstAct;

    TextInputEditText txtPatient;

    RadioGroup radioGroup;
    RadioButton rBtn1, rBtn2, rBtn3, rBtn4;

    String preset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        init();
        listenAct();
        listenRadioGroup();
    }

    private void init(){

        txtPatient = findViewById(R.id.APatient_txtIdPatient);

        btnFirstAct = findViewById(R.id.btnFirstAct);

        radioGroup = findViewById(R.id.RadioGroupFirst);

        File cameraDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        if (cameraDirectory.exists()) {
            File[] cameraFiles = cameraDirectory.listFiles();

            if (cameraFiles != null) {
                for (File file : cameraFiles) {
                    Log.d("TEST", ""+file);
                }
            }
        }

    }

    private void listenAct()
    {
        btnFirstAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stxtPatient = txtPatient.getText().toString();
                if(stxtPatient.equals(""))
                    Toast.makeText(FirstActivity.this, "Patient ID not correct", Toast.LENGTH_LONG).show();
                else {
                    if(preset == null){
                        Toast.makeText(FirstActivity.this, "Select a measurement", Toast.LENGTH_LONG).show();
                    }else{
                        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                        intent.putExtra("patientID", stxtPatient);
                        intent.putExtra("preset", preset);
                        startActivity(intent);
                    }

                }
            }
        });
    }


    private void listenRadioGroup(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radioBtn1)
                    preset = "Norn";
                else if (i == R.id.radioBtn2) {
                    preset = "MS";
                }
                else if (i == R.id.radioBtn3) {
                    preset = "Parkinson";
                }
                else if (i == R.id.radioBtn4) {
                    preset = "Stroke";
                }
            }
        });
    }


}