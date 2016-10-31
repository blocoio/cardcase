package io.bloco.cardcase.presentation.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.OnClick;
import io.bloco.cardcase.R;
import io.bloco.cardcase.presentation.BaseActivity;

import android.app.Activity;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;

import static android.widget.Toast.*;

public class SettingsActivity extends BaseActivity implements OnItemSelectedListener {

    @Bind(R.id.toolbar_settings_close)
    ImageButton settingsClose;

    String[] data = {"Red", "DefaultTheme", "Green"};

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Choose Theme");
        spinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        Log.d("SAL", "HIHI");
        Log.d("SAL", String.valueOf(position));

        // показываем позиция нажатого элемента
        makeText(getBaseContext(), "Theme = " + parent.getItemAtPosition(position).toString() + " is selected", LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        Log.d("SAL", "BYEBYE");
    }

    @Override
    protected void onStart() {
        super.onStart();
////        Theme currentTheme = new Theme();
//        View view = this.getWindow().getDecorView();
//        RelativeLayout ll = (RelativeLayout) findViewById(R.id.activity_settings);

//        Theme.viewRelativeLayTheme(view, ll);

    }

    @OnClick(R.id.toolbar_settings_close)
    public void onClickedBack() {
        Intent intent = HomeActivity.getIntent(this);
        startActivityWithAnimation(intent);
    }
}
