package com.example.itquiz_gemini;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    private TextView scoreTextView, recommendationsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results); // Create an XML layout for this activity

        // Initialize UI elements
        scoreTextView = findViewById(R.id.score_text_view);
        recommendationsTextView = findViewById(R.id.recommendations_text_view);

        // Get data from Intent
        Intent intent = getIntent();
        int userScore = intent.getIntExtra("userScore", 0);
        String feedback = intent.getStringExtra("feedback");

        // Display score and recommendations
        scoreTextView.setText("Your Score: " + userScore + "/30");
        recommendationsTextView.setText("Suggestions for improvement:\n" + feedback);
    }
}
