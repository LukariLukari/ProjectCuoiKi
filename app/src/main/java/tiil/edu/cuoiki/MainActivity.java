package tiil.edu.cuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.view.Gravity;
import android.widget.RadioGroup;
import android.widget.RadioButton;

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

        // Spinner
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

        // Xử lý dấu hỏi ? (tooltip + toast)
        ImageView helpIcon = findViewById(R.id.helpIcon);
        helpIcon.setOnClickListener(v -> {
            View popupView = LayoutInflater.from(this).inflate(R.layout.popup_tooltip, null, false);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            // Lấy tọa độ của helpIcon trên màn hình
            int[] location = new int[2];
            helpIcon.getLocationOnScreen(location);

            // Hiện popup phía trên icon (tùy chỉnh offset Y)
            popupWindow.showAtLocation(helpIcon, Gravity.NO_GRAVITY,
                    location[0], location[1] - helpIcon.getHeight() - 100); // 100 = khoảng cách tùy chỉnh
        });


        // Nút Dịch
        Button btnTranslate = findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(v -> {
            EditText inputText = findViewById(R.id.editTextInput);
            EditText promptText = findViewById(R.id.editTextPrompt);
            String content = inputText.getText().toString().trim();
            String prompt = promptText.getText().toString().trim();
            String template = spinner.getSelectedItem().toString();

            // Lấy mô hình đã chọn từ RadioGroup
            RadioGroup modelGroup = findViewById(R.id.radioModelGroup);
            int selectedId = modelGroup.getCheckedRadioButtonId();
            String model = "";

            if (selectedId == R.id.radioGemini) {
                model = "Model: Gemini 1.0";
            } else if (selectedId == R.id.radioDeepseek) {
                model = "Model: Deepseek V3";
            }

            String result = model + "\nPrompt: " + prompt + "\nTemplate: " + template + "\n\n" + content;

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("translated_result", result);
            startActivity(intent);
        });


        // Nút Cài đặt
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }
}
