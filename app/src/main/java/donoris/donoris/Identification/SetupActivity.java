package donoris.donoris.Identification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import donoris.donoris.MainActivity;
import donoris.donoris.R;

public class SetupActivity extends AppCompatActivity
{
    private EditText UserName, FullName;
    private Button SaveInformationbutton;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    String currentUserID;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null)
        {
            currentUserID = mFirebaseUser.getUid();
        }
        UserProfileImageRef = FirebaseStorage.getInstance() .getReference() .child("Profile images");


        UserName = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_full_name);
        SaveInformationbutton = (Button) findViewById(R.id.setup_information_button);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        loadingBar = new ProgressDialog(this);


        SaveInformationbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SaveAccountSetupInformation();
            }
        });




        ProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });



        UsersRef = FirebaseDatabase.getInstance() .getReference() .child("Users") .child(currentUserID);
        if (mFirebaseUser != null)
        {
            UsersRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        if (dataSnapshot.hasChild("profileimage"))
                        {
                            String image = dataSnapshot.child("profileimage").getValue().toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);
                        }
                    }
                    else
                        {
                        Toast.makeText(SetupActivity.this, "S'il vous plaît sélectionnez d'abord l'image de profil.", Toast.LENGTH_SHORT).show();
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }



    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {

                loadingBar.setTitle("Image de profil");
                loadingBar.setMessage("Veuillez patienter pendant que nous mettons à jour votre image de profil...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {

                            Toast.makeText(SetupActivity.this, "Image de profil stockée avec succès sur Donoris...", Toast.LENGTH_LONG).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    final String downloadUrl = uri.toString();

                                    UsersRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(SetupActivity.this, "Image de profil stockée sur Donoris avec succès...", Toast.LENGTH_LONG).show();
                                                        loadingBar.dismiss();
                                                    }
                                                    else
                                                        {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Erreur survenue: " + message, Toast.LENGTH_LONG).show();
                                                        loadingBar.dismiss();
                                                        }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
            else
                {
                Toast.makeText(SetupActivity.this, "Erreur: l'image n'a pas été prise en compte. Essayez de nouveau.", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
                }
        }
    }





    private void SaveAccountSetupInformation()
    {
        String username = UserName.getText() .toString();
        String fullname = FullName.getText() .toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "S'il vous plaît, écrivez votre nom d'utilisateur...", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "S'il vous plaît, écrivez votre nom complet...", Toast.LENGTH_LONG).show();
        }


        else
        {
            loadingBar.setTitle("Sauvegarde des informations");
            loadingBar.setMessage("Veuillez patienter pendant que nous créons votre nouveau compte...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("status", "Bonjour, j'utilise l'affiche Donoris, développée par Matthieu Frain.");
            userMap.put("gender", "aucun");
            userMap.put("dob", "aucun");
            userMap.put("relationshipstatus", "aucun");
            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                   if(task.isSuccessful())
                   {
                       SendUserToMainActivity();
                       Toast.makeText(SetupActivity.this, "Votre compte a été créé avec succès.", Toast.LENGTH_LONG).show();
                       loadingBar.dismiss();
                   }

                   else
                   {
                       String message = task.getException() .getMessage();
                       Toast.makeText(SetupActivity.this, "Erreur survenue: " + message, Toast.LENGTH_LONG).show();
                       loadingBar.dismiss();
                   }
                }
            });
        }
    }





    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
