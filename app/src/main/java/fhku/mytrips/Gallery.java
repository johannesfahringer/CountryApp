package fhku.mytrips;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Gallery extends AppCompatActivity {

    ImageView image;
    LinearLayout linearLayout;
    String currentCountryName;
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private ImageAdapter imageAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent getCurrenctCountry = getIntent();
        currentCountryName = getCurrenctCountry.getStringExtra("gallery");
        Log.i("CCN", currentCountryName);
        getFromInternalStorage();
        GridView imagegrid = findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);


    }

    public void setImage() {
        File imgFile = new File("/storage/emulated/0/DCIM/Camera/IMG_20151102_193132.jpg");
        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            image = new ImageView(this);
            linearLayout.addView(image);
            image.setImageBitmap(myBitmap);


        }
        ;
    }

    public void getFromInternalStorage() {
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                String searchIn = String.valueOf(listFile[i]);
                String findString = currentCountryName;
                int searchInLength = searchIn.length();
                int findStringLength = findString.length();
                boolean foundIt = false;
                for (int index = 0; index<= (searchInLength-findStringLength);index++){
                    if (searchIn.regionMatches(index, findString, 0, findStringLength)){
                        foundIt=true;
                        f.add(listFile[i].getAbsolutePath());
                        Log.i("listfile", listFile[i].getAbsolutePath());
                        break;
                    }
                }
                if (!foundIt)
                    Log.i("NOTFOUND","No match found.");
            }
        }
    }


    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return f.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position));
            holder.imageview.setImageBitmap(myBitmap);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;


    }
}
