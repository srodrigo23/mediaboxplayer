package com.umss.rodres.mbp2.search;

import android.support.annotation.NonNull;
import android.widget.Filter;

import com.umss.rodres.mbp2.files.MyAdapter;
import com.umss.rodres.mbp2.model.File;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper extends Filter {

    private static List<File> mFileList;
    private static List<File> mFileListFiltered;

    private static MyAdapter mAdapter;

    @NonNull
    public static FilterHelper newInstance(List<File> currentList,
                                           MyAdapter adapter) {
        mAdapter  = adapter;
        mFileList = currentList;
        return new FilterHelper();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        String charString = constraint.toString();
        if(charString.isEmpty()){
            mFileListFiltered = mFileList;
        }else {
            List<File> filteredList = new ArrayList<>();
            for (File file : mFileList) {
                if(file.getName().toLowerCase().contains(charString.toLowerCase())){
                    filteredList.add(file);
                }
            }
            mFileListFiltered = filteredList;
        }
        FilterResults filterResults = new FilterResults();
        filterResults.values = mFileListFiltered;
        return filterResults;
    }
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        mAdapter.replaceData((List<File>) filterResults.values);
        mAdapter.notifyDataSetChanged();
    }
}