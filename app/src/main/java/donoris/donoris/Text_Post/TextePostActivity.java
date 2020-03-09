package donoris.donoris.Text_Post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import donoris.donoris.MainActivity;
import donoris.donoris.R;

public class TextePostActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private EditText PostDescriptionTexte;
    private Button UpdatePostTexteButton;

    private Uri TextUri;
    private String Description;

    private StorageReference PostsTextRefrence;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texte_post);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        PostsTextRefrence = FirebaseStorage.getInstance() .getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("PostsText");


        PostDescriptionTexte = (EditText) findViewById(R.id.post_description_texte);
        UpdatePostTexteButton = (Button) findViewById(R.id.update_post_texte_button);
        loadingBar = new ProgressDialog(this);



        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar() .setDisplayHomeAsUpEnabled(true);
        getSupportActionBar() .setDisplayShowHomeEnabled(true);
        getSupportActionBar() .setTitle("Nouveau texte");


        UpdatePostTexteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });
    }


    private void ValidatePostInfo()
    {
        Description = PostDescriptionTexte.getText().toString();

        if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "S'il vous plaît, écrivez votre texte...", Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Nouvelle publication");
            loadingBar.setMessage("Veuillez patienter pendant que nous ajoutons votre nouvelle pubication...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringTextToFirebaseStorage();
        }
    }




    private void StoringTextToFirebaseStorage()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsTextRefrence.child("Post Text").child(TextUri + postRandomName);

        SavingPostInformationToDatabase();
    }





    private void SavingPostInformationToDatabase()
    {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("fullname", userFullName);
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        SendUserToMainActivity();
                                        Toast.makeText(TextePostActivity.this, "Votre nouveau texte est publiée.", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(TextePostActivity.this, "Erreur lors de la publication.", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }




    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(TextePostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
