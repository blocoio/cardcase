package io.bloco.cardcase.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.common.di.ActivityModule;
import io.bloco.cardcase.common.di.ApplicationComponent;
import io.bloco.cardcase.presentation.common.Toolbar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

  protected Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  protected ApplicationComponent getApplicationComponent() {
    return ((AndroidApplication) getApplication()).getApplicationComponent();
  }

  protected ActivityModule getActivityModule() {
    return new ActivityModule(this);
  }

  protected void startActivityWithAnimation(Intent intent) {
    @SuppressWarnings("unchecked")
    Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
    ActivityCompat.startActivity(this, intent, options);
  }

  protected void bindToolbar() {
    toolbar = findViewById(R.id.toolbar);
  }

  protected void finishWithAnimation() {
    ActivityCompat.finishAfterTransition(this);
  }
}
