package com.example.calories;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private Interpreter tflite;
    private final String MODEL_PATH = "calories_model.tflite";

    private Button predictButton;
    private EditText genderEditText;
    private EditText ageEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private EditText durationEditText;
    private EditText heartRateEditText;
    private EditText bodyTempEditText;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        predictButton = findViewById(R.id.btnPredict);
        genderEditText = findViewById(R.id.etGender);
        ageEditText = findViewById(R.id.etAge);
        heightEditText = findViewById(R.id.etHeight);
        weightEditText = findViewById(R.id.etWeight);
        durationEditText = findViewById(R.id.etDuration);
        heartRateEditText = findViewById(R.id.etHeartRate);
        bodyTempEditText = findViewById(R.id.etBodyTemp);
        resultTextView = findViewById(R.id.tvResult);

        // Load the TensorFlow Lite model
        try {
            MappedByteBuffer tfliteModel = loadModelFile();
            tflite = new Interpreter(tfliteModel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set click listener for the predict button
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictCaloriesBurnt();
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(MODEL_PATH);
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long length = fileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }

    private void predictCaloriesBurnt() {
        // Get user inputs
        int gender = Integer.parseInt(genderEditText.getText().toString());
        float age = Float.parseFloat(ageEditText.getText().toString());
        float height = Float.parseFloat(heightEditText.getText().toString());
        float weight = Float.parseFloat(weightEditText.getText().toString());
        float duration = Float.parseFloat(durationEditText.getText().toString());
        float heartRate = Float.parseFloat(heartRateEditText.getText().toString());
        float bodyTemp = Float.parseFloat(bodyTempEditText.getText().toString());

        // Perform inference using the TensorFlow Lite model
        float[][] input = {{gender, age, height, weight, duration, heartRate, bodyTemp}};
        float[][] output = new float[1][1];
        tflite.run(input, output);

        // Display the predicted calories burnt
        float predictedCalories = output[0][0];
        String outputText = "calories burnt is " +
                ": " + predictedCalories;
        resultTextView.setText(outputText);
    }
}
