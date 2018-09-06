package com.deedsit.android.bookworm.services.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.deedsit.android.bookworm.config.AppConfig;
import com.deedsit.android.bookworm.models.User;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.ServiceCallback;
import com.deedsit.android.bookworm.services.exceptions.NoConnectionException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

/**
 * Created by steph on 12/11/2017.
 */

public class AuthRepoImpl implements AuthRepo {
    @NonNull
    private AppConfig appConfig;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference cloudDatabase;

    @Inject
    public AuthRepoImpl(@NonNull AppConfig appConfig) {
        this.appConfig = appConfig;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.cloudDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void signIn(String email, String password, final ServiceCallback<User> callback) {
        if (!appConfig.hasNetworkConnection()) {
            callback.onFailure(new NoConnectionException());
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            callback.onSuccess(getCurrentUser());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    @Override
    public void signUp(final User user, String password, final ServiceCallback<User> callback) {
        if (!appConfig.hasNetworkConnection()) {
            callback.onFailure(new NoConnectionException());
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser(), user, callback);
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    @Override
    public boolean signOut() {
        firebaseAuth.signOut();
        return true;
    }

    private void onAuthSuccess(FirebaseUser fUser, final User user, final ServiceCallback<User> callback) {
        final String username = usernameFromEmail(fUser.getEmail());
        user.username = username;
        cloudDatabase.child("users");
        String key = cloudDatabase.push().getKey();
        user.key = key;
        cloudDatabase.child("users").child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Nullable
    @Override
    public User getCurrentUser() {
        FirebaseUser fUser = firebaseAuth.getCurrentUser();
        if (fUser == null)
            return null;
        User user = new User();
        user.username = usernameFromEmail(fUser.getEmail());
        user.email = fUser.getEmail();
        user.firstName = fUser.getDisplayName();
        return user;
    }
}
