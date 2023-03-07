package c2222648live.tees.ac.uk.snipchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
// this is the main activity
public class MainActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail,editTextPassword;
    private Button buttonSignin,buttonSignup;
    private TextView textViewForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignin = findViewById(R.id.buttonSignin);
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewForget = findViewById(R.id.textViewForget);
    }
}