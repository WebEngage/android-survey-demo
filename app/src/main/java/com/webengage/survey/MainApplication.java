package com.webengage.survey;

import android.app.Application;
import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks;
import com.webengage.sdk.android.WebEngageConfig;
import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.callbacks.StateChangeCallbacks;

/**
 * Created by ashwin on 03/08/18.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize WebEngage
        WebEngageConfig config = new WebEngageConfig.Builder()
                .setWebEngageKey("YOUR-WEBENGAGE-LICENSE-CODE")
                .setDebugMode(true)
                .setEventReportingStrategy(ReportingStrategy.BUFFER)
                .setPushSmallIcon(R.mipmap.ic_launcher_round)
                .setPushLargeIcon(R.mipmap.ic_launcher)
                .build();
        registerActivityLifecycleCallbacks(new WebEngageActivityLifeCycleCallbacks(MainApplication.this, config));

        String token = FirebaseInstanceId.getInstance().getToken();
        WebEngage.get().setRegistrationID(token);

        WebEngage.registerStateChangeCallback(new StateChangeCallbacks() {
            @Override
            public void onAnonymousIdChanged(Context context, String anonymousUserID) {
                super.onAnonymousIdChanged(context, anonymousUserID);
                if (anonymousUserID != null) {
                    getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(Constants.LUID, anonymousUserID).apply();
                }
            }
        });
    }
}
