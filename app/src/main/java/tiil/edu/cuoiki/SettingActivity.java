package tiil.edu.cuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button saveBtn = findViewById(R.id.btnSaveSettings);
        saveBtn.setOnClickListener(v -> {
            // TODO: lưu cấu hình nếu cần

            // Quay về MainActivity
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
