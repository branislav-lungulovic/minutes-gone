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

package info.minutesgone.callbacks;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import info.minutesgone.R;
import info.minutesgone.adapters.ExcludedPhoneNumberRecyclerViewAdapter;
import info.minutesgone.fragments.ExcludedPhoneNumberFragment;
import info.minutesgone.models.ExcludedPhoneNumbers;

public class Toolbar_ActionMode_Callback implements ActionMode.Callback {

    private final ExcludedPhoneNumberFragment fragment;
    private final ExcludedPhoneNumberRecyclerViewAdapter recyclerView_adapter;


    public Toolbar_ActionMode_Callback(ExcludedPhoneNumberFragment fragment, ExcludedPhoneNumberRecyclerViewAdapter recyclerView_adapter, List<ExcludedPhoneNumbers> message_models, boolean isListViewFragment) {
        this.fragment = fragment;
        this.recyclerView_adapter = recyclerView_adapter;
        List<ExcludedPhoneNumbers> message_models1 = message_models;
        boolean isListViewFragment1 = isListViewFragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {


            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:


                //If current fragment is recycler view fragment
                if (fragment != null)
                    //If recycler fragment not null
                    fragment.deleteRows();//delete selected rows
                break;

        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        recyclerView_adapter.removeSelection();  // remove selection

        if (fragment != null)
            fragment.setNullToActionMode();//Set action mode null
    }
}
