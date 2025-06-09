package tiil.edu.cuoiki;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextApiKey;
    private EditText editTextBaseUrl;
    private EditText editTextModel;
    private EditText editTextMaxToken;
    private EditText editTextTemperature;
    private EditText editTextConcurrentRequests;

    public static final String KEY_API_KEY = "api_key";
    public static final String KEY_BASE_URL = "base_url";
    public static final String KEY_MODEL = "model";
    public static final String KEY_MAX_TOKEN = "max_token";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_CONCURRENT_REQUESTS = "concurrent_requests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cài đặt");
        }

        editTextApiKey = findViewById(R.id.editTextApiKey);
        editTextBaseUrl = findViewById(R.id.editTextBaseUrl);
        editTextModel = findViewById(R.id.editTextModel);
        editTextMaxToken = findViewById(R.id.editTextMaxToken);
        editTextTemperature = findViewById(R.id.editTextTemperature);
        editTextConcurrentRequests = findViewById(R.id.editTextConcurrentRequests);
        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity sau khi lưu
        });
    }

    private void loadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editTextApiKey.setText(prefs.getString(KEY_API_KEY, ""));
        editTextBaseUrl.setText(prefs.getString(KEY_BASE_URL, ""));
        editTextModel.setText(prefs.getString(KEY_MODEL, "gpt-4o"));
        editTextMaxToken.setText(String.valueOf(prefs.getInt(KEY_MAX_TOKEN, 4096)));
        editTextTemperature.setText(String.valueOf(prefs.getFloat(KEY_TEMPERATURE, 0.7f)));
        editTextConcurrentRequests.setText(String.valueOf(prefs.getInt(KEY_CONCURRENT_REQUESTS, 5)));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(KEY_API_KEY, editTextApiKey.getText().toString().trim());
        editor.putString(KEY_BASE_URL, editTextBaseUrl.getText().toString().trim());
        editor.putString(KEY_MODEL, editTextModel.getText().toString().trim());

        try {
            editor.putInt(KEY_MAX_TOKEN, Integer.parseInt(editTextMaxToken.getText().toString()));
        } catch (NumberFormatException e) {
            editor.putInt(KEY_MAX_TOKEN, 4096); // giá trị mặc định
        }

        try {
            editor.putFloat(KEY_TEMPERATURE, Float.parseFloat(editTextTemperature.getText().toString()));
        } catch (NumberFormatException e) {
            editor.putFloat(KEY_TEMPERATURE, 0.7f); // giá trị mặc định
        }
        
        try {
            editor.putInt(KEY_CONCURRENT_REQUESTS, Integer.parseInt(editTextConcurrentRequests.getText().toString()));
        } catch (NumberFormatException e) {
            editor.putInt(KEY_CONCURRENT_REQUESTS, 5); // giá trị mặc định
        }

        editor.apply();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}