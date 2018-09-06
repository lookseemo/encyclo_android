package com.deedsit.android.bookworm.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.security.SessionManager;
import com.deedsit.android.bookworm.ui.authentication.UserAuthFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import javax.inject.Inject;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";



    @Inject
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BookWormApplication)getApplication()).getComponent().inject(this);

        if(sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        
        setContentView(R.layout.activity_sign_in);
        //check for playstore service
        int statusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable
                (this);
        switch(statusCode){
            case ConnectionResult.SUCCESS:
                getSupportFragmentManager().beginTransaction().replace(R.id.sign_in_activity_content,
                        UserAuthFragment
                                .newInstance(), UserAuthFragment.TAG).commit();
                break;
            case ConnectionResult.SERVICE_MISSING:
                showErrDialog("Play Services is missing, Please Install Play Store!");
                break;
            case ConnectionResult.SERVICE_INVALID:
                showErrDialog("Google Play Service installed on this device is not authentic! " +
                        "Please Install Authentic Version To Continue!");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                showErrDialog("Please Update Play Service to continue!");
                break;
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                showErrDialog("Please Enable Play Services!");
                break;
        }
    }

    private void showErrDialog(String message){
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setTitle("Play Service Error");
        errorDialog.setMessage(message);
        errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO: redirect user to playstore if missing or need update
               dialogInterface.dismiss();
               finish();
            }
        });
        
        errorDialog.create().show();
    }

}
