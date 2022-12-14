package com.example.heal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heal.customerui.OtpActivity;
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

public class CustomerSignInActivity extends AppCompatActivity {
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    TextView btn;
    EditText inputEmail,inputPassword;
    Button btnLogin, phoneButton;
    private ProgressDialog mLoadingBar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(getApplicationContext(),CustomerHomeActivity.class));
            finish();}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signin);
        btn = findViewById(R.id.signupcust);
        inputEmail=findViewById(R.id.cusemail);
        inputPassword=findViewById(R.id.custpassword);


        btnLogin = findViewById(R.id.Logincust);
        btnLogin.setOnClickListener(v -> checkCredentials());


        mLoadingBar = new ProgressDialog(CustomerSignInActivity.this);
        btn.setOnClickListener(v -> startActivity(new Intent(CustomerSignInActivity.this,CustomerRegisterActivity.class)));

        signInButton=findViewById(R.id.btn_google_sign_in);
        mAuth = FirebaseAuth.getInstance();
        processrequest();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        phoneButton=(Button) findViewById(R.id.PhoneButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerSignInActivity.this, OtpActivity.class));
            }
        });

    }
    private void processrequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data ) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"Error in getting User's Information",Toast.LENGTH_LONG).show();
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),CustomerHomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"Sign In Error",Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }
    private void checkCredentials() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if(email.isEmpty() || !email.contains("@"))
        {
            showError(inputEmail,"Email is not valid!");
        }
        else if (password.isEmpty() || password.length()<7)
        {
            showError(inputPassword,"Password not valid.Enter 7 Charachetrs");
        }
        else
        {
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please wait");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();


            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(CustomerSignInActivity.this,CustomerHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        mLoadingBar.dismiss();
                    }
                    else if(!task.isSuccessful()){
                        Toast.makeText(CustomerSignInActivity.this, "Sign In Error", Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                    }

                }
            });
        }
    }

    private void showError(EditText input, String s) {

        input.setError(s);
        input.requestFocus();

    }
}