package info.talkalert.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.talkalert.activities.MainActivity;
import info.talkalert.adapters.ExcludedPhoneNumberRecyclerViewAdapter;
import info.talkalert.R;
import info.talkalert.callbacks.Toolbar_ActionMode_Callback;
import info.talkalert.tasks.persistence.PersistenceServiceAsyncTask;
import info.talkalert.listeners.RecyclerClick_Listener;
import info.talkalert.listeners.RecyclerTouchListener;
import info.talkalert.models.ExcludedPhoneNumbers;
import info.talkalert.tasks.persistence.OnDeleteTaskEnd;
import info.talkalert.tasks.persistence.OnGetAllTaskEnd;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ExcludedPhoneNumberFragment extends Fragment implements OnGetAllTaskEnd<ExcludedPhoneNumbers>, OnDeleteTaskEnd {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    List<ExcludedPhoneNumbers> excludedPhoneNumbers;

    private RecyclerView rView;
    private TextView emptyText;
    private ExcludedPhoneNumberRecyclerViewAdapter adapter;
    private ActionMode mActionMode;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExcludedPhoneNumberFragment() {
    }


    public static ExcludedPhoneNumberFragment newInstance(int columnCount) {
        ExcludedPhoneNumberFragment fragment = new ExcludedPhoneNumberFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        setRetainInstance(true);
    }

    private PersistenceServiceAsyncTask<ExcludedPhoneNumbers> createPersistanceTask(){
        return new PersistenceServiceAsyncTask<>(((MainActivity)getActivity()).getPersistenceService(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_excludedphonenumber_list, container, false);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("excludedPhoneNumber", new ExcludedPhoneNumbers());

                showEditPhoneDialog(bundle);
            }
        });

        // Set the adapter
        rView = (RecyclerView) view.findViewById(R.id.phone_list);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                rView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        rView.addItemDecoration(mDividerItemDecoration);


        emptyText = (TextView) view.findViewById(R.id.empty_text);
        if (rView != null) {
            Context context = rView.getContext();
            rView.setLayoutManager(new LinearLayoutManager(context));
            List<ExcludedPhoneNumbers> tempExcludedPhoneNumbers = new ArrayList<>();
            adapter = new ExcludedPhoneNumberRecyclerViewAdapter(getActivity(), tempExcludedPhoneNumbers, mListener);
            rView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        implementRecyclerViewClickListeners();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.phone_list));

        return view;
    }

    private void showEditPhoneDialog(Bundle bundle) {
        EditPhoneFragment editPhoneFragment = new EditPhoneFragment();
        editPhoneFragment.setArguments(bundle);
        editPhoneFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadData();
            }
        });
        editPhoneFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        rView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rView, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null){
                    onListItemSelect(position);
                } else{

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("excludedPhoneNumber", adapter.getItem(position));

                    showEditPhoneDialog(bundle);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    //List item select method
    private void onListItemSelect(int position) {
        adapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;//Check if any items are already selected or not


        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new Toolbar_ActionMode_Callback(this,adapter, excludedPhoneNumbers, false));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(adapter
                    .getSelectedCount()) + " selected");


    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public void showEmptyText() {
        emptyText.setVisibility(View.VISIBLE);
        rView.setVisibility(View.GONE);
    }

    public void hideEmptyText() {
        emptyText.setVisibility(View.GONE);
        rView.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        createPersistanceTask().getAll();

    }

    //Delete selected rows
    public void deleteRows() {

        SparseBooleanArray selected = adapter
                .getSelectedIds();//Get selected ids

        List<ExcludedPhoneNumbers> itemsToDelete = new ArrayList<>();

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                itemsToDelete.add(adapter.getItem(selected.keyAt(i)));
            }
        }

        if(itemsToDelete.size() > 0)createPersistanceTask().delete(itemsToDelete);


    }

    @Override
    public void onGetAllTaskEnd(List<ExcludedPhoneNumbers> list) {
        adapter.notifyDataSetChanged();//notify adapter
        excludedPhoneNumbers = list;
        if (excludedPhoneNumbers != null && excludedPhoneNumbers.size() > 0){
            adapter.replaceData(excludedPhoneNumbers);
            hideEmptyText();
        }else{
            showEmptyText();
        }

    }

    @Override
    public void onDeleteTaskEnd(int count) {
        if(isAdded()) {
            Toast.makeText(getActivity(), count + getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();//Show Toast
        }
        mActionMode.finish();//Finish action mode after use
        loadData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ExcludedPhoneNumbers item);
    }
}
