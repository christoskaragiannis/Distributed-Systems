package com.example.trackingapp;

import android.animation.ValueAnimator;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.fragment.NavHostFragment;
import com.example.trackingapp.databinding.FragmentFirstBinding;
import com.dd.CircularProgressButton;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.messageTextView.setText(getString(R.string.welcome_message));
        CircularProgressButton loginButton = view.findViewById(R.id.loginMorphButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.usernameEditText.getText().toString();
                String server = binding.serverEditText.getText().toString();

                loginButton.setIndeterminateProgressMode(true);
                loginButton.setProgress(50);

                // Perform login logic here with the entered username and server

                // Simulate progress animation
                simulateSuccessProgress(loginButton);

                // Delayed navigation after a delay of 1 second
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Navigate to the next screen after login and pass arguments
                        Bundle args = new Bundle();
                        args.putString("username", username);
                        args.putString("server", server);
                        NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment, args);
                    }
                }, 2000); // Delay of 1 second (1000 milliseconds)
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(1000);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }
}