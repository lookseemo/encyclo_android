package com.deedsit.android.bookworm.ui.authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.models.User;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.ServiceCallback;
import com.deedsit.android.bookworm.services.exceptions.NoConnectionException;
import com.deedsit.android.bookworm.ui.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * Fragment for user logging in
 *
 * @author Jack N
 * @version 1.0
 */
public class UserAuthFragment extends Fragment implements Transition.TransitionListener {

    public static final String TAG = "SIGN_IN_FRAGMENT";
    //UI components
    @BindView(R.id.user_auth_button)
    Button userCredButton;
    @BindView(R.id.email_input_text)
    EditText emailField;
    @BindView(R.id.pwd_input_field)
    EditText passwordField;
    @BindView(R.id.sign_up_pwd_confirm_input_text)
    EditText passwordConFirmField;
    @BindView(R.id.sign_up_fname_text)
    EditText firstNameField;
    @BindView(R.id.sign_up_lname_text)
    EditText lastNameField;
    @BindView(R.id.switch_link)
    TextView switchLink;
    @BindView(R.id.email_input_layout)
    TextInputLayout emailInputLayout;
    @BindView(R.id.pwd_input_layout)
    TextInputLayout passwordInputLayout;
    @BindView(R.id.sign_up_pwd_confirm_input_layout)
    TextInputLayout passwordConfirmInputLayout;
    @BindView(R.id.sign_up_fname_layout)
    TextInputLayout firstNameInputLayout;
    @BindView(R.id.sign_up_lname_layout)
    TextInputLayout lastNameInputLayout;
    @BindView(R.id.user_type_dropdown)
    Spinner userTypeDropDown;

    private ProgressDialog mProgressBar;

    //Transition components
    private Scene signInScene, signUpScene;
    private Transition transition;

    //variables
    private boolean signInOp = true;

    @Inject
    AuthRepo authRepo;

    /**
     * Empty constructor
     */
    public UserAuthFragment() {

    }

    /**
     * new instance method
     *
     * @return instance of SignInFragment
     */
    public static UserAuthFragment newInstance() {
        return new UserAuthFragment();

    }

