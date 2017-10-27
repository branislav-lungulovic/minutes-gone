package info.talkalert.callbacks;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.talkalert.R;
import info.talkalert.activities.MainActivity;
import info.talkalert.adapters.ExcludedPhoneNumberRecyclerViewAdapter;
import info.talkalert.fragments.ExcludedPhoneNumberFragment;
import info.talkalert.models.ExcludedPhoneNumbers;

public class Toolbar_ActionMode_Callback implements ActionMode.Callback {

    private ExcludedPhoneNumberFragment fragment;
    private ExcludedPhoneNumberRecyclerViewAdapter recyclerView_adapter;
    private List<ExcludedPhoneNumbers> message_models;
    private boolean isListViewFragment;


    public Toolbar_ActionMode_Callback(ExcludedPhoneNumberFragment fragment, ExcludedPhoneNumberRecyclerViewAdapter recyclerView_adapter, List<ExcludedPhoneNumbers> message_models, boolean isListViewFragment) {
        this.fragment = fragment;
        this.recyclerView_adapter = recyclerView_adapter;
        this.message_models = message_models;
        this.isListViewFragment = isListViewFragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

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
