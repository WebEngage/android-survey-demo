package com.webengage.survey;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SurveyDialogActivity extends Activity {
    private static final String TAG = "WebEngage";
    private static final int CLOSE_AFTER = 5000;
    private static final int WIDTH_MARGIN = 100;
    private static final float HEIGHT_PERCENT = 0.65F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        final RelativeLayout rootLayout = new RelativeLayout(SurveyDialogActivity.this);
        RelativeLayout.LayoutParams rootLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayout.setLayoutParams(rootLayoutParams);
        rootLayout.setGravity(Gravity.CENTER);
        final WebView webView = new WebView(SurveyDialogActivity.this);
        final LinearLayout.LayoutParams webviewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(webviewLayoutParams);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "loading url: " + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Close after 5 seconds of submit
                Uri uri = Uri.parse(url);
                String action = uri.getQueryParameter("action");
                if (action != null && action.equals("success")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, CLOSE_AFTER);
                }
            }
        });

        rootLayout.addView(webView);

        setContentView(rootLayout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels - WIDTH_MARGIN;
        int height = (int) (HEIGHT_PERCENT * displayMetrics.heightPixels);
        getWindow().setLayout(width, height);

        // Get survey URL
        Uri data = getIntent().getData();
        String url = data.getQueryParameter("url");

        // Append User ID
        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        if (!sharedPrefs.getString(Constants.CUID, "").isEmpty()) {
            url += "?data(cuid)=" + sharedPrefs.getString(Constants.CUID, "");
        } else {
            url += "?data(luid)=" + sharedPrefs.getString(Constants.LUID, "");
        }

        webView.loadUrl(url);
    }
}
