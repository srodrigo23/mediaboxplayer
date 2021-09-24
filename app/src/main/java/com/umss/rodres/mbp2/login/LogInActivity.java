package com.umss.rodres.mbp2.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.umss.rodres.mbp2.R;

public class LogInActivity extends AppCompatActivity implements LoginContract.View{

    private Button                            mLogin_button;
    private Button                            mLogout_button;
    private Button                            mTryAgain_button;
    private ProgressBar                       mProgress;
    private LoginContract.UserActionsListener mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mPresenter = new LoginPresenter(this);

        mLogout_button   = (Button) findViewById(R.id.idLogoutButton);
        mLogin_button    = (Button) findViewById(R.id.idLoginButton);
        mProgress        = (ProgressBar) findViewById(R.id.idProgressBarLogin);
        mTryAgain_button = (Button) findViewById(R.id.idTryAgainButton);
        mPresenter.initSession();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.setOnResume();
        setup_login_button();
        setup_logout_button();
        setup_tryAgain_button();
    }

    private void setup_login_button(){
        mLogin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login();
            }
        });
    }

    private void setup_logout_button(){
        mLogout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.logOut();
            }
        });
    }

    private void setup_tryAgain_button(){
        mTryAgain_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.tryAgain();
            }
        });
    }

    @Override
    public void showNoConnectionError() {
        Snackbar.make(
                findViewById(R.id.id_login_layout),
                getString(R.string.disconnected), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.connect), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        }).setActionTextColor(Color.RED).show();
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showLoginButton() {
        mLogin_button.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoginButton() {
        mLogin_button.setVisibility(View.GONE);
    }

    @Override
    public void showLogoutButton() {
        mLogout_button.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLogoutButton() {
        mLogout_button.setVisibility(View.GONE);
    }

    @Override
    public void showTryAgainButton() {
        mTryAgain_button.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTryAgainButton() {
        mTryAgain_button.setVisibility(View.GONE);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void setTextLoginButton(String text) { mLogin_button.setText(text);}

    @Override
    public void animationTraslation() {
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}