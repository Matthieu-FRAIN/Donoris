package donoris.donoris.Video_Post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import donoris.donoris.MainActivity;
import donoris.donoris.R;

public class VideoPostActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private VideoView videoField;
    private ImageButton SelectPostVideoVideo;
    private Button UpdatePostVideoButton;
    private EditText PostDescriptionVideo;

    private Uri VideoUri;
    private String Description;

    private StorageReference PostsVideosRefrence;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_post);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        PostsVideosRefrence = FirebaseStorage.getInstance() .getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("PostsVideos");



        videoField = (VideoView) findViewById(R.id.video_field);
        SelectPostVideoVideo = (ImageButton) findViewById(R.id.select_post_video_video);
        UpdatePostVideoButton = (Button) findViewById(R.id.update_post_video_button);
        PostDescriptionVideo = (EditText) findViewById(R.id.post_description_video);
        loadingBar = new ProgressDialog(this);


        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar() .setDisplayHomeAsUpEnabled(true);
        getSupportActionBar() .setDisplayShowHomeEnabled(true);
        getSupportActionBar() .setTitle("Nouvelle vidéo");


        SelectPostVideoVideo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 0);
            }
        });




        UpdatePostVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });
    }



    private void ValidatePostInfo()
    {
        Description = PostDescriptionVideo.getText().toString();

        if(VideoUri == null)
        {
            Toast.makeText(this, "S'il vous plaît, séléctionner votre vidéo...", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "S'il vous plaît, descrivez votre vidéo...", Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Nouvelle publication");
            loadingBar.setMessage("Veuillez patienter pendant que nous ajoutons votre nouvelle pubication...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringVideoToFirebaseStorage();
        }
    }

    private void StoringVideoToFirebaseStorage()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsVideosRefrence.child("Post Videos").child(VideoUri.getLastPathSegment() + postRandomName + ".mp4");

        filePath.putFile(VideoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                    Toast.makeText(VideoPostActivity.this, "Vidéo téléchargée...", Toast.LENGTH_LONG).show();


                    SavingPostInformationToDatabase();


                }
                else
                {
                    String messsage = task.getException().getMessage();
                    Toast.makeText(VideoPostActivity.this, "Une erreur est survenue... " +messsage, Toast.LENGTH_LONG).show();
                }
            }
        });
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
                    postsMap.put("postvideo", downloadUrl);
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
                                        Toast.makeText(VideoPostActivity.this, "Votre nouvelle vidéo est publiée.", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(VideoPostActivity.this, "Erreur lors de la publication.", Toast.LENGTH_LONG).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Uri uri = data.getData();
            try{
                videoField.setVideoURI(uri);
                videoField.start();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
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
        Intent mainIntent = new Intent(VideoPostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}

