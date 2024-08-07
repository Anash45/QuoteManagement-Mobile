package com.example.helloworld;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Spinner spinnerLanguage;
    private boolean isFirstLoad = true;
    private SharedPreferences langPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences for language preference
        langPrefs = getSharedPreferences("LangPrefs", MODE_PRIVATE);

        // Initialize Spinner for language selection
        spinnerLanguage = findViewById(R.id.spinner_language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // Load and set the saved language preference
        String savedLanguage = langPrefs.getString("language", "en");
        if ("fr".equals(savedLanguage)) {
            spinnerLanguage.setSelection(1); // Set French if saved preference is French
        } else {
            spinnerLanguage.setSelection(0); // Default to English
        }

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String languageCode = position == 0 ? "en" : "fr"; // 0 for English, 1 for French
                if (!isFirstLoad && !savedLanguage.equals(languageCode)) {
                    onLanguageChange(languageCode);
                } else {
                    isFirstLoad = false; // Mark initial load as complete
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_add_product:
                    selectedFragment = new AddProductFragment();
                    break;
                case R.id.nav_products:
                    selectedFragment = new ProductsFragment();
                    break;
                case R.id.nav_cart:
                    selectedFragment = new CartFragment();
                    break;
            }

            if (selectedFragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Load default fragment on initial setup
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_add_product); // Load the default fragment
        }
    }

    private void onLanguageChange(String languageCode) {
        // Save the selected language to SharedPreferences
        SharedPreferences.Editor editor = langPrefs.edit();
        editor.putString("language", languageCode);
        editor.apply(); // Apply changes asynchronously

        // Set the locale and reload the activity
        LocaleHelper.setLocale(this, languageCode);
        recreate(); // Recreate the activity to apply the new language
    }
}

