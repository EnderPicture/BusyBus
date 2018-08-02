package com.enderpicture.android.busybus;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    public static final String THEME_KEY = "theme";
    public static final String AUTO = "AUTO";
    public static final String DARK = "DARK";
    public static final String LIGHT = "LIGHT";
    public static final String SETTINGS_FILE_NAME = "settings";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        RadioGroup radioGroup = findViewById(R.id.theme_group);

        if (sharedPreferences.contains(THEME_KEY)) {
            String s = sharedPreferences.getString(THEME_KEY,"");

            if (s.equals(AUTO)) {
                radioGroup.check(R.id.auto);
            } else if (s.equals(LIGHT)) {
                radioGroup.check(R.id.light);
            } else if (s.equals(DARK)) {
                radioGroup.check(R.id.dark);
            }
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                SharedPreferences.Editor editor = sharedPreferences.edit();


                switch (checkedId) {
                    case R.id.auto:
                        editor.putString(THEME_KEY, AUTO);
                        break;

                    case R.id.light:
                        editor.putString(THEME_KEY, LIGHT);
                        break;

                    case R.id.dark:
                        editor.putString(THEME_KEY, DARK);
                        break;

                }

                editor.apply();
            }
        });

    }
}
