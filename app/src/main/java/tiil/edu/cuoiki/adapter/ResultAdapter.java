package tiil.edu.cuoiki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import tiil.edu.cuoiki.R;
import tiil.edu.cuoiki.model.TranslatedSegment;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private List<TranslatedSegment> segments;

    public ResultAdapter(List<TranslatedSegment> segments) {
        this.segments = segments;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        TranslatedSegment segment = segments.get(position);
        holder.bind(segment);
    }

    @Override
    public int getItemCount() {
        return segments.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView segmentIdTextView;
        TextView segmentContentTextView;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            segmentIdTextView = itemView.findViewById(R.id.textViewSegmentId);
            segmentContentTextView = itemView.findViewById(R.id.textViewSegmentContent);
        }

        public void bind(TranslatedSegment segment) {
            segmentIdTextView.setText("ID: " + segment.getId());
            segmentContentTextView.setText(segment.getContent());
        }
    }
} 