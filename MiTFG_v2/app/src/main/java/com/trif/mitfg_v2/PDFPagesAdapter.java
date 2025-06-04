package com.trif.mitfg_v2;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PDFPagesAdapter extends RecyclerView.Adapter<PDFPagesAdapter.PageViewHolder> {

    private List<Bitmap> pages;

    public PDFPagesAdapter(List<Bitmap> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        Bitmap page = pages.get(position);
        holder.bind(page, position + 1);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPage;
        private TextView txtPageNumber;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPage = itemView.findViewById(R.id.imgPDFPage);
            txtPageNumber = itemView.findViewById(R.id.txtPageNumber);
        }

        public void bind(Bitmap page, int pageNumber) {
            imgPage.setImageBitmap(page);
            txtPageNumber.setText(itemView.getContext().getString(R.string.page_number, pageNumber));
        }
    }
}