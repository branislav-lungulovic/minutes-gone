package info.talkalert.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import info.talkalert.R;
import info.talkalert.shared.ActivityUtils;
import info.talkalert.shared.ColorUtils;
import info.talkalert.shared.Logger;
import info.talkalert.shared.LoggerUtils;
import info.talkalert.shared.StringUtils;
import info.talkalert.tasks.CallLogData;
import info.talkalert.tasks.OnTaskEnd;
import info.talkalert.tasks.ParseCallLogTask;
import pl.pawelkleczkowski.customgauge.CustomGauge;



public class StatusFragment extends Fragment implements OnTaskEnd<CallLogData> {

    private CustomGauge gauge;
    private RelativeLayout gaugeContainer;
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

    private static Logger logger = LoggerUtils.getLogger(StatusFragment.class.getName());

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
        gaugeContainer = (RelativeLayout) view.findViewById(R.id.gaugeContainer);

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

        new ParseCallLogTask(this).execute(getActivity());

        TelephonyManager telemamanger = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String getSimSerialNumber = telemamanger.getSimSerialNumber();
        final String simCountry = telemamanger.getSimCountryIso();
        String getSimNumber = telemamanger.getLine1Number();


        return view;

    }

    @Override
    public void onTaskEnd(CallLogData val) {
        logger.d("onTaskEnd called with value: ",val);
        if(isAdded()){
            showData(val);
        }

    }


    public void showData(final CallLogData data) {

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



    }

    public void hideData() {


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
