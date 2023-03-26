package c2222648live.tees.ac.uk.snipchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private Button createAccount;
    private TextInputEditText userName, userPassword, userEmail;
    private CircleImageView imageViewProfile;

    boolean imageControl = false;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createAccount = findViewById(R.id.buttonSignupBtn);
        userEmail = findViewById(R.id.editTextEmail);
        userName = findViewById(R.id.editTextUsername);
        userPassword = findViewById(R.id.editTextPassword);
        imageViewProfile = findViewById(R.id.imageViewSignUp);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String username = userName.getText().toString();

                if (!email.equals("") && !password.equals("") && !username.equals(""))
                {
                    signUp(email,password,username);
                }
                else
                {
                    Toast.makeText(CreateAccountActivity.this,"Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChoser();
            }
        });

    }

    public void imageChoser()
    {
        Intent intent = new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewProfile);
            imageControl = true;
        }
        else
        {
            imageControl = false;
        }
    }


    public void signUp(String email,String password,String username)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    reference.child("Users").child(auth.getUid()).child("username").setValue(username);
                    reference.child("Users").child(auth.getUid()).child("email").setValue(email);

                    if (imageControl)
                    {
                        UUID randomID = UUID.randomUUID();
                        String imageName = "image/"+randomID+".jpg";
                        storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();
                                        reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(CreateAccountActivity.this, "write to database is successful.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CreateAccountActivity.this, "write to database is not successful.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }

                    Intent intent = new Intent (CreateAccountActivity.this,MainActivity.class);
                    intent.putExtra( "username",username);
                    startActivity(intent);
                    finish();
                }
                else
                {

                    Toast.makeText(CreateAccountActivity.this, "there is a problem.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}