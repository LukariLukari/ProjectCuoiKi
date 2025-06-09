package tiil.edu.cuoiki;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText apiKeyInput, baseUrlInput, modelNameInput, tempInput, maxTokensInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ các view
        apiKeyInput = findViewById(R.id.editTextApiKey);
        baseUrlInput = findViewById(R.id.editTextBaseUrl);
        modelNameInput = findViewById(R.id.editTextModel);
        tempInput = findViewById(R.id.editTextTemperature);
        maxTokensInput = findViewById(R.id.editTextMaxToken);

        // Load cài đặt đã lưu
        loadSettings();

        // Xử lý nút lưu
        Button saveBtn = findViewById(R.id.btnSaveSettings);
        saveBtn.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
            finish(); // Quay về màn hình trước đó
        });
    }

    private void loadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        apiKeyInput.setText(prefs.getString("api_key", ""));
        baseUrlInput.setText(prefs.getString("base_url", "https://api.openai.com/v1/"));
        modelNameInput.setText(prefs.getString("model_name", "gpt-3.5-turbo"));
        tempInput.setText(String.valueOf(prefs.getFloat("temperature", 0.7f)));
        maxTokensInput.setText(String.valueOf(prefs.getInt("max_tokens", 2048)));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("api_key", apiKeyInput.getText().toString().trim());
        editor.putString("base_url", baseUrlInput.getText().toString().trim());
        editor.putString("model_name", modelNameInput.getText().toString().trim());
        try {
            editor.putFloat("temperature", Float.parseFloat(tempInput.getText().toString()));
        } catch (NumberFormatException e) {
            editor.putFloat("temperature", 0.7f); // giá trị mặc định nếu nhập sai
        }
        try {
            editor.putInt("max_tokens", Integer.parseInt(maxTokensInput.getText().toString().trim()));
        } catch (NumberFormatException e) {
            editor.putInt("max_tokens", 2048); // giá trị mặc định nếu nhập sai
        }
        editor.apply();
    }
}
