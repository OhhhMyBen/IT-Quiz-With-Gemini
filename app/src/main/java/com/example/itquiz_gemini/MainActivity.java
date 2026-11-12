package com.example.itquiz_gemini;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void main_btn(View view) {
        int id = view.getId();

        if (id == R.id.btn_play) {
            startActivity(new Intent(MainActivity.this, ListOfChoices.class));
        } else if (id == R.id.btn_setting) {
            startActivity(new Intent(MainActivity.this, settingActivity.class));
        } else if (id == R.id.btn_exit) {
            this.finishAffinity();
        }
    }

}
