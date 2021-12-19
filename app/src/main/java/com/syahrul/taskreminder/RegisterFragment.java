package com.syahrul.taskreminder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class RegisterFragment extends Fragment {

    private EditText et_name, et_email, et_password, et_repassword;
    private Button btn_register;
    private FirebaseAuth mAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        et_name = view.findViewById(R.id.et_name);
        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);
        et_repassword = view.findViewById(R.id.et_repassword);
        btn_register = view.findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(V -> {
            String name = et_name.getText().toString();
            String email = et_email.getText().toString();
            String pass = et_password.getText().toString();
            String re_pass = et_repassword.getText().toString();

            if (!pass.equals(re_pass)){
                Toast.makeText(getActivity(), "Please check both having same password..", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(re_pass)) {
                Toast.makeText(getActivity(), "Please enter your credentials..", Toast.LENGTH_SHORT).show();
            } else{
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "User Registered..", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), "Now you can login", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w("FailRegister", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Fail: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        user.updateProfile(profileUpdates);
                    }
                });
            }

        });
        return view;
    }
}