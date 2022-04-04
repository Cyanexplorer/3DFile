package com.example.g3863.a3dfile.View.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import com.example.g3863.a3dfile.Model.MtlData;
import com.example.g3863.a3dfile.Model.ModelSettings;
import com.example.g3863.a3dfile.R;
import com.example.g3863.a3dfile.View.Adapter.TextureRVAdapter;
import com.example.g3863.a3dfile.View.Activity.SquareImageView;
import com.example.g3863.a3dfile.util.OpenglColor;

import java.util.ArrayList;

import static com.example.g3863.a3dfile.util.FileInputPath.getPathFromUri;


public class TexManagerFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private RecyclerView recyclerView;
    private Interlaction interlaction;
    private ArrayList<ModelSettings> selects;

    public TexManagerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_tex_manager, null);

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollBar);
        scrollView.smoothScrollTo(0, 0);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.textureRV);
        recyclerView.setLayoutManager(manager);
        final TextureRVAdapter adapter = new TextureRVAdapter(getContext(), selects);
        final SquareImageView imageView = (SquareImageView) view.findViewById(R.id.sqrIV);

        adapter.setInterlaction(new TextureRVAdapter.Interlaction() {
            @Override
            public void sendBitmap(Bitmap[] bitmap) {
                imageView.setImageBitmap(bitmap[1]);
            }
        });

        FloatingActionButton changePtBtn = view.findViewById(R.id.change);
        changePtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MtlData mtlData = null;
                if (recyclerView != null) {
                    mtlData = ((TextureRVAdapter) recyclerView.getAdapter()).getSelectedItem();
                }

                if (interlaction != null && mtlData != null) {
                    interlaction.getPicture();
                }
            }
        });

        FloatingActionButton deleteBtn = view.findViewById(R.id.delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MtlData mtlData = null;
                if (recyclerView != null) {
                    mtlData = ((TextureRVAdapter) recyclerView.getAdapter()).getSelectedItem();
                }

                if (mtlData != null) {
                    mtlData.resetTxr();
                    mtlData.resetLight();
                    adapter.refresh();
                }
            }
        });

        FloatingActionButton cb01 = view.findViewById(R.id.cb01);
        FloatingActionButton cb02 = view.findViewById(R.id.cb02);
        FloatingActionButton cb03 = view.findViewById(R.id.cb03);
        FloatingActionButton cb04 = view.findViewById(R.id.cb04);
        FloatingActionButton cb05 = view.findViewById(R.id.cb05);
        FloatingActionButton cb06 = view.findViewById(R.id.cb06);
        FloatingActionButton cb07 = view.findViewById(R.id.cb07);

        cb01.setOnClickListener(ColorSelect());
        cb02.setOnClickListener(ColorSelect());
        cb03.setOnClickListener(ColorSelect());
        cb04.setOnClickListener(ColorSelect());
        cb05.setOnClickListener(ColorSelect());
        cb06.setOnClickListener(ColorSelect());
        cb07.setOnClickListener(ColorSelect());

        recyclerView.setAdapter(adapter);

        CardView txr_crt_btn = view.findViewById(R.id.txr_crt_btn);
        txr_crt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.createMtlData();
            }
        });

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Texture Manager")
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MtlData mtlData = adapter.getSelectedItem();
                        if (mtlData != null && interlaction != null) {
                            interlaction.setTexture(mtlData);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
/*
    private void reloadMainScreen(View view) {
        try {
            Bitmap bm = ((TextureRVAdapter) recyclerView.getAdapter()).getSelectedItem().generateKaB(75);
            ((ImageView) view).setImageBitmap(bm);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
*/
    private View.OnClickListener ColorSelect() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MtlData mtlData = null;
                if (recyclerView != null) {
                    mtlData = ((TextureRVAdapter) recyclerView.getAdapter()).getSelectedItem();
                }

                if (mtlData == null) {
                    return;
                }

                switch (v.getId()) {
                    case R.id.cb01:
                        mtlData.setColor(OpenglColor.RED, OpenglColor.RED);
                        break;
                    case R.id.cb02:
                        mtlData.setColor(OpenglColor.YELLOW, OpenglColor.YELLOW);
                        break;
                    case R.id.cb03:
                        mtlData.setColor(OpenglColor.BLUE, OpenglColor.BLUE);
                        break;
                    case R.id.cb04:
                        mtlData.setColor(OpenglColor.GREEN, OpenglColor.GREEN);
                        break;
                    case R.id.cb05:
                        mtlData.setColor(OpenglColor.PURPLE, OpenglColor.PURPLE);
                        break;
                    case R.id.cb06:
                        mtlData.setColor(OpenglColor.WHITE, OpenglColor.WHITE);
                        break;
                    case R.id.cb07:
                        mtlData.setColor(OpenglColor.BLACK, OpenglColor.BLACK);
                        break;
                }

                if (recyclerView.getAdapter() instanceof TextureRVAdapter) {
                    ((TextureRVAdapter) recyclerView.getAdapter()).refresh();
                }

            }
        };
    }

    public static TexManagerFragment newInstance(@NonNull ArrayList<ModelSettings> selects) {
        TexManagerFragment fragment = new TexManagerFragment();
        fragment.selects = selects;
        return fragment;
    }

    public void changePicture(Uri uri) {
        String imagePath = getPathFromUri(getContext(), uri);

        if (recyclerView != null && recyclerView.getAdapter() != null && imagePath != null && !imagePath.isEmpty()) {
            MtlData data = ((TextureRVAdapter) recyclerView.getAdapter()).getSelectedItem();
            data.setmKa(imagePath);
            ((TextureRVAdapter) recyclerView.getAdapter()).refresh();
        }
        Log.i("imagePath", imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Interlaction) {
            interlaction = (Interlaction) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interlaction = null;
    }

    public interface Interlaction {
        void getPicture();

        void setTexture(MtlData mtlData);
    }

}
