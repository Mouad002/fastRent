package com.example.fastrentv2.Controller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fastrentv2.MainActivity;
import com.example.fastrentv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity
{
    private TextView createAccount, forgotPassword;
    private TextInputEditText email,password;
    private AppCompatButton signInButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // firebase stuff
        mAuth = FirebaseAuth.getInstance();

        // initialize compenent
        email = findViewById(R.id.sign_in_tiet_email);
        password = findViewById(R.id.sign_in_tiet_password);
        signInButton = findViewById(R.id.sign_in_acb_signin);
        forgotPassword = findViewById(R.id.sign_in_tv_password);
        createAccount = findViewById(R.id.sign_in_tv_account);

        // actions
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                signIn(view);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                forgotPassword(view);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getBaseContext(),SignUpActivity.class));
                finish();
            }
        });

        // progress dialog

    }

    public void signIn(View view)
    {
        String emailText = email.getEditableText().toString().trim();
        String passwordText = password.getEditableText().toString().trim();

        if(emailText.isEmpty())
        {
            email.setError("email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            email.setError("please provide valid email");
            email.requestFocus();
            return;
        }

        if(passwordText.isEmpty())
        {
            password.setError("password is required!");
            password.requestFocus();
            return;
        }

        if(passwordText.length()<6)
        {
            password.setError("the length of the passsword should be more than 6 ");
            password.requestFocus();
            return;
        }

        // declare and show the progress dialog
        ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // firebase Authentication
        mAuth.signInWithEmailAndPassword(emailText,passwordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()) // verify if the user have verified his account
                    {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class)); // open main activity
                        finish(); // close the current activity
                    }else
                    {
                        user.sendEmailVerification();
                        Toast.makeText(SignInActivity.this, "check your email to verify your account", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(SignInActivity.this, "Failed to sign in!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss(); // the progress dialog disappear
            }
        });

    }

    // the operations that happen when you click on forgot password
    private void forgotPassword(View view)
    {
        String emailText = email.getEditableText().toString().trim();

        if(emailText.isEmpty())
        {
            email.setError("email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            email.setError("please provide valid email");
            email.requestFocus();
            return;
        }

        // declare and show the progress dialog
        ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        mAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SignInActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(SignInActivity.this, "Try again! Something wrong happened", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss(); // the progress dialog disappear
            }
        });

    }

    // to quite the application
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setMessage("do you want to leave ?");
        builder.setCancelable(true);
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SignInActivity.super.onBackPressed();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}