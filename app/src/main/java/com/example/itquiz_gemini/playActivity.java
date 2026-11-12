package com.example.itquiz_gemini;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.FutureCallback;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

public class playActivity extends AppCompatActivity {

    private TextView questionTextView, cptQuestionTextView, scoreTextView;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnNext;
    private ImageView imageBack;
    private ArrayList<String> questions;
    private int currentQuestionIndex = 0, userScore = 0;
    private String correctAnswer;
    private GenerativeModel generativeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // Initialize UI elements
        questionTextView = findViewById(R.id.text_question);
        cptQuestionTextView = findViewById(R.id.cpt_question);
        scoreTextView = findViewById(R.id.score_text); // New TextView for score feedback
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);
        btnNext = findViewById(R.id.btn_next);
        imageBack = findViewById(R.id.image_back);

        // Back button to return to ListOfChoices
        imageBack.setOnClickListener(v -> {
            Intent intent = new Intent(playActivity.this, ListOfChoices.class);
            startActivity(intent);
            finish();
        });

        // Set up the generative model
        generativeModel = new GenerativeModel("gemini-1.5-flash", "AIzaSyD4uG043LQQkrx2FFMvoVn8YqWZudyJrxg");
        GenerativeModelFutures modelFutures = GenerativeModelFutures.from(generativeModel);

        // Retrieve questions from intent, shuffle, and limit to 30 items
        questions = getIntent().getStringArrayListExtra("quizQuestions");
        if (questions != null) {
            Collections.shuffle(questions);
            if (questions.size() > 30) {
                questions = new ArrayList<>(questions.subList(0, 30)); // Limit to 30 questions
            }
            generateQuestion(modelFutures, questions.get(currentQuestionIndex));
        }

        // "Next" button to load next question or finish quiz
        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                generateQuestion(modelFutures, questions.get(currentQuestionIndex));

                // Change button text to "Finish" on the last question
                if (currentQuestionIndex == questions.size() - 1) {
                    btnNext.setText("Finish");
                }
            } else {
                finishQuiz(modelFutures);
            }
        });
    }

    private void generateQuestion(GenerativeModelFutures modelFutures, String questionJson) {
        try {
            JSONObject questionObj = new JSONObject(questionJson);
            String questionText = questionObj.getString("question");
            String optionA = questionObj.getJSONArray("options").getString(0);
            String optionB = questionObj.getJSONArray("options").getString(1);
            String optionC = questionObj.getJSONArray("options").getString(2);
            String optionD = questionObj.getJSONArray("options").getString(3);
            correctAnswer = questionObj.getString("correct");

            // Modify question based on difficulty using the generative model
            Content content = new Content.Builder()
                    .addText("Remake the question and make it long up to 30 words: " + questionText)
                    .build();

            ListenableFuture<GenerateContentResponse> response = modelFutures.generateContent(content);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String modifiedQuestion = result.getText();
                        runOnUiThread(() -> {
                            // Display the modified question and options
                            questionTextView.setText(modifiedQuestion);
                            btnOption1.setText(optionA);
                            btnOption2.setText(optionB);
                            btnOption3.setText(optionC);
                            btnOption4.setText(optionD);
                            cptQuestionTextView.setText((currentQuestionIndex + 1) + "/" + questions.size());

                            // Setup answer button listeners
                            setAnswerButton(btnOption1, "A");
                            setAnswerButton(btnOption2, "B");
                            setAnswerButton(btnOption3, "C");
                            setAnswerButton(btnOption4, "D");
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                }, getMainExecutor());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAnswerButton(Button button, String answer) {
        button.setOnClickListener(v -> {
            if (answer.equals(correctAnswer)) {
                userScore++;
                questionTextView.setText("Correct!");
            } else {
                questionTextView.setText("Incorrect! The correct answer was: " + correctAnswer);
            }
        });
    }

    private void finishQuiz(GenerativeModelFutures modelFutures) {
        // Create an intent to start the ResultsActivity
        Intent intent = new Intent(playActivity.this, ResultsActivity.class);
        intent.putExtra("userScore", userScore); // Pass the score to the next activity
        intent.putExtra("quizCategory", getIntent().getStringExtra("quizCategory")); // Pass the category if needed

        // Generate feedback based on user performance
        Content feedbackContent = new Content.Builder()
                .addText("Provide feedback for a score of " + userScore + "/30 in the topic " + getIntent().getStringExtra("quizCategory"))
                .build();

        ListenableFuture<GenerateContentResponse> feedbackResponse = modelFutures.generateContent(feedbackContent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(feedbackResponse, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String feedback = result.getText();
                    intent.putExtra("feedback", feedback); // Pass the feedback to the next activity
                    startActivity(intent); // Start the ResultsActivity
                    finish(); // Finish current activity
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    startActivity(intent); // Start ResultsActivity even if there’s an error
                    finish(); // Finish current activity
                }
            }, getMainExecutor());
        } else {
            startActivity(intent); // Fallback to start ResultsActivity
            finish(); // Finish current activity
        }

        btnNext.setEnabled(false); // Disable "Next" button after quiz is finished
    }
}
