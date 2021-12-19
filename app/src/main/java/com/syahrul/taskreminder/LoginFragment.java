package com.syahrul.taskreminder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    private EditText et_email;
    private EditText pwd;
    private Button btn_login;
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        et_email = view.findViewById(R.id.et_email);
        pwd = view.findViewById(R.id.pwd);
        btn_login = view.findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(v -> {
            String email = et_email.getText().toString();
            String password = pwd.getText().toString();
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Please enter your credentials..", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                            // on below line we are hiding our progress bar.

                            Toast.makeText(getActivity(), "Login Successful..", Toast.LENGTH_SHORT).show();
                            // on below line we are opening our mainactivity.
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                        } else {
                            // hiding our progress bar and displaying a toast message.
                            Toast.makeText(getActivity(), "Fail: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                }
            });
        });
        return view;


    }
}