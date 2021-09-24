package com.umss.rodres.mbp2.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.helper.PicassoClient;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.model.Library;

import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> implements Filterable {

    private Context context;
    private FilesAdapterListener listener;
    private List<File> fileList;
    private List<File> fileListFiltered;
    private Picasso picasso;

    public FilesAdapter(Context context, List<File> fileList, FilesAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.fileList = fileList;
        this.fileListFiltered = fileList;
        this.picasso = PicassoClient.getPicasso();
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_filter, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        File file = Library.mLibrary.get(position);

        holder.nameFile.setText(file.getName());
        holder.path.setText(file.getPath());

        if(file.getType() == File.TypeOfFile.Audio){
            picasso.load(R.drawable.ic_file_music)
                    .noFade()
                    .into(holder.icon);
        }else if(file.getType() == File.TypeOfFile.Video)
        {
            picasso.load(R.drawable.ic_file_video)
                    .noFade()
                    .into(holder.icon);
        }else if(file.getType() == File.TypeOfFile.Image)
        {
            picasso.load(R.drawable.ic_file_image)
                    .noFade()
                    .into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return fileListFiltered.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    fileListFiltered = fileList;
                } else {
                    List<File> filteredList = new ArrayList<>();
                    for (File file : fileList) {
                        if ((file.getName().toLowerCase()).contains(charString.toLowerCase())) {
                            filteredList.add(file);
                        }
                    }
                    fileListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = fileListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.d("name", charSequence.toString());
                fileListFiltered = (ArrayList<File>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView  nameFile, path;
        public ImageView icon;

        public FileViewHolder(View view) {
            super(view);
            nameFile  = view.findViewById(R.id.idFileName);
            path      = view.findViewById(R.id.idFilePath);
            icon      = view.findViewById(R.id.idIconTypeFile);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onFileSelected(fileListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface FilesAdapterListener {
        void onFileSelected(File file);
    }

}
