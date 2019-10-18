package com.ryansoft.internet.speed.meter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Trace;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class AboutFragment extends BaseFragment {
    private View view;
    private RewardedVideoAd mAd;
    private TextView version;
    Button button;
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =   inflater.inflate(R.layout.fragment_about, container, false);
        version = view.findViewById(R.id.version);
        version.setText("("+version.getText()+" "+appVersion()+")");
        button = view.findViewById(R.id.button);
        button.setText("Loading...");
        button.setEnabled(false);
        MobileAds.initialize(getContext(), "ca-app-pub-7518503677713099~9509477831");
        // Use an activity context to get the rewarded video instance.
        mAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                button.setText("SUPPORT US");
                button.setEnabled(true);
                button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                button.setEnabled(false);
                Toast.makeText(getContext(), "Thank you for supporting us.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {
                button.setVisibility(View.INVISIBLE);
                button.setEnabled(false);
            }
        });
        loadRewardedVideoAd();

        Button button=(Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAd.isLoaded()) {
                    mAd.show();
                }
            }
        });
        return view;
    }
    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-7518503677713099/2289463367",
                new AdRequest.Builder().build());
    }

}