    // [OVERRIDE METHODS]
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BookWormApplication) getActivity().getApplication()).getComponent().inject(this);
        mProgressBar = new ProgressDialog(getActivity());
        mProgressBar.setMessage("Checking Your details...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get master layout
        final View view = inflater.inflate(R.layout.sign_in_layout, container, false);

        //init views
        ButterKnife.bind(this, view);

        //Transition initialize
        RelativeLayout masterLayout = view.findViewById(R.id.user_auth_master_layout);
        ViewGroup startViews = (ViewGroup) getLayoutInflater().inflate(R.layout.sign_in_layout,
                masterLayout, false);
        ViewGroup endViews = (ViewGroup) getLayoutInflater().inflate(R.layout.sign_up_layout,
                masterLayout, false);
        signInScene = new Scene(masterLayout, startViews);
        signUpScene = new Scene(masterLayout, endViews);
        transition = new ChangeBounds();
        transition.setDuration(500);
        transition.setInterpolator(new AccelerateDecelerateInterpolator());
        transition.addListener(this);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {
        ButterKnife.bind(this, this.getView());
    }

    @Override
    public void onTransitionCancel(Transition transition) {

    }

    @Override
    public void onTransitionPause(Transition transition) {

    }

    @Override
    public void onTransitionResume(Transition transition) {

    }

    //Butter Knife Listeners
    @OnClick({R.id.switch_link, R.id.user_auth_button})
    void onClick(View view) {
        hideSoftKeyBoard();
        switch (view.getId()) {
            case R.id.switch_link:
                changeScene();
                break;
            case R.id.user_auth_button:
                if (signInOp)
                    signIn();
                else
                    signUp();
                break;
        }
    }

    @OnFocusChange({R.id.email_input_text, R.id.pwd_input_field, R.id
            .sign_up_pwd_confirm_input_text, R.id.sign_up_fname_text, R.id.sign_up_lname_text})
    void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            switch (view.getId()) {
                case R.id.email_input_text:
                    emailInputLayout.setErrorEnabled(false);
                    emailInputLayout.setError("");
                    break;
                case R.id.pwd_input_field:
                    passwordInputLayout.setErrorEnabled(false);
                    passwordInputLayout.setError("");
                    break;
                case R.id.sign_up_fname_text:
                    firstNameInputLayout.setErrorEnabled(false);
                    firstNameInputLayout.setError("");
                    break;
                case R.id.sign_up_lname_text:
                    lastNameInputLayout.setErrorEnabled(false);
                    lastNameInputLayout.setError("");
                    break;
                case R.id.sign_up_pwd_confirm_input_text:
                    passwordConfirmInputLayout.setErrorEnabled(false);
                    passwordConfirmInputLayout.setError("");
                    break;
            }
        } else
            validateForm(false);
    }

    // [END OVERRIDE METHODS]

    /**
     * Signing in Process with FirebaseAuth
     */
    private void signIn() {
        if (!validateForm(true))
            return;
        mProgressBar.show();
        String email = emailField.getText().toString().toLowerCase();
        String password = passwordField.getText().toString();
        authRepo.signIn(email, password, new ServiceCallback<User>() {
            @Override
            public void onSuccess(User data) {
                mProgressBar.dismiss();
                onSuccessAuth();
            }

            @Override
            public void onFailure(Exception e) {
                mProgressBar.dismiss();
                if (e instanceof NoConnectionException) {
                    Snackbar.make(getView(), "No Internet Connection...", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(), "Failed to sign in:" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataSuccessfullyAdded(User data) {

            }

            @Override
            public void onDataSuccessfullyChanged(User data) {

            }

            @Override
            public void onDataSuccessfullyRemoved(User data) {

            }
        });
    }

    private void signUp() {
        if (!validateForm(true)) {
            return;
        }

        String email = emailField.getText().toString().toLowerCase();
        String password = passwordField.getText().toString();
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        //Todo: Create a dropdown to select userType and get value here
        String userType = userTypeDropDown.getSelectedItem().toString();
        User user = new User();
        user.email = email;
        user.firstName = firstName;
        user.lastName = lastName;
        user.userType = userType;
        //set message
        mProgressBar.setMessage("Registering your account....");
        mProgressBar.show();
        authRepo.signUp(user, password, new ServiceCallback<User>() {
            @Override
            public void onSuccess(User returnedUser) {
                mProgressBar.dismiss();
                Snackbar.make(getView(), "Account " + returnedUser.username + " has been created successfully", Snackbar.LENGTH_SHORT).show();
                onSuccessAuth();
            }

            @Override
            public void onFailure(Exception e) {
                mProgressBar.dismiss();
                if (e instanceof NoConnectionException) {
                    Snackbar.make(getView(), "No Internet Connection...", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(), "Failed to sign up:" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataSuccessfullyAdded(User data) {

            }

            @Override
            public void onDataSuccessfullyChanged(User data) {

            }

            @Override
            public void onDataSuccessfullyRemoved(User data) {

            }
        });
    }

    private boolean validateForm(boolean buttonClicked) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String passwordConfirmed = passwordConFirmField.getText().toString().trim();
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!TextUtils.isEmpty(password) && password.length() >= 6) {
                    if (!signInOp) { // registration validation
                        if (!TextUtils.isEmpty(passwordConfirmed)) {
                            if (passwordConfirmed.equals(password)) {
                                if (!TextUtils.isEmpty(firstName)) {
                                    if (!TextUtils.isEmpty(lastName)) {
                                        return true;
                                    } else if (TextUtils.isEmpty(lastName) && buttonClicked) {
                                        displayErrorMessage(lastNameInputLayout, "First Name " +
                                                "Cannot " +
                                                "Be Empty");
                                    }
                                } else if (TextUtils.isEmpty(firstName) && buttonClicked) {
                                    displayErrorMessage(firstNameInputLayout, "First Name Cannot " +
                                            "Be Empty");
                                }
                            } else {
                                displayErrorMessage(passwordConfirmInputLayout, "Password Does " +
                                        "Not Match");
                            }
                        } else if (TextUtils.isEmpty(passwordConfirmed) && buttonClicked) {
                            displayErrorMessage(passwordConfirmInputLayout, "Confirm Password " +
                                    "Cannot Be Empty");
                        }
                    } else {
                        return true;
                    }
                } else if (TextUtils.isEmpty(password) && buttonClicked) {
                    displayErrorMessage(passwordInputLayout, "Password Cannot Be Empty");
                } else if (!TextUtils.isEmpty(password) && password.length() < 6) {
                    displayErrorMessage(passwordInputLayout, "Password Has To Be At Least 6 " +
                            "Characters");
                }
            } else {
                displayErrorMessage(emailInputLayout, "Wrong Email Format");
            }
        } else if (TextUtils.isEmpty(email) && buttonClicked) {
            displayErrorMessage(emailInputLayout, "Email Cannot Be Empty");
        }
        return false;
    }

    private void displayErrorMessage(TextInputLayout textInputLayout, String message) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(message);
    }

    /**
     * Method to start another activity after successfully logged in
     */
    private void onSuccessAuth() {
        //go to activity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    public void changeScene() {
        if (signInOp) {
            TransitionManager.go(signUpScene, transition);
            signInOp = false;
        } else {
            TransitionManager.go(signInScene, transition);
            signInOp = true;
        }
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(emailField.getWindowToken(), 0);
    }
}
