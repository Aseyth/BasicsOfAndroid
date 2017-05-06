import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Aseith on 06/05/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewAdapterViewHolder> {

    List<Data> mDataList;
    int mLayout;

    public RecyclerViewAdapter(List<Data> dataList, int layout) {
        this.mDataList = dataList;
        this.mLayout = layout;
    }

    @Override
    public RecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new RecyclerViewAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.RecyclerViewAdapterViewHolder holder, int position) {
        holder.mItem = mDataList.get(position);
        holder.mName.setText(holder.mItem.getName());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class RecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {

        private Data mItem;
        private TextView mName;

        RecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_template);
        }
    }
}