package donoris.donoris.Identification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import donoris.donoris.MainActivity;
import donoris.donoris.R;

public class RegisterActivity extends AppCompatActivity
{

    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();


        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        CreateAccountButton = (Button) findViewById(R.id.register_create_account);
        loadingBar = new ProgressDialog(this);


        CreateAccountButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();
            }
        });
    }


    @Override
    protected void onStart()
    {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }




    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }




    private void CreateNewAccount()
    {
        String email = UserEmail.getText(). toString();
        String password = UserPassword.getText(). toString();
        String confirmPassword = UserConfirmPassword.getText(). toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "S'il vous plaît, écrivez votre adresse électronique...", Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "S'il vous plaît, écrivez votre mot de passe...", Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this, "S'il vous plaît, confirmer votre mot de passe...", Toast.LENGTH_LONG).show();
        }
        else if (!password.equals(confirmPassword))
        {
            Toast.makeText(this, "Votre mot de passe n'est pas identique à votre mot de passe de confirmation ...", Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Créer un nouveau compte");
            loadingBar.setMessage("Veuillez patienter pendant que nous créons votre nouveau compte...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendUserToSetupActivity();

                                Toast.makeText(RegisterActivity.this, "Vous êtes authentifié avec succès...", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException() .getMessage();
                                Toast.makeText(RegisterActivity.this, "Erreur survenue: " + message, Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }




    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
