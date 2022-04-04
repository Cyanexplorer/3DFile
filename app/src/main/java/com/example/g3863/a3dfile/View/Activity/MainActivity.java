package com.example.g3863.a3dfile.View.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.g3863.a3dfile.Model.MtlData;
import com.example.g3863.a3dfile.Controller.Manager.ObjManager;
import com.example.g3863.a3dfile.Controller.GlEs2Renderer;
import com.example.g3863.a3dfile.R;
import com.example.g3863.a3dfile.View.Object.PressImageButton;
import com.example.g3863.a3dfile.View.Fragment.TexManagerFragment;
import com.example.g3863.a3dfile.View.Object.Glsurface_Ext;

import java.io.File;
import java.util.Locale;

import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.MOVE;
import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.ROTATE;
import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.SELECT;
import static com.example.g3863.a3dfile.Controller.Manager.ObjManager.GRID;
import static com.example.g3863.a3dfile.Controller.Manager.ObjManager.SHAPE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TexManagerFragment.Interlaction {
    public static final int Pt = 3;
    private final String TAG = "MainActivity";
    private final int Obj = 1;
    private final int Mtl = 2;
    private Glsurface_Ext glSurfaceView;
    private boolean rendererSet = false;
    private com.example.g3863.a3dfile.Controller.GlEs2Renderer GlEs2Renderer;
    private PressImageButton selectOption = null;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.rotateButton:
                    glSurfaceView.state = ROTATE;
                    ObjManager.setTools(0);
                    switchState(v);
                    break;

                case R.id.moveButton:
                    glSurfaceView.state = MOVE;
                    ObjManager.setTools(1);
                    switchState(v);
                    break;

                case R.id.selectButton:
                    glSurfaceView.state = SELECT;
                    ObjManager.setTools(-1);
                    switchState(v);
                    break;

                case R.id.deleteButton:
                    ObjManager.ObjmngMsg objmngMsg = new ObjManager.ObjmngMsg();
                    objmngMsg.setDelete();
                    ObjManager.setObjmngMsg(objmngMsg);
                    ObjManager.setTools(-1);
                    switchState(v);
                    break;

                case R.id.texButton:
                    ObjManager.TextureManager(getSupportFragmentManager());
                    break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WorkSpace");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        glSurfaceView = findViewById(R.id.glSurface);
        // Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        GlEs2Renderer = new GlEs2Renderer(this);
        if (supportsEs2) {
            // ...
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setEGLConfigChooser(true);
            glSurfaceView.setPreserveEGLContextOnPause(true);
            // Assign our renderer.
            glSurfaceView.setRenderer(GlEs2Renderer);
            rendererSet = true;

        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since
             * we're not doing anything, the app will crash if the device
             * doesn't support OpenGL ES 2.0. If we publish on the market, we
             * should also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        PressImageButton rotate = findViewById(R.id.rotateButton);
        rotate.setImageId(R.drawable.rotate_option_t, R.drawable.rotate_option);
        rotate.setOnClickListener(clickListener);

        PressImageButton move = findViewById(R.id.moveButton);
        move.setImageId(R.drawable.trans_option_t, R.drawable.trans_option);
        move.setOnClickListener(clickListener);

        PressImageButton delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(clickListener);

        PressImageButton texture = findViewById(R.id.texButton);
        texture.setOnClickListener(clickListener);

        PressImageButton select = findViewById(R.id.selectButton);
        select.setImageId(R.drawable.select_option_t, R.drawable.select_option);
        select.setOnClickListener(clickListener);
        select.performClick();


        //check permission
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        requestPermission(permissions);

        //create default path
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + File.separator + "temp");
        Log.i("create doc:",dir.getAbsolutePath());
        if (!dir.exists() && !dir.mkdir()) {
            this.finish();
        }
    }

    private void switchState(View v) {
        if (selectOption != null) {
            selectOption.setSelected(false);
        }
        if (v instanceof PressImageButton) {
            selectOption = (PressImageButton) v;
            selectOption.setSelected(true);
        }
        Log.i("test", v.toString());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjManager.destroyAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_shape).setChecked(false);
        menu.findItem(R.id.action_grid).setChecked(false);
        onOptionsItemSelected(menu.findItem(R.id.action_shape));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_shape) {
            ObjManager.setDrawState(SHAPE, switchBool(item));
            return true;
        } else if (id == R.id.action_grid) {
            ObjManager.setDrawState(GRID, switchBool(item));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean switchBool(MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
            return false;
        } else {
            item.setChecked(true);
            return true;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inport) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            startActivityForResult(Intent.createChooser(intent, "tools"), Obj);
            // Handle the camera action
        } else if (id == R.id.nav_exit) {
            this.finish();
        } else if (id == R.id.nav_save_new) {
            ObjManager.saveToObjFile(this, "test");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkPermission(String[] permissions) {
        return checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean requestPermission(String[] permissions) {
        if (!checkPermission(permissions)) {
            requestPermissions(permissions, 0);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != RESULT_OK) {
                    requestPermission(permissions);
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Obj && resultCode == RESULT_OK) {
            Uri obj = data.getData();

            String[] parts = obj.getLastPathSegment().split("\\.");
            Log.i("filename", obj.getPath());
            if (parts.length > 0 && parts[parts.length - 1].toLowerCase().equals("obj")) {
                ObjManager.openFile(this, obj);
            } else {
                Toast.makeText(this, "Not support this file format.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == Pt && resultCode == RESULT_OK) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("Tex");
            if (fragment != null) {
                ((TexManagerFragment) fragment).changePicture(data.getData());
            }
        }
        Log.i(TAG, String.format(Locale.getDefault(), "%d/%d", requestCode, resultCode));
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void getPicture() {
        String[] mineType = new String[]{"image/jpg", "image/jpeg", "image/png"};
        Intent pickIntent = new Intent();
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent takeicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooserIntent = Intent.createChooser(pickIntent, "Tools");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takeicture});
        startActivityForResult(chooserIntent, Pt);
    }

    @Override
    public void setTexture(MtlData mtlData) {
        ObjManager.setTexture(mtlData);
    }

}
