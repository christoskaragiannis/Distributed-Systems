package com.example.trackingapp;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.trackingapp.databinding.FragmentThirdBinding;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        BarChart chart1 = binding.chart1;
        BarChart chart2 = binding.chart2;
        BarChart chart3 = binding.chart3;

        // Retrieve the stats from the arguments
        Map<String, Map.Entry<Double, Double>> stats = (Map<String, Map.Entry<Double, Double>>) getArguments().getSerializable("stats");
        if (stats != null) {
            List<Map.Entry<Double, Double>> entries = new ArrayList<>(stats.values());

            // Chart 1 - Total Ascent
            Map.Entry<Double, Double> totalAscentEntry = entries.get(0);
            double userTotalAscent = totalAscentEntry.getKey();
            double averageTotalAscent = totalAscentEntry.getValue();

            List<BarEntry> chart1Data = new ArrayList<>();
            chart1Data.add(new BarEntry(0, new float[]{(float) userTotalAscent}));
            chart1Data.add(new BarEntry(1, new float[]{(float) averageTotalAscent}));

            BarDataSet dataSet1 = new BarDataSet(chart1Data, "Total Ascent");
            dataSet1.setColors(new int[]{ColorTemplate.rgb("#FF7171"), ColorTemplate.rgb("#71C8FF")});


            BarData data1 = new BarData(dataSet1);
            data1.setBarWidth(0.4f);
            chart1.setData(data1);
            chart1.animateY(4000);
            chart1.getDescription().setText("User vs Average");
            chart1.getAxisLeft().setEnabled(false);
            chart1.getAxisRight().setLabelCount(4, false);
            chart1.getAxisRight().setDrawAxisLine(true);
            chart1.getAxisRight().setDrawGridLines(true);
            chart1.getXAxis().setDrawAxisLine(true);
            chart1.getXAxis().setDrawGridLines(true);
            chart1.getAxisRight().setAxisMinimum(0f);
            chart1.getAxisLeft().setAxisMinimum(0f);
            chart1.getAxisRight().setGridLineWidth(1.0f);
            chart1.getAxisRight().setGridColor(Color.BLACK);
            chart1.getAxisRight().setAxisLineColor(Color.BLACK);
            chart1.getAxisRight().setTextColor(Color.BLACK);
            chart1.getXAxis().setGridLineWidth(1.0f);
            chart1.getXAxis().setGridColor(Color.BLACK);
            chart1.getXAxis().setAxisLineColor(Color.BLACK);
            chart1.getXAxis().setTextColor(Color.BLACK);
            chart1.getXAxis().setDrawLabels(false);


            // Chart 2 - Total Distance
            Map.Entry<Double, Double> totalDistanceEntry = entries.get(1);
            double userTotalDistance = totalDistanceEntry.getKey();
            double averageTotalDistance = totalDistanceEntry.getValue();

            List<BarEntry> chart2Data = new ArrayList<>();
            chart2Data.add(new BarEntry(0, new float[]{(float) userTotalDistance}));
            chart2Data.add(new BarEntry(1, new float[]{(float) averageTotalDistance}));

            BarDataSet dataSet2 = new BarDataSet(chart2Data, "Total Distance");
            dataSet2.setColors(new int[]{ColorTemplate.rgb("#FF7171"), ColorTemplate.rgb("#71C8FF")});

            BarData data2 = new BarData(dataSet2);
            data2.setBarWidth(0.4f);
            chart2.setData(data2);
            chart2.animateY(4000);
            chart2.getDescription().setText("User vs Average");
            chart2.getAxisRight().setLabelCount(4, false);
            chart2.getAxisLeft().setEnabled(false);
            chart2.getAxisRight().setDrawAxisLine(true);
            chart2.getAxisRight().setDrawGridLines(true);
            chart2.getXAxis().setDrawAxisLine(true);
            chart2.getXAxis().setDrawGridLines(true);
            chart2.getAxisRight().setAxisMinimum(0f);
            chart2.getAxisLeft().setAxisMinimum(0f);
            chart2.getAxisRight().setGridLineWidth(1.0f);
            chart2.getAxisRight().setGridColor(Color.BLACK);
            chart2.getAxisRight().setAxisLineColor(Color.BLACK);
            chart2.getAxisRight().setTextColor(Color.BLACK);
            chart2.getXAxis().setGridLineWidth(1.0f);
            chart2.getXAxis().setGridColor(Color.BLACK);
            chart2.getXAxis().setAxisLineColor(Color.BLACK);
            chart2.getXAxis().setTextColor(Color.BLACK);
            chart2.getXAxis().setDrawLabels(false);


            // Chart 3 - Total Time
            Map.Entry<Double, Double> totalTimeEntry = entries.get(2);
            double userTotalTime = totalTimeEntry.getKey();
            double averageTotalTime = totalTimeEntry.getValue();

            List<BarEntry> chart3Data = new ArrayList<>();
            chart3Data.add(new BarEntry(0, new float[]{(float) userTotalTime}));
            chart3Data.add(new BarEntry(1, new float[]{(float) averageTotalTime}));

            BarDataSet dataSet3 = new BarDataSet(chart3Data, "Total Time");
            dataSet3.setColors(new int[]{ColorTemplate.rgb("#FF7171"), ColorTemplate.rgb("#71C8FF")});


            BarData data3 = new BarData(dataSet3);
            data3.setBarWidth(0.4f);
            chart3.setData(data3);
            chart3.animateY(4000);
            chart3.getDescription().setText("User vs Average");
            chart3.getAxisRight().setLabelCount(4, false);
            chart3.getAxisLeft().setEnabled(false);
            chart3.getAxisRight().setDrawAxisLine(true);
            chart3.getAxisRight().setDrawGridLines(true);
            chart3.getXAxis().setDrawAxisLine(true);
            chart3.getXAxis().setDrawGridLines(true);
            chart3.getAxisRight().setAxisMinimum(0f);
            chart3.getAxisLeft().setAxisMinimum(0f);
            chart3.getAxisRight().setGridLineWidth(1.0f);
            chart3.getAxisRight().setGridColor(Color.BLACK);
            chart3.getAxisRight().setAxisLineColor(Color.BLACK);
            chart3.getAxisRight().setTextColor(Color.BLACK);
            chart3.getXAxis().setGridLineWidth(1.0f);
            chart3.getXAxis().setGridColor(Color.BLACK);
            chart3.getXAxis().setAxisLineColor(Color.BLACK);
            chart3.getXAxis().setTextColor(Color.BLACK);
            chart3.getXAxis().setDrawLabels(false);




            // Refresh the charts
            chart1.invalidate();
            chart2.invalidate();
            chart3.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}