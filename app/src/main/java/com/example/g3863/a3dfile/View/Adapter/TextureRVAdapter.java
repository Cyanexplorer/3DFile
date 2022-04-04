package com.example.g3863.a3dfile.View.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.g3863.a3dfile.Model.MtlData;
import com.example.g3863.a3dfile.Controller.Manager.MtlManager;
import com.example.g3863.a3dfile.Model.ModelSettings;
import com.example.g3863.a3dfile.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TextureRVAdapter extends RecyclerView.Adapter<TextureRVAdapter.ViewHolder> {

    private final ArrayList<MtlData> mValues;
    private Context context;
    private BitmapFactory.Options options;
    private Interlaction interlaction;
    private int index;

    public TextureRVAdapter(Context context,ArrayList<ModelSettings> selects) {
        mValues = new ArrayList<>();
        mValues.addAll(MtlManager.mtlOption);

        if(selects.size()>0){
            index = mValues.indexOf(selects.get(0).getModel().mtlData);
        }

        this.context = context;
        options = new BitmapFactory.Options();
    }

    public void refresh(){
        mValues.clear();
        mValues.addAll(MtlManager.mtlOption);
        notifyDataSetChanged();
    }

    public void createMtlData(){
        MtlManager.mtlOption.add(new MtlData());
        refresh();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.texture_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Bitmap[] bitmap = new Bitmap[3];
        holder.mItem = mValues.get(position);
        holder.name.setText(holder.mItem.name);

        try {

            bitmap[0] = holder.mItem.generateKaB(context,75, MtlData.Direction.None);
            bitmap[1] = holder.mItem.generateKdB(context,75, MtlData.Direction.None);
            bitmap[2] = holder.mItem.generateKsB(context,75, MtlData.Direction.None);

            holder.bitmap = new Bitmap[3];
            holder.bitmap = bitmap;
            holder.mBitmap.setImageBitmap(bitmap[1]);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sltMtl!=null){
                        sltMtl.mView.setCardBackgroundColor(Color.WHITE);
                    }
                    sltMtl = holder;
                    sltMtl.mView.setCardBackgroundColor(Color.BLUE);
                    if(interlaction!=null){
                        interlaction.sendBitmap(holder.bitmap);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(position == index){
            holder.add.setText("current");
            holder.mView.performClick();
        }
        else {
            holder.add.setText("");
        }
    }

    private ViewHolder sltMtl = null;

    public void setInterlaction(Interlaction interlaction){
        this.interlaction = interlaction;
    }

    public MtlData getSelectedItem(){
        if(sltMtl==null){
            return null;
        }
        return sltMtl.mItem;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView mView;
        public final ImageView mBitmap;
        public final TextView name;
        public final TextView add;
        public Bitmap[] bitmap;
        public MtlData mItem;

        public ViewHolder(View view) {
            super(view);

            mView = (CardView)view.findViewById(R.id.imageCV);
            mBitmap = (ImageView) view.findViewById(R.id.image);
            name = (TextView)view.findViewById(R.id.name);
            add = (TextView)view.findViewById(R.id.add);
            mView.setCardBackgroundColor(Color.WHITE);
        }

    }

    public interface Interlaction{
        void sendBitmap(Bitmap[] bitmap);
    }
}
