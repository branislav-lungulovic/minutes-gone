package info.talkalert.callbacks;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import info.talkalert.R;
import info.talkalert.adapters.ExcludedPhoneNumberRecyclerViewAdapter;
import info.talkalert.fragments.ExcludedPhoneNumberFragment;
import info.talkalert.models.ExcludedPhoneNumbers;

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
