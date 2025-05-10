package com.example.rewear_app1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.VideoView;
import android.widget.ImageButton;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Map;

public class EdukasiUserActivity extends AppCompatActivity {

    private Button btnArtikel, btnVideo;
    private LinearLayout layoutArtikel;
    private GridLayout layoutVideo;
    private boolean isArtikelActive = true;
    private String selectedVideoUri = "";
    private DatabaseHelperEdukasi dbHelper;
    private ActivityResultLauncher<Intent> videoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_edukasi);

        btnArtikel = findViewById(R.id.btnArtikel);
        btnVideo = findViewById(R.id.btnVideo);
        layoutArtikel = findViewById(R.id.layoutArtikel);
        layoutVideo = findViewById(R.id.layoutVideo);

        dbHelper = new DatabaseHelperEdukasi(this);

        btnArtikel.setOnClickListener(v -> {
            layoutArtikel.setVisibility(View.VISIBLE);
            layoutVideo.setVisibility(View.GONE);
            isArtikelActive = true;
        });

        btnVideo.setOnClickListener(v -> {
            layoutArtikel.setVisibility(View.GONE);
            layoutVideo.setVisibility(View.VISIBLE);
            isArtikelActive = false;
        });

        loadData();
    }

    private void loadData() {
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
        tvJudul.setTypeface(null, android.graphics.Typeface.BOLD);

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
        Uri uri = Uri.parse(videoUri);
        videoView.setVideoURI(uri);
        videoView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (getResources().getDisplayMetrics().density * 200)
        ));
        videoView.seekTo(1); // thumbnail awal

        ImageButton btnPlayPause = new ImageButton(this);
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        btnPlayPause.setBackgroundColor(Color.TRANSPARENT);
        btnPlayPause.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

        btnPlayPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                videoView.start();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        // Tambahkan elemen ke tampilan
        videoItem.addView(tvTitle);
        videoItem.addView(tvDeskripsi);
        videoItem.addView(videoView);
        videoItem.addView(btnPlayPause);

        layoutVideo.addView(videoItem);
    }
}
