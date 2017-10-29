package info.talkalert.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.talkalert.R;
import info.talkalert.fragments.ExcludedPhoneNumberFragment.OnListFragmentInteractionListener;
import info.talkalert.models.ExcludedPhoneNumbers;


public class ExcludedPhoneNumberRecyclerViewAdapter extends RecyclerView.Adapter<ExcludedPhoneNumberRecyclerViewAdapter.ViewHolder> {

    private List<ExcludedPhoneNumbers> mValues;
    private SparseBooleanArray mSelectedItemsIds;
    private final Context context;

    public ExcludedPhoneNumberRecyclerViewAdapter(Context context, List<ExcludedPhoneNumbers> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_excludedphonenumber, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mPhoneView.setText(mValues.get(position).getPhone());

        /** Change background color of the selected items in list view  **/
        if(position > -1){
            if(mSelectedItemsIds.get(position)){
                holder.itemView
                        .setBackgroundColor(ContextCompat.getColor(context,R.color.primary));
                holder.mNameView.setTextColor(ContextCompat.getColor(context,R.color.primary_light));
                holder.mPhoneView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }else{
                holder.itemView
                        .setBackgroundColor(Color.TRANSPARENT);
                holder.mNameView.setTextColor(ContextCompat.getColor(context,R.color.dark_grey));
                holder.mPhoneView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }


    }

    public void replaceData(List<ExcludedPhoneNumbers> mValues){
        this.mValues = mValues;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public ExcludedPhoneNumbers getItem(int position) {
        return mValues.get(position);
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray   getSelectedIds() {
        return mSelectedItemsIds;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mPhoneView;
        public ExcludedPhoneNumbers mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.contact_name_in_lst);
            mPhoneView = (TextView) view.findViewById(R.id.phone_in_list);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPhoneView.getText() + "'";
        }
    }
}
