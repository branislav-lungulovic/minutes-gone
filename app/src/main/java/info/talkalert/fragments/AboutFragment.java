package info.talkalert.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.talkalert.R;
import info.talkalert.shared.ActivityUtils;
import info.talkalert.shared.StringUtils;


public class AboutFragment extends Fragment {

    private TextView txtIconAuthorCredit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        txtIconAuthorCredit = (TextView) view.findViewById(R.id.txtIconAuthorCredit);

        ActivityUtils.setHtmlText(txtIconAuthorCredit,getString(R.string.about_credit_text));
        Linkify.addLinks(txtIconAuthorCredit, Linkify.WEB_URLS);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.about));

        return view;
    }

}
