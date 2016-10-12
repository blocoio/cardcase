package io.bloco.cardcase.presentation.home;

import android.app.Activity;
import android.os.Bundle;
import io.bloco.cardcase.R;
import android.content.Context;
import android.content.Intent;

public class SettingsActivity extends Activity {

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
