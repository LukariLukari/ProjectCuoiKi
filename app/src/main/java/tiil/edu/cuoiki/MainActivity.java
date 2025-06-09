package tiil.edu.cuoiki;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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

        // Xử lý sự kiện cho nút Cài đặt
        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, SettingsActivity.class));
        });
    }
}
