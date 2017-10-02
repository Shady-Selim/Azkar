package com.shady_selim.azkar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shady_selim.azkar.firebaseDB.AzkarListClass;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ZikrDetailFragment extends Fragment {
    @BindView(R.id.zikr_RecyclerView) RecyclerView mRecyclerView;
    @BindArray(R.array.degree) String degree[];
    @BindString(R.string.number) String num;
    @BindString(R.string.repeated_text) String repeatedText;

    private String mTitle;
    private Activity activity;
    private List<AzkarListClass> azkarClass;

    public ZikrDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();

        if (getArguments().containsKey("name")) {
            mTitle = getArguments().getString("name");
            azkarClass = Parcels.unwrap(getArguments().getParcelable("azkar"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.zikr_detail, container, false);
        ButterKnife.bind(this,rootView);
        if (activity.findViewById(R.id.zikr_detail_container) != null) {
        }
        activity.setTitle(mTitle);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(new ZikrDetailFragment.SimpleItemRecyclerViewAdapter(azkarClass));
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putString("name", mTitle);
        super.onSaveInstanceState(outState);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final List<AzkarListClass> mValues;
        public SimpleItemRecyclerViewAdapter(List<AzkarListClass> azkarClass) {
            mValues = azkarClass;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_detail, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {
            final int[] counter = {1};
            holder.mItem = mValues.get(position);
            holder.count.setText(repeatedText + mValues.get(position).count.toString() + " / " + counter[0]);
            holder.zikrNo.setText(num + String.valueOf(position+1));
            holder.content.setText(mValues.get(position).content);
            if (mValues.get(position).degree == null)
                holder.degree.setVisibility(View.GONE);
            else
                holder.degree.setText(degree[mValues.get(position).degree]);
            if (mValues.get(position).reference == null)
                holder.reference.setVisibility(View.GONE);
            else
                holder.reference.setText(mValues.get(position).reference);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (counter[0] <  mValues.get(position).count){
                        ++counter[0];
                        holder.count.setText(repeatedText + mValues.get(position).count.toString() + " / " + counter[0]);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return azkarClass.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public final View mView;
            public AzkarListClass mItem;
            @BindView(R.id.count) TextView count;
            @BindView(R.id.zikrNo) TextView zikrNo;
            @BindView(R.id.content) TextView content;
            @BindView(R.id.degree) TextView degree;
            @BindView(R.id.reference) TextView reference;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
