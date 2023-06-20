package com.example.trackingapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.trackingapp.databinding.FragmentSecondBinding;
import net.GPXFile;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.Serializable;
import java.util.Map;


public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private GPXFile receivedGpxFile;
    private PopupWindow popupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String serverAddress = getArguments().getString("server");
                        String userId = getArguments().getString("username");
                        sendFileToServer(requireContext(), serverAddress, userId, uri);
                    }
                }
        );

        // Navigate to the next screen and pass arguments
        binding.selectFileButton.setOnClickListener(v -> openFilePicker(filePickerLauncher));

        binding.statisticsButton.setOnClickListener(v -> {
            if (receivedGpxFile != null) {
                String serverAddress = getArguments().getString("server");
                String userId = getArguments().getString("username");
                AndroidNetworkUtils.sendStatsToServer(requireContext(), serverAddress, userId, new AndroidNetworkUtils.OnStatsReceivedListener() {
                    @Override
                    public void onStatsReceived(Map<String, Map.Entry<Double, Double>> stats) {
                        Bundle args = new Bundle();
                        args.putString("username", userId);
                        args.putString("server", serverAddress);
                        args.putSerializable("stats", (Serializable) stats);

                        // Navigate to the Third Fragment and pass the arguments
                        NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_ThirdFragment, args);
                    }
                });
            } else {
                showFileSelectionAlert();
            }
        });
    }

    private void showFileSelectionAlert() {
        Toast.makeText(requireContext(), "Please select a file", Toast.LENGTH_SHORT).show();
    }


    private void showStatisticsPopup() {
        if (receivedGpxFile != null) {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View popupView = inflater.inflate(R.layout.popup_statistics, null);

            // Find the root view of the popup window
            ConstraintLayout popupRootView = popupView.findViewById(R.id.popupRootView);
            popupRootView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background));


            // Apply animation using AndroidViewAnimations library
            YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(popupRootView);

                // Create the popup window
            popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );



            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Make the background transparent
            popupWindow.setElevation(10); // Adjust the elevation if needed

            // Update the height and width of the popup window
            int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);
            int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
            popupWindow.setHeight(popupHeight);
            popupWindow.setWidth(popupWidth);

            // Find the TextViews
            TextView averageSpeedTextView = popupView.findViewById(R.id.averageSpeedTextView);
            TextView totalAscentTextView = popupView.findViewById(R.id.totalAscentTextView);
            TextView totalDistanceTextView = popupView.findViewById(R.id.totalDistanceTextView);
            TextView totalTimeTextView = popupView.findViewById(R.id.totalTimeTextView);

            // Set gravity to center for the TextViews
            averageSpeedTextView.setGravity(Gravity.CENTER);
            totalAscentTextView.setGravity(Gravity.CENTER);
            totalDistanceTextView.setGravity(Gravity.CENTER);
            totalTimeTextView.setGravity(Gravity.CENTER);

            // Set the text for the TextViews
            double averageSpeed = receivedGpxFile.getStatistic("averageSpeed");
            double totalAscent = receivedGpxFile.getStatistic("totalAscent");
            double totalDistance = receivedGpxFile.getStatistic("totalDistance");
            double totalTime = receivedGpxFile.getStatistic("totalTime");

            averageSpeedTextView.setText("Average Speed: " + String.format("%.2f", averageSpeed) + " m/s");
            totalAscentTextView.setText("Total Ascent: " + String.format("%.2f", totalAscent) + " m");
            totalDistanceTextView.setText("Total Distance: " + String.format("%.2f", totalDistance) + " m");
            totalTimeTextView.setText("Total Time: " + String.format("%.2f", totalTime) + " s");

            popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);
        } else {
            Toast.makeText(requireContext(), "File not received yet", Toast.LENGTH_SHORT).show();
        }
    }


    private void openFilePicker(ActivityResultLauncher<String> filePickerLauncher) {
        filePickerLauncher.launch("*/*");
    }

    private void sendFileToServer(Context context, String serverAddress, String userId, Uri fileUri) {
        AndroidNetworkUtils.sendFileToServer(context, serverAddress, userId, fileUri, receivedFile -> {
            receivedGpxFile = receivedFile;
            showStatisticsPopup();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}