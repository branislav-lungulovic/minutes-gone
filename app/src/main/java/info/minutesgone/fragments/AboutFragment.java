/*
 * minutes-gone
 * Copyright (C) 2017.  Author: Branislav Lungulovic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package info.minutesgone.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.minutesgone.R;
import info.minutesgone.shared.ActivityUtils;


public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView txtIconAuthorCredit = (TextView) view.findViewById(R.id.txtIconAuthorCredit);
        TextView txtIconCredit = (TextView) view.findViewById(R.id.txtIconCredit);

        ActivityUtils.setHtmlText(txtIconAuthorCredit,getString(R.string.about_credit_text));
        Linkify.addLinks(txtIconAuthorCredit, Linkify.WEB_URLS);

        ActivityUtils.setHtmlText(txtIconCredit,getString(R.string.about_credit_icons));
        Linkify.addLinks(txtIconCredit, Linkify.WEB_URLS);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.about));

        return view;
    }

}
