package com.umss.rodres.mbp2.search;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.model.Library;
import com.umss.rodres.mbp2.util.MyDividerItemDecoration;


//import android.widget.SearchView.OnQueryTextListener;

public class ActivitySearch extends AppCompatActivity implements FilesAdapter.FilesAdapterListener{

    private RecyclerView mRecyclerView;
    private SearchView   mSearchView;
    private Toolbar      mToolbar;

    private FilesAdapter mFilesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mToolbar    = findViewById(R.id.idSearchBar);
        //mSearchView = (SearchView) mToolbar.findViewById(R.id.idSearchView);
        mRecyclerView = findViewById(R.id.idListToSearch);

        mFilesAdapter = new FilesAdapter(this, Library.mLibrary, this);

        setSupportActionBar(mToolbar);
        setRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        //mSearchView.setIconified(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mFilesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mFilesAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    private void setRecyclerView(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        mRecyclerView.setAdapter(mFilesAdapter);
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onFileSelected(File file) {
        if(file.getType() == File.TypeOfFile.Audio)      showToastMessage("audio");
        else if(file.getType() == File.TypeOfFile.Video) showToastMessage("video");
        else if(file.getType() == File.TypeOfFile.Image) showToastMessage("imagen");
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}