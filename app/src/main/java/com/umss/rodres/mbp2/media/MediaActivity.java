package com.umss.rodres.mbp2.media;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.umss.rodres.mbp2.Adaptador_Archivos;
import com.umss.rodres.mbp2.GetListFolder;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.session.Client;
import com.umss.rodres.mbp2.session.LoadInfo;
import com.umss.rodres.mbp2.session.LoadLogOut;
import com.umss.rodres.mbp2.session.Session;

import static android.widget.Toast.*;
import static com.umss.rodres.mbp2.R.id.toolbar;

public class MediaActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView mNavView;

    private int backButtonCount;

    private Session session;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        pref = getSharedPreferences(getString(R.string.info_dropbox), MODE_PRIVATE);

        mDrawer  = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (NavigationView) findViewById(R.id.nav_view_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        session = new Session(pref);
        configMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.conectar();
        if(session.poseeToken()){
            cambiarHeader();
            Client.iniciar(session.getToken());
            View v = mNavView.getHeaderView(0);
            if(pref.getBoolean("flag", false)){
                HeaderSession.show(v, pref); // solo muestra info de la sesion
            }else{
                LoadInfo.load(this, pref, v); // carga y muestra info de la session
            }
            launchMusicFolder();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (backButtonCount >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                makeText(this, String.valueOf(R.string.alerta_salida), LENGTH_SHORT).show();
                backButtonCount++;
            }

        }
    }

    private void configMenu() {
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.idLogin) {
                if(!session.poseeToken())
                    Auth.startOAuth2Authentication(MediaActivity.this, getString(R.string.app_key));
                else{
                    alertaCerrarSesion();
                }
            }else if (id == R.id.idSalir) {
                alertaSalir();
            }else if (id == R.id.idMusic) {
                launchMusicFolder();
            }else if (id == R.id.idVideos) {
                launchVideoFolder();
            }else if (id == R.id.idImag){
                launchPictureFolder();
            }else if(id == R.id.idAcercaDe){
                Toast.makeText(MediaActivity.this, "Acerca de...", Toast.LENGTH_LONG).show();
            }
            mDrawer.closeDrawer(GravityCompat.START);
            return true;
            }
        });
    }

    private void launchMusicFolder(){
        ((ImageView)findViewById(R.id.icon_section)).setImageResource(R.mipmap.ic_folder_music);
        ((TextView)findViewById(R.id.title_section)).setText(getText(R.string.musica));
        Menu  menu = mNavView.getMenu();
        MenuItem navItem = menu.findItem(R.id.idMusic);
        menu.findItem(R.id.idImag).setChecked(false);
        menu.findItem(R.id.idVideos).setChecked(false);
        navItem.setChecked(true);
        recycler("/music");
    }

    private void launchVideoFolder(){
        ((ImageView)findViewById(R.id.icon_section)).setImageResource(R.mipmap.ic_folder_video);
        ((TextView)findViewById(R.id.title_section)).setText(getText(R.string.video));
        Menu  menu = mNavView.getMenu();
        MenuItem navItem = menu.findItem(R.id.idVideos);
        menu.findItem(R.id.idMusic).setChecked(false);
        menu.findItem(R.id.idImag).setChecked(false);
        navItem.setChecked(true);
        recycler("/video");
    }

    private void launchPictureFolder(){
        ((ImageView)findViewById(R.id.icon_section)).setImageResource(R.mipmap.ic_folder_picture);
        ((TextView)findViewById(R.id.title_section)).setText(getText(R.string.imagen));
        Menu  menu = mNavView.getMenu();
        MenuItem navItem = menu.findItem(R.id.idImag);
        menu.findItem(R.id.idMusic).setChecked(false);
        menu.findItem(R.id.idVideos).setChecked(false);
        navItem.setChecked(true);
        recycler("/images");
    }

    private void alertaSalir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MediaActivity.this);
        builder.setMessage(getString(R.string.preg_para_salir));

        builder.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
                Intent i = new Intent();
                i.putExtra("finish", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                System.exit(0);
            }
        });

        builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void alertaCerrarSesion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.preg_cerrar_session));

        builder.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            session.clearToken();
            cerrarSession();

            }
        });

        builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void cerrarSession() {

        Intent mStartActivity = new Intent(this, MediaActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(
                MediaActivity.this, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        LoadLogOut.load(this);

        AlarmManager mgr = (AlarmManager) MediaActivity.this.getSystemService(ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, mPendingIntent);
        System.exit(0);
    }

    private void cambiarHeader(){
        View v = mNavView.getHeaderView(0);
        mNavView.removeHeaderView(v);
        mNavView.inflateHeaderView(R.layout.nav_header_session);

        Menu menu = mNavView.getMenu();
        MenuItem navItem = menu.findItem(R.id.idLogin);
        navItem.setIcon(R.mipmap.ic_logout1);
        navItem.setTitle(R.string.logout);
    }

    private void recycler(String  fol){

        RecyclerView archivos;
        final Adaptador_Archivos adapter;

        archivos = (RecyclerView) findViewById(R.id.files);

        adapter = new Adaptador_Archivos(new Adaptador_Archivos.Callback() {
            @Override
            public void onFolderClicked(FolderMetadata folder) {
                //
            }
            @Override
            public void onFileClicked(FileMetadata file) {
                //para reprocir exoplayer
                Toast.makeText(getApplicationContext(), file.getPathLower(), LENGTH_LONG).show();
            }
        });

        RecyclerView.LayoutManager manager;

        manager = new LinearLayoutManager(this);
        archivos.setLayoutManager(manager);
        archivos.setAdapter(adapter);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Cargando...");
        dialog.show();

        new GetListFolder(Client.getDbxCliente(), new GetListFolder.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult resultado) {
                dialog.dismiss();
                adapter.setmFiles(resultado.getEntries());
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                makeText(MediaActivity.this, "Un error ha ocurrido", LENGTH_LONG).show();
            }
        }).execute(fol);
    }
}