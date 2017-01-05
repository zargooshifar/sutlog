package ir.zarg.serendipity.sutlog.fragments.navigationDrawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import ir.zarg.serendipity.sutlog.R;

/**
 * Created by serendipity on 7/8/16.
 */


public class navRecyclerAdapter extends RecyclerView.Adapter<navRecyclerAdapter.nawViewHolder> {

    private LayoutInflater inflater;
    List<navDataFormat> data = Collections.emptyList();

    public navRecyclerAdapter(Context context, List<navDataFormat> data) {

        inflater = LayoutInflater.from(context);
        this.data = data;

    }


    @Override
    public nawViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.naw_row, parent, false);

        nawViewHolder holder = new nawViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(nawViewHolder holder, int position) {
        navDataFormat current = data.get(position);
        holder.tv_naw.setText(current.naw_text);
        holder.iv_naw.setImageResource(current.naw_img_id);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class nawViewHolder extends RecyclerView.ViewHolder {

        TextView tv_naw;
        ImageView iv_naw;

        public nawViewHolder(View itemView) {
            super(itemView);

            tv_naw = (TextView) itemView.findViewById(R.id.tv_naw);
            iv_naw = (ImageView) itemView.findViewById(R.id.iv_nav);


        }
    }


}


