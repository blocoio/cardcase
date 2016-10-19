package io.bloco.cardcase.presentation.home;

import android.app.Activity;
import android.os.Bundle;

import io.bloco.cardcase.R;

import android.content.Context;
import android.content.Intent;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;

public class SettingsActivity extends Activity {

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
        // заголовок
        spinner.setPrompt("Choose Theme");
        // выделяем элемент
        spinner.setSelection(2);


        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
//                Toast.makeText(getBaseContext(), "Theme = " + spinner.getitgetItemAtPosition(position), Toast.LENGTH_SHORT + " is selected").show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }
}
