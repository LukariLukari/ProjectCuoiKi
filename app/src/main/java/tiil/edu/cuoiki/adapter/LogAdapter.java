package tiil.edu.cuoiki.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import tiil.edu.cuoiki.R;
import tiil.edu.cuoiki.model.LogMessage;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogMessage> logMessages;

    public LogAdapter(List<LogMessage> logMessages) {
        this.logMessages = logMessages;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogMessage logMessage = logMessages.get(position);
        holder.bind(logMessage);
    }

    @Override
    public int getItemCount() {
        return logMessages.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView logMessageTextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logMessageTextView = (TextView) itemView;
        }

        public void bind(LogMessage logMessage) {
            logMessageTextView.setText(logMessage.getMessage());
            if (logMessage.isError()) {
                logMessageTextView.setTextColor(Color.RED);
            } else {
                logMessageTextView.setTextColor(Color.parseColor("#455A64"));
            }
        }
    }
} 