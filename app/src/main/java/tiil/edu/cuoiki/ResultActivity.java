package tiil.edu.cuoiki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import tiil.edu.cuoiki.adapter.LogAdapter;
import tiil.edu.cuoiki.adapter.ResultAdapter;
import tiil.edu.cuoiki.model.LogMessage;
import tiil.edu.cuoiki.model.TranslatedSegment;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private RecyclerView rvResults;
    private RecyclerView rvLogs;
    private ResultAdapter resultAdapter;
    private LogAdapter logAdapter;
    private List<TranslatedSegment> translatedSegments = new ArrayList<>();
    private List<LogMessage> logMessages = new ArrayList<>();
    private ActivityResultLauncher<Intent> saveFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setupViews();
        setupButtons();
        setupSaveFileLauncher();

        String rawContent = getIntent().getStringExtra("raw_content");
        String systemPrompt = getIntent().getStringExtra("system_prompt");
        int maxSegmentSize = getIntent().getIntExtra("max_segment_size", 0);

        // Nhận toàn bộ cấu hình từ Intent
        String apiKey = getIntent().getStringExtra("api_key");
        String baseUrl = getIntent().getStringExtra("base_url");
        String model = getIntent().getStringExtra("model_name");
        double temperature = getIntent().getDoubleExtra("temperature", 0.7);
        long maxTokens = getIntent().getLongExtra("max_tokens", 2048);

        if (rawContent != null && !rawContent.isEmpty() && systemPrompt != null) {
            List<String> segmentsToTranslate;
            if (maxSegmentSize > 0) {
                segmentsToTranslate = splitTextIntoSegments(rawContent, maxSegmentSize);
            } else {
                segmentsToTranslate = new ArrayList<>();
                segmentsToTranslate.add(rawContent);
            }
            initializePlaceholders(segmentsToTranslate.size());
            startTranslationProcess(segmentsToTranslate, systemPrompt, apiKey, baseUrl, model, temperature, maxTokens);
        } else {
            Toast.makeText(this, "Không có dữ liệu để dịch.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViews() {
        rvResults = findViewById(R.id.rvResults);
        rvLogs = findViewById(R.id.rvLogs);

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new ResultAdapter(translatedSegments);
        rvResults.setAdapter(resultAdapter);

        rvLogs.setLayoutManager(new LinearLayoutManager(this));
        logAdapter = new LogAdapter(logMessages);
        rvLogs.setAdapter(logAdapter);
    }

    private void setupButtons() {
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnExportFile = findViewById(R.id.btnExportFile);
        btnExportFile.setOnClickListener(v -> exportResultsToFile());
    }
    
    

    private void exportResultsToFile() {
        String fileName = "Translated_Result_" + System.currentTimeMillis() + ".txt";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        saveFileLauncher.launch(intent);
    }

    private void initializePlaceholders(int count) {
        for (int i = 0; i < count; i++) {
            translatedSegments.add(new TranslatedSegment(i + 1, "Đang dịch segment " + (i + 1) + "..."));
        }
        resultAdapter.notifyDataSetChanged();
    }

    private List<String> splitTextIntoSegments(String text, int maxSegmentSize) {
        List<String> segments = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return segments;
        }

        String[] lines = text.split("\\r?\\n");
        StringBuilder currentSegment = new StringBuilder();
        int currentLength = 0;

        for (String line : lines) {
            if (currentLength > 0 && currentLength + line.length() + 1 > maxSegmentSize) {
                segments.add(currentSegment.toString());
                currentSegment = new StringBuilder();
                currentLength = 0;
            }
            if (currentSegment.length() > 0) {
                currentSegment.append("\n");
                currentLength++;
            }
            currentSegment.append(line);
            currentLength += line.length();
        }

        if (currentSegment.length() > 0) {
            segments.add(currentSegment.toString());
        }

        runOnUiThread(() -> addLog("Văn bản đã được tách thành " + segments.size() + " segments.", false));

        return segments;
    }

    private void startTranslationProcess(List<String> segments, String systemPrompt,
                                       String apiKey, String baseUrl, String model,
                                       double temperature, long maxTokens) {
        int totalSegments = segments.size();

        OpenAIClientAsync client = OpenAIOkHttpClientAsync.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        for (int i = 0; i < segments.size(); i++) {
            final int index = i;
            String segmentContent = segments.get(i);
            
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(model)
                    .addSystemMessage(systemPrompt)
                    .addUserMessage(segmentContent)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

            client.chat().completions().create(params)
                .whenCompleteAsync((completion, throwable) -> runOnUiThread(() -> {
                    if (throwable != null) {
                        // Handle failure
                        String errorMessage = "Lỗi: " + throwable.getMessage();
                        translatedSegments.get(index).setContent("Thất bại: " + errorMessage);
                        resultAdapter.notifyItemChanged(index);
                        addLog("Segment " + (index + 1) + "/" + totalSegments + ": Thất bại. " + errorMessage, true);
                        Log.e(TAG, "API call failed for segment " + index, throwable);
                    } else {
                        // Handle success
                        String result = completion.choices().get(0).message().content().get();
                        translatedSegments.get(index).setContent(result);
                        resultAdapter.notifyItemChanged(index);
                        addLog("Segment " + (index + 1) + "/" + totalSegments + ": Thành công.", false);
                    }
                }));
        }
    }
    
    private void addLog(String message, boolean isError) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        logMessages.add(new LogMessage("[" + timestamp + "] " + message, isError));
        logAdapter.notifyItemInserted(logMessages.size() - 1);
        rvLogs.scrollToPosition(logMessages.size() - 1);
    }
}
