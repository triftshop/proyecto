package com.trif.mitfg_v2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {

    private List<DocumentItem> documents;
    private OnDocumentClickListener listener;

    public interface OnDocumentClickListener {
        void onDocumentClick(DocumentItem document);
        void onDocumentLongClick(DocumentItem document);
    }

    public DocumentsAdapter(List<DocumentItem> documents, OnDocumentClickListener listener) {
        this.documents = documents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocumentItem document = documents.get(position);
        holder.bind(document, listener);
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void updateDocuments(List<DocumentItem> newDocuments) {
        this.documents = newDocuments;
        notifyDataSetChanged();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private TextView txtTitle;
        private TextView txtDate;
        private TextView txtType;
        private ImageView imgTypeIcon;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgDocumentThumbnail);
            txtTitle = itemView.findViewById(R.id.txtDocumentTitle);
            txtDate = itemView.findViewById(R.id.txtDocumentDate);
            txtType = itemView.findViewById(R.id.txtDocumentType);
            imgTypeIcon = itemView.findViewById(R.id.imgDocumentTypeIcon);
        }

        public void bind(DocumentItem document, OnDocumentClickListener listener) {
            txtTitle.setText(document.getTitle());

            // Formatear fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            txtDate.setText(sdf.format(new Date(document.getDateAdded())));

            // Configurar tipo de documento
            if (document.isPredefined()) {
                // Para documentos oficiales, mostrar la región directamente
                txtType.setText(document.getAutonomousRegion() != null ?
                        document.getAutonomousRegion() :
                        itemView.getContext().getString(R.string.autonomous_region));
                imgTypeIcon.setImageResource(R.drawable.ic_government);
                imgTypeIcon.setVisibility(View.VISIBLE);
            } else {
                txtType.setText(itemView.getContext().getString(R.string.user_document));
                imgTypeIcon.setImageResource(R.drawable.ic_user_document);
                imgTypeIcon.setVisibility(View.VISIBLE);
            }

            // Cargar thumbnail
            if (document.getThumbnailPath() != null && !document.getThumbnailPath().isEmpty()) {
                try {
                    Bitmap thumbnail = BitmapFactory.decodeFile(document.getThumbnailPath());
                    if (thumbnail != null) {
                        imgThumbnail.setImageBitmap(thumbnail);
                    } else {
                        imgThumbnail.setImageResource(R.drawable.ic_pdf_placeholder);
                    }
                } catch (Exception e) {
                    imgThumbnail.setImageResource(R.drawable.ic_pdf_placeholder);
                }
            } else {
                imgThumbnail.setImageResource(R.drawable.ic_pdf_placeholder);
            }

            // Configurar listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDocumentClick(document);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null && !document.isPredefined()) {
                    listener.onDocumentLongClick(document);
                    return true;
                }
                return false;
            });
        }
    }
}