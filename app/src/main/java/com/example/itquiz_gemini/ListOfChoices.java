package com.example.itquiz_gemini;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;

public class ListOfChoices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_choices);

        // Setup button click listeners for each quiz category
        Button btnSubject1 = findViewById(R.id.btn_subject1);
        btnSubject1.setOnClickListener(v -> startQuiz("Computer Programming", R.raw.computerprogramingquiz));

        Button btnSubject2 = findViewById(R.id.btn_subject2);
        btnSubject2.setOnClickListener(v -> startQuiz("Web Development", R.raw.webdevelopmentquiz));

        Button btnSubject3 = findViewById(R.id.btn_subject3);
        btnSubject3.setOnClickListener(v -> startQuiz("Cyber Security", R.raw.cybersecurityquiz));

        Button btnSubject4 = findViewById(R.id.btn_subject4);
        btnSubject4.setOnClickListener(v -> startQuiz("Networking", R.raw.networkingquiz));

    }

    private void startQuiz(String category, int jsonFileId) {
        Intent intent = new Intent(ListOfChoices.this, playActivity.class);
        intent.putExtra("quizCategory", category);

        // Load questions from the selected JSON file
        ArrayList<String> questionList = loadQuestionsFromJSON(jsonFileId);

        // Pass question data as intent extra
        intent.putStringArrayListExtra("quizQuestions", questionList);
        startActivity(intent);
    }

    private ArrayList<String> loadQuestionsFromJSON(int jsonFileId) {
        ArrayList<String> questions = new ArrayList<>();
        try {
            // Load JSON file from res/raw directory
            InputStream is = getResources().openRawResource(jsonFileId);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            // Parse JSON content
            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            // Add each question object as a JSON string to the list
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                questions.add(obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
