package com.webengage.survey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.webengage.sdk.android.WebEngage;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        getSupportActionBar().setTitle("WebEngage Survey");

        mSharedPrefs = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);

        String userid = mSharedPrefs.getString(Constants.CUID, "");
        if (!userid.isEmpty()) {
            ((Button) findViewById(R.id.login_button)).setText("LOGOUT");
        } else {
            ((Button) findViewById(R.id.login_button)).setText("LOGIN");
        }
        ((EditText) findViewById(R.id.userid_edittext)).setText(userid);
    }

    public void login(View view) {
        if (!mSharedPrefs.getString(Constants.CUID, "").isEmpty()) {
            // Logout
            mSharedPrefs.edit().putString(Constants.CUID, "").apply();
            ((EditText) findViewById(R.id.userid_edittext)).setText("");
            ((Button) findViewById(R.id.login_button)).setText("LOGIN");

            WebEngage.get().user().logout();
        } else {
            String userid = ((EditText) findViewById(R.id.userid_edittext)).getText().toString();
            if (!userid.isEmpty()) {
                // Login
                mSharedPrefs.edit().putString(Constants.CUID, userid).apply();
                ((EditText) findViewById(R.id.userid_edittext)).setText(userid);
                ((Button) findViewById(R.id.login_button)).setText("LOGOUT");

                WebEngage.get().user().login(userid);
            }
        }
    }

    public void showSurvey(View view) {
        String url = ((EditText) findViewById(R.id.url_edittext)).getText().toString();
        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.webengage.com?url=" + url), MainActivity.this, SurveyDialogActivity.class);
            startActivity(intent);
        }
    }
}
