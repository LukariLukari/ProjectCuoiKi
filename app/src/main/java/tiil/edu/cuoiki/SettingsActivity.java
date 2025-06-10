package tiil.edu.cuoiki;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText edtApiKey, edtBaseUrl, edtModel, edtTemperature, edtMaxTokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ các view
        edtApiKey = findViewById(R.id.edtApiKey);
        edtBaseUrl = findViewById(R.id.edtBaseUrl);
        edtModel = findViewById(R.id.edtModel);
        edtTemperature = findViewById(R.id.edtTemperature);
        edtMaxTokens = findViewById(R.id.edtMaxToken);

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
        edtApiKey.setText(prefs.getString("api_key", ""));
        edtBaseUrl.setText(prefs.getString("base_url", "https://api.openai.com/v1/"));
        edtModel.setText(prefs.getString("model_name", "gpt-3.5-turbo"));
        edtTemperature.setText(String.valueOf(prefs.getFloat("temperature", 0.7f)));
        edtMaxTokens.setText(String.valueOf(prefs.getInt("max_tokens", 2048)));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("api_key", edtApiKey.getText().toString().trim());
        editor.putString("base_url", edtBaseUrl.getText().toString().trim());
        editor.putString("model_name", edtModel.getText().toString().trim());
        try {
            editor.putFloat("temperature", Float.parseFloat(edtTemperature.getText().toString()));
        } catch (NumberFormatException e) {
            editor.putFloat("temperature", 0.7f); // giá trị mặc định nếu nhập sai
        }
        try {
            editor.putInt("max_tokens", Integer.parseInt(edtMaxTokens.getText().toString().trim()));
        } catch (NumberFormatException e) {
            editor.putInt("max_tokens", 2048); // giá trị mặc định nếu nhập sai
        }
        editor.apply();
    }
}
