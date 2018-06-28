package com.umss.rodres.mbp2.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umss.rodres.mbp2.MyApplication;
import com.umss.rodres.mbp2.R;

import com.umss.rodres.mbp2.model.Session;
import com.umss.rodres.mbp2.player.audio.AudioPlayer;
import com.umss.rodres.mbp2.search.ActivitySearch;
import com.umss.rodres.mbp2.util.Conversor;
import com.umss.rodres.mbp2.util.NetworkChangeReceiver;

public class MainActivity extends    AppCompatActivity
                          implements MainContract.View,
                                     NetworkChangeReceiver.ConnectivityReceiverListener,
                                     NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout       mDrawer;
    private NavigationView     mNavView;
    private Toolbar            mToolbar;

    private CoordinatorLayout  mCoordinatorLayout;
    private TextView           mTitleToolbar;
    private ImageView          mImageToolBar;

    private ProgressDialog     mDialog;

    public static MainContract.UserActionsListener  mMainPresenter;

    private FragmentManager     mFragmentManager;
    private FragmentTransaction mTransaction;

    private TextView    mAppName;
    private TextView    mUserName;
    private TextView    mEmail;
    private TextView    mTypeAccount;
    private TextView    mCapacity;
    private ProgressBar mProgressBar;

    private int backButtonCount;

    private static String fragActual;

    private static String fragMusic = "MUSIC";
    private static String fragImag  = "IMAG";
    private static String fragVideo = "VIDEO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawer             = (DrawerLayout)       findViewById(R.id.id_main_drawer_layout);
        mNavView            = (NavigationView)     findViewById(R.id.nav_view_menu);
        mToolbar            = (Toolbar)            findViewById(R.id.toolbar);
        mCoordinatorLayout  = (CoordinatorLayout)  findViewById(R.id.coordinatorMain);
        mTitleToolbar       = (TextView)           findViewById(R.id.title_section);
        mImageToolBar       = (ImageView)          findViewById(R.id.icon_section);

        mMainPresenter      = new MainPresenter(this);
        mDialog             = new ProgressDialog(this);

        mFragmentManager    = getSupportFragmentManager();

        mMainPresenter.launchMusicFragment();

        fragActual = fragMusic;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        setInfoAccountElements(mNavView.getHeaderView(0));
        mNavView.setNavigationItemSelectedListener(this);
        setDefault(true);
    }

    private void setInfoAccountElements(View v) {
        mAppName = v.findViewById(R.id.title_appname);
        mAppName.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/billabong.ttf"));
        mUserName    = v.findViewById(R.id.idNombreUsuario);
        mEmail       = v.findViewById(R.id.idEMailUsuario);
        mTypeAccount = v.findViewById(R.id.idTipoCuenta);
        mCapacity    = v.findViewById(R.id.idCapacidad);
        mProgressBar = v.findViewById(R.id.idProgressBar);

        mUserName.setText(Session.getUserName());
        mEmail.setText(Session.getEmail());
        mTypeAccount.setText(Session.getTypeAccount());
        mMainPresenter.setCapacityAccount();
    }

    @Override
    public void showCapacity(String text){
        mCapacity.setText(text);
    }

    @Override
    public void setProgressBar(int max, int progress){
        mProgressBar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mProgressBar.setProgress(progress, true);
            mProgressBar.setBackgroundColor(Color.BLACK);
        }else{
            mProgressBar.setProgress(progress);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDefault(boolean show){
        mNavView.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(show);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        backButtonCount = 0;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if(id == R.id.action_refresh){
            if (item.isChecked()){
                mMainPresenter.disableAllSwipeRefresh();
                item.setChecked(false);
            }else{
                mMainPresenter.enableAllSwipeRefresh();
                item.setChecked(true);
            }
            return true;
        }else if (id == R.id.action_search){
            launchItemFinder();
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (backButtonCount >= 1) {
                mMainPresenter.exitApp();
            } else {
                showToastMessage(getString(R.string.alerta_salida));
                backButtonCount++;
            }
        }
    }

    @Override
    public void setToolBarInfo(String tittle, int imagRes){
        mTitleToolbar.setText(tittle);
        mImageToolBar.setImageResource(imagRes);
    }

    @Override
    public void animation() {
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void showProgressDialog(String message) {
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setMessage(message);
        mDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        mDialog.dismiss();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showFragment(Fragment fragment){
        mFragmentManager.popBackStack();
        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.content, fragment);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }


    public void showNoConnectionMesagge() {
        Snackbar.make(
                mCoordinatorLayout,
                getString(R.string.disconnected), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.connect), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                }).setActionTextColor(Color.RED).show();
    }

//    @Override
//    public void launchItemFinder() {
//        startActivity(new Intent(this, ActivitySearch.class));
//    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackBarMessage(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_SHORT).setActionTextColor(Color.RED).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.idLogout) {
            mMainPresenter.closeSession();
        }else if (id == R.id.idSalir) {
            mMainPresenter.exitApp();
        }else if (id == R.id.idMusic && fragActual != fragMusic) {
            fragActual = fragMusic;
            mMainPresenter.launchMusicFragment();
        }else if (id == R.id.idVideos && fragActual != fragVideo) {
            fragActual = fragVideo;
            mMainPresenter.launchVideoFragment();
            setDefault(false);
        }else if (id == R.id.idImag && fragActual != fragImag){
            fragActual = fragImag;
            mMainPresenter.launchImageFragment();
            setDefault(false);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected){
            showNoConnectionMesagge();
        }else{
            showSnackBarMessage(getString(R.string.conectado));
        }
    }
}