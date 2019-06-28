package com.c3rberuss.wi_free;

import android.Manifest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.c3rberuss.wi_free.activities.Perfil;
import com.c3rberuss.wi_free.fragments.InicioFragment;
import com.c3rberuss.wi_free.fragments.WifiFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFirestoreSettings settings;
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    private Menu menu;
    private InicioFragment inicioFragment = new InicioFragment();
    private WifiFragment wifiFragment = new WifiFragment();

    private  List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        open_fragment(true, inicioFragment);

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        this.db.setFirestoreSettings(this.settings);


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.CHANGE_WIFI_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();

        BubbleNavigationConstraintView bubbleNavigation = findViewById(R.id.nav);


        bubbleNavigation.setNavigationChangeListener((view, position) -> {

            switch (position){

                case 0:
                    open_fragment(false, wifiFragment);
                    break;

                case 1:
                    open_fragment(false, inicioFragment);
                    break;

                case 2:
                    //Toast.makeText(this, "pos "+String.valueOf(position), Toast.LENGTH_LONG).show();
                    break;
            }

        });


        bubbleNavigation.setCurrentActiveItem(1);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.user_profile, menu);
        this.menu = menu;

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.user = this.mAuth.getCurrentUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.profile:

                if(this.user == null){

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);

                }else {

                    Intent intent =  new Intent(this, Perfil.class);
                    startActivity(intent);

                }

                break;

        }

        return true;
    }

    void open_fragment(boolean add, Fragment fragment){

        if(add){

            getSupportFragmentManager().beginTransaction().add(R.id.content_main, fragment).commit();

        }else{

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                this.user = FirebaseAuth.getInstance().getCurrentUser();
                //this.menu.getItem(1).setVisible(true);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
