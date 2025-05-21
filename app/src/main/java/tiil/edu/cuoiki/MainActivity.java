package tiil.edu.cuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Áp insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Spinner
        Spinner spinner = findViewById(R.id.spinnerPromptTemplate);
        String[] promptTemplates = {
                "Dịch tự nhiên",
                "Dịch sát nghĩa",
                "Dịch sáng tạo",
                "Tóm tắt ý chính"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                promptTemplates
        );
        spinner.setAdapter(adapter);

        // Xử lý nút "Dịch"
        Button btnTranslate = findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(v -> {
            EditText inputText = findViewById(R.id.editTextInput);
            EditText promptText = findViewById(R.id.editTextPrompt);
            String content = inputText.getText().toString().trim();
            String prompt = promptText.getText().toString().trim();
            String template = spinner.getSelectedItem().toString();

            // Dữ liệu giả (chỉ nối lại) — bạn có thể xử lý dịch thật tại đây
            String result = "Prompt: " + prompt + "\nTemplate: " + template + "\n\n" + content;

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("translated_result", result);
            startActivity(intent);
        });

        // Xử lý nút "Cài đặt"
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }
}
