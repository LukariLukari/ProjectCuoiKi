package tiil.edu.cuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.view.Gravity;
import android.widget.RadioGroup;
import android.graphics.Color;
import android.widget.TextView;
import android.text.InputFilter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.app.Activity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int OPEN_FILE_REQUEST_CODE = 2;
    private EditText editTextInput;
    private EditText editTextSegmentSize;
    private TextView textViewSegmentResult;
    private String textFromFile = ""; // Biến lưu nội dung từ file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        editTextInput = findViewById(R.id.editTextInput);
        editTextSegmentSize = findViewById(R.id.editTextSegmentSize);
        textViewSegmentResult = findViewById(R.id.textViewSegmentResult);

        // Đặt giới hạn ký tự cho EditText
        final int MAX_CHAR_LIMIT = 15000;
        editTextInput.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(MAX_CHAR_LIMIT),
            (source, start, end, dest, dstart, dend) -> {
                int newLength = dest.length() + (end - start);
                if (newLength > MAX_CHAR_LIMIT) {
                    runOnUiThread(() -> Toast.makeText(
                        MainActivity.this,
                        "Văn bản quá dài. Vui lòng dùng chức năng 'Chọn file (.txt)' cho nội dung trên " + MAX_CHAR_LIMIT + " ký tự.",
                        Toast.LENGTH_LONG
                    ).show());
                    return source.subSequence(0, dest.length() > dstart ? dest.length() - dstart : 0);
                }
                return null; // Chấp nhận thay đổi
            }
        });

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                promptTemplates) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.parseColor("#b35a02")); // màu chữ ở đây
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.parseColor("#b35a02")); // màu chữ dropdown
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPopupBackgroundResource(R.drawable.bg_spinner_dropdown);


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
            translateText();
        });


        // Nút Cài đặt
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Nút chọn file
        Button btnChooseFile = findViewById(R.id.btnChooseFile);
        btnChooseFile.setOnClickListener(v -> {
            openFilePicker();
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                readTextFromUri(uri);
            }
        }
    }

    private void readTextFromUri(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            // Không set text vào EditText, chỉ lưu vào biến
            textFromFile = stringBuilder.toString();
            // Cập nhật EditText với thông báo
            editTextInput.setText("[Đã tải file thành công với " + textFromFile.length() + " ký tự. Sẵn sàng dịch.]");
            Toast.makeText(this, "Đã tải file.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error reading file", e);
            Toast.makeText(this, "Lỗi khi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void translateText() {
        EditText promptText = findViewById(R.id.editTextPrompt);
        Spinner spinner = findViewById(R.id.spinnerPromptTemplate);
        RadioGroup modelGroup = findViewById(R.id.radioModelGroup);

        String content;
        // Ưu tiên nội dung từ file nếu có, nếu không thì lấy từ EditText
        if (!textFromFile.isEmpty()) {
            content = textFromFile;
        } else {
            content = editTextInput.getText().toString().trim();
        }

        String mainPrompt = promptText.getText().toString().trim();
        String templatePrompt = spinner.getSelectedItem().toString();
        String segmentSizeStr = editTextSegmentSize.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung hoặc chọn file", Toast.LENGTH_SHORT).show();
            return;
        }

        int maxSegmentSize = 0;
        if (!segmentSizeStr.isEmpty()){
            try {
                maxSegmentSize = Integer.parseInt(segmentSizeStr);
            } catch (NumberFormatException e){
                Toast.makeText(MainActivity.this, "Số ký tự segment không hợp lệ.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Chuyển màn hình ngay lập tức
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("raw_content", content);
        intent.putExtra("system_prompt", "System Prompt: " + templatePrompt + "\n\nUser Prompt: " + mainPrompt);
        intent.putExtra("max_segment_size", maxSegmentSize);

        // Lấy model được chọn
        int selectedId = modelGroup.getCheckedRadioButtonId();
        String modelOverride = null;
        if (selectedId == R.id.radioGemini) {
            modelOverride = "gemini-1.0-pro";
        } else if (selectedId == R.id.radioDeepseek) {
            modelOverride = "deepseek-chat";
        }
        if (modelOverride != null) {
            intent.putExtra("model_override", modelOverride);
        }

        startActivity(intent);
    }
}
