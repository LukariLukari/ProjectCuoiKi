package tiil.edu.cuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultView = findViewById(R.id.textTranslatedResult);
        String result = getIntent().getStringExtra("translated_result");
        resultView.setText(result != null ? result : "Không có dữ liệu.");

        // Xử lý nút "Quay về"
        Button backBtn = findViewById(R.id.btnBackToMain);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng màn hiện tại
        });
    }
}
