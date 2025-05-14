package com.example.rewear_app1;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ImageView;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Map;

public class EdukasiUserActivity extends AppCompatActivity {

    private static final String TAG = "EdukasiUserActivity";
    private Button btnArtikel, btnVideo;
    private LinearLayout layoutArtikel;
    private GridLayout layoutVideo;
    private boolean isArtikelActive = true;
    private DatabaseHelperEdukasi dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edukasi_user);

        // Initialize views
        btnArtikel = findViewById(R.id.btnArtikel);
        btnVideo = findViewById(R.id.btnVideo);
        layoutArtikel = findViewById(R.id.layoutArtikel);
        layoutVideo = findViewById(R.id.layoutVideo);

        // Set initial visibility - show only articles at start
        layoutArtikel.setVisibility(View.VISIBLE);
        layoutVideo.setVisibility(View.GONE);
        isArtikelActive = true;

        dbHelper = new DatabaseHelperEdukasi(this);

        // Button click listeners
        btnArtikel.setOnClickListener(v -> {
            layoutArtikel.setVisibility(View.VISIBLE);
            layoutVideo.setVisibility(View.GONE);
            isArtikelActive = true;
        });

        ImageView backIcon = findViewById(R.id.back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EdukasiUserActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnVideo.setOnClickListener(v -> {
            layoutArtikel.setVisibility(View.GONE);
            layoutVideo.setVisibility(View.VISIBLE);
            isArtikelActive = false;
        });

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAllVideos();
    }

    private void stopAllVideos() {
        for (int i = 0; i < layoutVideo.getChildCount(); i++) {
            View child = layoutVideo.getChildAt(i);
            if (child instanceof LinearLayout) {
                VideoView videoView = findVideoViewInLayout((LinearLayout) child);
                if (videoView != null && videoView.isPlaying()) {
                    videoView.stopPlayback();
                }
            }
        }
    }

    private VideoView findVideoViewInLayout(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof VideoView) {
                return (VideoView) child;
            }
        }
        return null;
    }

    private void loadData() {
        layoutArtikel.removeAllViews();
        layoutVideo.removeAllViews();

        List<Map<String, String>> data = dbHelper.getAllEdukasi();
        for (Map<String, String> item : data) {
            String judul = item.get(DatabaseHelperEdukasi.COL_JUDUL);
            String deskripsi = item.get(DatabaseHelperEdukasi.COL_DESKRIPSI);
            String tipe = item.get(DatabaseHelperEdukasi.COL_TIPE);
            String videoUri = item.get(DatabaseHelperEdukasi.COL_VIDEO_URI);
            int id = Integer.parseInt(item.get(DatabaseHelperEdukasi.COL_ID));

            if (tipe.equals("artikel")) {
                tambahArtikelKeLayout(judul, deskripsi, id);
            } else {
                tambahVideoKeLayout(judul, id, videoUri, deskripsi);
            }
        }
    }

    private void tambahArtikelKeLayout(String judul, String deskripsi, int id) {
        LinearLayout artikelItem = new LinearLayout(this);
        artikelItem.setOrientation(LinearLayout.VERTICAL);
        artikelItem.setBackgroundColor(0xFFEEEEEE);
        artikelItem.setPadding(24, 24, 24, 24);
        artikelItem.setTag(id);

        TextView tvJudul = new TextView(this);
        tvJudul.setText(judul);
        tvJudul.setTextSize(16);
        tvJudul.setTypeface(null, Typeface.BOLD);

        TextView tvDeskripsi = new TextView(this);
        tvDeskripsi.setText(deskripsi);
        tvDeskripsi.setTextSize(14);

        artikelItem.addView(tvJudul);
        artikelItem.addView(tvDeskripsi);

        layoutArtikel.addView(artikelItem);
    }


    private void tambahVideoKeLayout(String judul, int id, String videoUri, String deskripsi) {
        LinearLayout videoItem = new LinearLayout(this);
        videoItem.setOrientation(LinearLayout.VERTICAL);
        videoItem.setPadding(32, 32, 32, 32);
        videoItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
        videoItem.setElevation(8f);
        videoItem.setTag(id);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(24, 24, 24, 24);
        videoItem.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(judul);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.BLACK);

        TextView tvDeskripsi = new TextView(this);
        tvDeskripsi.setText(deskripsi);
        tvDeskripsi.setTextSize(14);
        tvDeskripsi.setTextColor(Color.DKGRAY);
        tvDeskripsi.setPadding(0, 8, 0, 0);

        VideoView videoView = new VideoView(this);
        try {
            Uri uri = Uri.parse(videoUri);

            try {
                getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                Log.e(TAG, "Permission already granted or uri invalid");
            }

            videoView.setVideoURI(uri);

            LinearLayout.LayoutParams videoParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (getResources().getDisplayMetrics().density * 200)
            );
            videoView.setLayoutParams(videoParams);

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.setOnPreparedListener(mp -> {
                videoView.seekTo(1);
                Log.d(TAG, "Video prepared: " + videoUri);
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Error playing video - what:" + what + " extra:" + extra);
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "Error loading video", e);
            tvDeskripsi.setText("[Video tidak dapat dimuat]");
        }

        videoItem.addView(tvTitle);
        videoItem.addView(tvDeskripsi);
        videoItem.addView(videoView);

        layoutVideo.addView(videoItem);
    }
}