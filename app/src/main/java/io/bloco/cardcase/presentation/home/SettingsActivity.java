package io.bloco.cardcase.presentation.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.presentation.BaseActivity;
import io.bloco.cardcase.presentation.Preferences;

import android.app.Activity;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;

import java.util.Arrays;

import static android.widget.Toast.*;

public class SettingsActivity extends BaseActivity implements OnItemSelectedListener {

  @Bind(R.id.toolbar_settings_close) ImageButton settingsClose;

  @Bind(R.id.save_button) Button saveButton;

  Theme.ThemeType selectedTheme;
  Theme.ThemeType oldTheme;

  public static Intent getIntent(Context context) {
    return new Intent(context, SettingsActivity.class);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    String[] data = Theme.ThemeType.names();

    ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    Spinner spinner = (Spinner) findViewById(R.id.spinner);
    spinner.setAdapter(adapter);
    spinner.setPrompt("Choose Theme");
    spinner.setOnItemSelectedListener(this);

    String themeStr =
        Preferences.getStringForKeyInContext(Preferences.THEME_KEY, getApplicationContext());
    int index = Arrays.asList(data).indexOf(themeStr);
    spinner.setSelection(index);

    oldTheme = Theme.ThemeType.fromInt(index);
  }

  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    makeText(getBaseContext(),
        "" + parent.getItemAtPosition(position).toString() + " theme is selected",
        LENGTH_SHORT).show();

    selectedTheme = Theme.ThemeType.fromInt(position);

    Preferences.setStringForKeyInContext(selectedTheme.toString(), Preferences.THEME_KEY,
        getApplicationContext());

    Theme.setCurrentTheme(selectedTheme);
    Theme.applyThemeFor(this.getWindow().getDecorView(), getApplicationContext());
  }

  @OnClick(R.id.toolbar_settings_close) public void onClickedBack(View view) {
    Preferences.setStringForKeyInContext(oldTheme.toString(), Preferences.THEME_KEY,
        getApplicationContext());
    super.onBackPressed();
  }

  @OnClick(R.id.save_button) public void saveButtonClicked(View view) {
    super.onBackPressed();
  }

  public void onNothingSelected(AdapterView<?> arg0) {

  }
}
