package com.guauapp.ui.logIn;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.guauapp.MainActivity;
import com.guauapp.R;
import com.guauapp.databinding.FragmentLoginBinding;
import com.guauapp.model.DogsDAO;
import com.guauapp.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogInFragment extends Fragment {
    private FragmentLoginBinding binding;
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private NavController navController;
    private BottomNavigationView navView;
    private DogsDAO dogsDAO = new DogsDAO();
    private List<String> dogList = new ArrayList<>();
    public static FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        configureFirebase();

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navView = requireActivity().findViewById(R.id.nav_view);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this.requireContext(), googleSignInOptions);

        enableBottomBar(false);

        setDogList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void enableBottomBar(boolean enable){
        for (int i = 0; i < navView.getMenu().size(); i++) {
            navView.getMenu().getItem(i).setEnabled(enable);
        }
    }

    private void configureFirebase() {
        mAuth = FirebaseAuth.getInstance();
        signInButton = binding.signInButton;
        signInButton.setOnClickListener(this::signIn);
        signInButton.setVisibility(View.VISIBLE);
    }

    private void setDogList() {
        dogsDAO.getDogsIdAsync().thenAccept(dogsList -> {
            dogList.addAll(dogsList);
        }).exceptionally(exception -> null);
    }

    private void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                //lanzarActividad();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this.requireContext(), "Bienvenido " + user.getDisplayName()
                    + "[" + user.getEmail() + "]", Toast.LENGTH_LONG).show();
            LogInFragment.user = user;
            if(dogList.contains(user.getUid())) {
                enableBottomBar(true);
                navController.navigate(R.id.navigation_home);
            }else{
                navController.navigate(R.id.navigation_profile);
            }

            signInButton.setVisibility(View.GONE);

        } else {
            signInButton.setVisibility(View.VISIBLE);
        }
    }
}
