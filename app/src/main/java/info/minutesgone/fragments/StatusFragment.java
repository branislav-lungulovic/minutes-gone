package info.minutesgone.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import info.minutesgone.R;
import info.minutesgone.shared.ActivityUtils;
import info.minutesgone.shared.ColorUtils;
import info.androidminiloggr.Logger;
import info.minutesgone.shared.StringUtils;
import info.minutesgone.tasks.CallLogData;
import info.minutesgone.tasks.OnTaskEnd;
import info.minutesgone.tasks.ParseCallLogTask;
import pl.pawelkleczkowski.customgauge.CustomGauge;



public class StatusFragment extends Fragment implements OnTaskEnd<CallLogData> {

    private CustomGauge gauge;
    private TextView textViewValue;
    private TextView textViewHeader;
    private TextView textViewTo;

    private TextView tvDateInterval;
    private TextView tvPlanVal;
    private TextView tvIncludedCallMinutesVal;
    private TextView tvExcludedCallMinutesVal;


    private GridLayout gaugeMainLayout;
   // private TextView emptyText;

    private static boolean firstStart =true;

    private static final Logger logger = Logger.getLogger(StatusFragment.class.getName());

    private static final int URL_LOADER = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.d("savedInstanceState: ", savedInstanceState, ", firstStart: ", firstStart);
        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        logger.d("onPause: ");
        firstStart =false;
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.d("onResume", ", firstStart: ", firstStart);
    }

    @Override
    public void onStop() {
        super.onStop();
        logger.d("onStop: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        logger.d("onSaveInstanceState: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.status));
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        gauge = (CustomGauge) view.findViewById(R.id.gauge);

        textViewValue = (TextView) view.findViewById(R.id.textViewValue);
        textViewValue.setText("0%");

        //textViewHeader = (TextView) view.findViewById(R.id.textViewHeader);
        textViewTo = (TextView) view.findViewById(R.id.textViewTo);
        tvDateInterval = (TextView) view.findViewById(R.id.tvDateInterval);
        tvPlanVal = (TextView) view.findViewById(R.id.tvPlanVal);
        tvIncludedCallMinutesVal = (TextView) view.findViewById(R.id.tvIncludedCallMinutesVal);
        tvExcludedCallMinutesVal = (TextView) view.findViewById(R.id.tvExcludedCallMinutesVal);

        gaugeMainLayout = (GridLayout) view.findViewById(R.id.gaugeMainLayout);

        hideData();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    ActivityUtils.MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }else{

            new ParseCallLogTask(this).execute(getActivity());
        }

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    ActivityUtils.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivityUtils.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new ParseCallLogTask(this).execute(getActivity());

                } else {

                    Toast.makeText(this.getContext(), "Please enable read call logs access.", Toast.LENGTH_LONG).show();
                }

                return;
            }

            case ActivityUtils.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Toast.makeText(this.getContext(), "Please enable read phone state access.", Toast.LENGTH_LONG).show();


                }

            }


        }
    }

    @Override
    public void onTaskEnd(CallLogData val) {
        logger.d("onTaskEnd called with value: ",val);
        if(isAdded()){

            if(val.isPermissionError()){
                Toast.makeText(getContext(), "Please enable call logs read access.", Toast.LENGTH_LONG).show();
                return;
            }
            showData(val);
        }

    }


    private void showData(final CallLogData data) {

        gaugeMainLayout.setAlpha(1.0f);

        int padding = 0;
        int maxValForPadding = Math.max(data.getPlanaLimit(),Math.max(data.getExcludedDuration(),data.getIncludedDuration()));
        if(maxValForPadding>0)padding=Integer.toString(maxValForPadding).length();

        LocalDateTime normalizedEndDate = data.getDateTo().minusDays(1);
        ActivityUtils.setHtmlText(tvDateInterval,getString(R.string.measured_time_interval, DateTimeFormat.mediumDate().print(data.getDateFrom()),DateTimeFormat.mediumDate().print(normalizedEndDate)));
        ActivityUtils.setHtmlText(tvPlanVal,getString(R.string.plan_limit_value, StringUtils.padLeft(data.getPlanaLimit().toString(),padding,"&nbsp;")));
        ActivityUtils.setHtmlText(tvIncludedCallMinutesVal,getString(R.string.call_minutes_val,StringUtils.padLeft(data.getIncludedDuration().toString(),padding,"&nbsp;")));
        ActivityUtils.setHtmlText(tvExcludedCallMinutesVal,getString(R.string.free_minutes_val,StringUtils.padLeft(data.getExcludedDuration().toString(),padding,"&nbsp;")));


        if(firstStart){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    logger.d("postDelayed called");
                    if(data.getPlanaLimitPercent() > 0)animateData(data.getPlanaLimitPercent());
                }
            }, 600);
        }else{
            if(data.getPlanaLimitPercent() > 0)animateData(data.getPlanaLimitPercent());
        }

        ActivityUtils.checkIfToSendAlert(getContext(),data.getPlanaLimitPercent());


    }

    private void hideData() {


        gaugeMainLayout.setAlpha(0.15f);
        textViewTo.setText(Integer.toString(ActivityUtils.readPreferences(getActivity()).getMinutes()));
        gauge.setValue(0);
        textViewValue.setText("--");
    }

    private void animateData(Integer val){

        ObjectAnimator animation = ObjectAnimator.ofInt(gauge, "value", 0, val);

        int duration = Math.round(1400*(val/100f));
        logger.d("duration: ",duration);
        animation.setDuration(duration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                textViewValue.setText(Integer.toString(animatedValue) + "%");
                int endColor = ContextCompat.getColor(getActivity(),R.color.gaugePointStartColor);
                int startColor = ContextCompat.getColor(getActivity(),R.color.gaugeStrokeColor);

                double calcPosition = Math.floor(animatedValue/10);
                int[] colors = {startColor, endColor};
                float[] positions = {1, 10};
                int calcColor = ColorUtils.getColorFromGradient( colors, positions, (int)calcPosition );
                textViewValue.setTextColor(calcColor);

            }
        });

        animation.start();
    }
}
