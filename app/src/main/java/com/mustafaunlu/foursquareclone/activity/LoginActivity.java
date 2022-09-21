package com.mustafaunlu.foursquareclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mustafaunlu.foursquareclone.R;
import com.mustafaunlu.foursquareclone.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ParseUser user= ParseUser.getCurrentUser();
        if(user != null){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }



    public void signUp(View view){

        if(binding.usernameEditText.getText() != null && binding.passwordEditText.getText() != null){

            ParseUser user=new ParseUser();
            user.setUsername(binding.usernameEditText.getText().toString());
            user.setPassword(binding.passwordEditText.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if( e != null){
                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                }
            });



        }

    }

    public void signIn(View view){

        ParseUser.logInInBackground(binding.usernameEditText.getText().toString(), binding.passwordEditText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if( e != null){
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Welcome "+user.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);

                }
            }
        });

    }
}