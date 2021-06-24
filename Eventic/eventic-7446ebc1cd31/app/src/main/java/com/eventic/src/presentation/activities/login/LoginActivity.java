package com.eventic.src.presentation.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eventic.src.presentation.activities.companyHomePage.companyHomePageActivity;
import com.eventic.src.presentation.activities.userHomePage.userHomePageActivity;

import com.eventic.src.presentation.activities.register.company.CompanyRegisterActivity;
import com.eventic.src.presentation.activities.register.user.UserRegisterActivity;
import com.example.eventic.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity implements LoginContract.View, View.OnClickListener {

    private LoginContract.Presenter mPresenter;
    private EditText emailAddress;
    private EditText password;
    private Button signInBut;
    private TextView signUpButton, forgotPass;
    private TextView companyAccount;
    private ProgressBar progressBar;
    private SignInButton googleText;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    /*@Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        googleSignIn();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        emailAddress = findViewById(R.id.EmailAddress);
        password = findViewById(R.id.Password);
        signInBut = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.SignUpText);
        forgotPass = findViewById(R.id.ForgotText);
        companyAccount = findViewById(R.id.EnterpriseAccount);
        progressBar = findViewById(R.id.loginProgressBar);
        googleText = findViewById(R.id.GoogleText);
        // Add any new view to setLoading() method to prevent spamming while loading

        signInBut.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
        companyAccount.setOnClickListener(this);
        googleText.setOnClickListener(this);

        mPresenter = new LoginPresenter(this);
        // Tried creating autofill hints on elements:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            emailAddress.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);
            password.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
        }
        SharedPreferences userPreferences = getUserPreferences();
        String token = userPreferences.getString("token", null);
        String role = userPreferences.getString("role", null);

        if(role != null) {
            if (token != null && role.equals("company")) mPresenter.changeToCompanyHomePage();
            if (token != null && (role.equals("customer") || role.equals("google"))) mPresenter.changeToUserHomePage();
        }

        Intent intent = getIntent();
        String addr = intent.getStringExtra("email");
        String pass = intent.getStringExtra("password");
        if (addr != null && !addr.isEmpty()) emailAddress.setText(intent.getStringExtra(addr));
        if (pass != null && !pass.isEmpty()) password.setText(pass);
        if (addr != null && !addr.isEmpty() && pass != null && !pass.isEmpty()) mPresenter.checkLogin(addr, pass);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signInButton) mPresenter.checkLogin(emailAddress.getText().toString(), password.getText().toString());
        else if(v.getId() == R.id.SignUpText) mPresenter.userSignUp();
        else if(v.getId() == R.id.ForgotText) mPresenter.userForgotText();
        else if(v.getId() == R.id.EnterpriseAccount) mPresenter.companySignUp();
        else if(v.getId() == R.id.GoogleText) googleSignIn();

    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Google Sign In", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

                mPresenter.userExists(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google Sign In", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Google Sign In", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google Sign In", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void clearInputs() {
        emailAddress.setText("");
        password.setText("");
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

    @Override
    public void sendVerificationEmail() {
        Intent intent = new Intent(LoginActivity.this, PopUpMailActivity.class);
        this.startActivity(intent);

    }

    @Override
    public void setUserExists(boolean userExists, GoogleSignInAccount account) throws IOException {
        if(userExists) mPresenter.checkLoginGoogle(account.getEmail(), "pablomotoso69");
        else mPresenter.registerUserGoogle(account.getGivenName(), account.getGivenName(), account.getEmail(), "pablomotoso69", "google");
    }

    @Override
    public void setLoading(boolean loading) {
        if (loading) progressBar.setVisibility(ProgressBar.VISIBLE);
        else progressBar.setVisibility(ProgressBar.INVISIBLE);
        emailAddress.setEnabled(!loading);
        password.setEnabled(!loading);
        signInBut.setEnabled(!loading);
        signUpButton.setEnabled(!loading);
        companyAccount.setEnabled(!loading);
        progressBar.setEnabled(!loading);
        googleText.setEnabled(!loading);
    }

    @Override
    public void changeToCompanyHomepage() {
        Intent intent = new Intent(LoginActivity.this, companyHomePageActivity.class);
        this.startActivity(intent);
        finishActivity();
    }

    public void changeToUserHomepage() {
        Intent intent = new Intent(LoginActivity.this, userHomePageActivity.class);
        this.startActivity(intent);
        finishActivity();
    }


    @Override
    public void changeToUserSignUp() {
        //clearInputs();

        Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void changeToCompanySignUp() {
        //clearInputs();

        Intent intent = new Intent(LoginActivity.this, CompanyRegisterActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void failedLogin() {
        emailAddress.setHighlightColor(getResources().getColor(R.color.red));
        password.setHighlightColor(getResources().getColor(R.color.red));

        /*
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.showAsDropDown(findViewById(R.id.loginView));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText( getResources().getText(R.string.login_error));
        toast.show();
        //Toast.makeText(this, getResources().getText(R.string.login_error), 5).show();
        */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getText(R.string.wrong_parameters));
        alertDialogBuilder
                .setMessage(getText(R.string.email_password_incorrect))
                .setCancelable(true);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    @Override
    public void failedLoginGoogle() {
        emailAddress.setHighlightColor(getResources().getColor(R.color.red));
        password.setHighlightColor(getResources().getColor(R.color.red));

        /*
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.showAsDropDown(findViewById(R.id.loginView));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText( getResources().getText(R.string.login_error));
        toast.show();
        //Toast.makeText(this, getResources().getText(R.string.login_error), 5).show();
        */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Error de autenticación");
        alertDialogBuilder
                .setMessage("Ya existe un usuario creado con este email")
                .setCancelable(true);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }



    @Override
    /**
     * Displays a popup message informing about the connection error
     */
    public void failedConnection() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getText(R.string.connection_error));
        alertDialogBuilder
                .setMessage(getText(R.string.connection_error_msg))
                .setCancelable(true);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public SharedPreferences getUserPreferences() {
        return getSharedPreferences("userPreferences", this.MODE_PRIVATE);
    }
}