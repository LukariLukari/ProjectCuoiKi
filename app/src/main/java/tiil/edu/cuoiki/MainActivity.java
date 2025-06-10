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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int OPEN_FILE_REQUEST_CODE = 2;

    private static final String GEMINI_API_KEY = "AI";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/openai/";

    private static final String DEEPSEEK_API_KEY = "sk-or-v1";
    private static final String DEEPSEEK_BASE_URL = "https://openrouter.ai/api/v1";

    private static final String GPT4O_MINI_API_KEY = "sk-";
    private static final String OPENAI_BASE_URL = "https://api.sv2.llm.ai.vn/v1";

    private static final String QWEN_API_KEY = "sk-or-v13";
    private static final String QWEN_BASE_URL = "https://openrouter.ai/api/v1";

    // --- Giới hạn và Key cho lượt dùng thử ---
    private static final int TRIAL_USE_LIMIT = 5;
    private static final String KEY_GEMINI_USES = "gemini_trial_uses";
    private static final String KEY_DEEPSEEK_USES = "deepseek_trial_uses";
    private static final String KEY_GPT4O_MINI_USES = "gpt4o_mini_trial_uses";
    private static final String KEY_QWEN_USES = "qwen_trial_uses";

    private EditText edtInput;
    private EditText edtSegmentSize;
    private TextView txtSegmentResult;
    private String textFromFile = ""; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        edtInput = findViewById(R.id.edtInput);
        edtSegmentSize = findViewById(R.id.edtSegmentSize);
        txtSegmentResult = findViewById(R.id.txtSegmentResult);

        // Đặt giới hạn ký tự cho EditText
        final int MAX_CHAR_LIMIT = 15000;
        edtInput.setFilters(new InputFilter[] {
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
                return null; 
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Spinner
        Spinner spinner = findViewById(R.id.spnPromptTemplate);
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

        ImageView helpIcon = findViewById(R.id.ivHelpIcon);
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
            textFromFile = stringBuilder.toString();
            edtInput.setText("[Đã tải file thành công với " + textFromFile.length() + " ký tự. Sẵn sàng dịch.]");
            Toast.makeText(this, "Đã tải file.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error reading file", e);
            Toast.makeText(this, "Lỗi khi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void translateText() {
        EditText promptText = findViewById(R.id.edtPrompt);
        Spinner spinner = findViewById(R.id.spnPromptTemplate);
        RadioGroup modelGroup = findViewById(R.id.rgModelGroup);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String content;
        if (!textFromFile.isEmpty()) {
            content = textFromFile;
        } else {
            content = edtInput.getText().toString().trim();
        }

        String mainPrompt = promptText.getText().toString().trim();
        String templatePrompt = spinner.getSelectedItem().toString();
        String segmentSizeStr = edtSegmentSize.getText().toString().trim();

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

        // Các biến cấu hình sẽ được gửi đi
        String apiKey;
        String baseUrl;
        String model;
        long maxTokens;

        // Lấy model được chọn và cấu hình tương ứng
        int selectedId = modelGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.rbGemini) {
            if (!canUseTrial(KEY_GEMINI_USES, "Gemini 1.5 Flash")) return;
            apiKey = GEMINI_API_KEY;
            baseUrl = GEMINI_BASE_URL;
            model = "gemini-1.5-flash-8b";
            maxTokens = 5000;
        } else if (selectedId == R.id.rbDeepseek) {
            if (!canUseTrial(KEY_DEEPSEEK_USES, "Deepseek V3")) return;
            apiKey = DEEPSEEK_API_KEY;
            baseUrl = DEEPSEEK_BASE_URL;
            model = "deepseek/deepseek-chat-v3-0324:free";
            maxTokens = 5000;
        } else if (selectedId == R.id.rbGpt4oMini) {
            if (!canUseTrial(KEY_GPT4O_MINI_USES, "GPT-4o mini")) return;
            apiKey = GPT4O_MINI_API_KEY;
            baseUrl = OPENAI_BASE_URL;
            model = "gpt-4o-mini";
            maxTokens = 5000;
        } else if (selectedId == R.id.rbQwen3) {
            if (!canUseTrial(KEY_QWEN_USES, "Qwen3")) return;
            apiKey = QWEN_API_KEY;
            baseUrl = QWEN_BASE_URL;
            model = "qwen/qwen3-30b-a3b:free";
            maxTokens = 5000;
        } else {
            apiKey = prefs.getString("api_key", null);
            baseUrl = prefs.getString("base_url", null);
            model = prefs.getString("model_name", null);
            maxTokens = (long) prefs.getInt("max_tokens", 2048);
        }

        if (apiKey == null || baseUrl == null || model == null || apiKey.isEmpty() || apiKey.contains("YOUR_")) {
            Toast.makeText(this, "Vui lòng chọn một mô hình hoặc cấu hình đầy đủ trong Cài đặt.", Toast.LENGTH_LONG).show();
            return;
        }

        // Chuyển màn hình ngay lập tức
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("raw_content", content);
        intent.putExtra("system_prompt", "System Prompt: " + templatePrompt + "\n\nUser Prompt: " + mainPrompt);
        intent.putExtra("max_segment_size", maxSegmentSize);

        // Đưa toàn bộ thông tin cấu hình vào intent
        intent.putExtra("api_key", apiKey);
        intent.putExtra("base_url", baseUrl);
        intent.putExtra("model_name", model);
        intent.putExtra("temperature", (double) prefs.getFloat("temperature", 0.7f));
        intent.putExtra("max_tokens", maxTokens);

        startActivity(intent);
    }


    private boolean canUseTrial(String usageKey, String modelName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int currentUses = prefs.getInt(usageKey, 0);

        if (currentUses >= TRIAL_USE_LIMIT) {
            Toast.makeText(this, "Bạn đã hết " + TRIAL_USE_LIMIT + " lượt dùng thử cho " + modelName, Toast.LENGTH_LONG).show();
            return false; // Hết lượt
        }

        // Tăng số lượt đã dùng và lưu lại
        prefs.edit().putInt(usageKey, currentUses + 1).apply();


    }
}
