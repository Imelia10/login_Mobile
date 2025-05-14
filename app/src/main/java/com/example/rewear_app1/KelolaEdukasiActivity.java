package com.example.rewear_app1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.ImageButton;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Map;

public class KelolaEdukasiActivity extends AppCompatActivity {

    private static final String TAG = "KelolaEdukasiActivity";
    private Button btnArtikel, btnVideo;
    private ImageButton btnTambah;
    private LinearLayout layoutArtikel;
    private GridLayout layoutVideo;
    private BottomSheetDialog bottomSheetDialog;
    private boolean isArtikelActive = true;
    private String selectedVideoUri = "";
    private DatabaseHelperEdukasi dbHelper;
    private ActivityResultLauncher<Intent> videoPickerLauncher;
    private ImageButton btnUploadVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_edukasi);

        // Initialize views
        btnArtikel = findViewById(R.id.btnArtikel);
        btnVideo = findViewById(R.id.btnVideo);
        btnTambah = findViewById(R.id.btnTambah);
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

        btnVideo.setOnClickListener(v -> {
            layoutArtikel.setVisibility(View.GONE);
            layoutVideo.setVisibility(View.VISIBLE);
            isArtikelActive = false;
        });

        ImageView backIcon = findViewById(R.id.back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KelolaEdukasiActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnTambah.setOnClickListener(v -> showBottomSheetForm());

        // Initialize video picker launcher
        videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                selectedVideoUri = uri.toString();
                                Log.d(TAG, "Video URI: " + selectedVideoUri);
                                if (btnUploadVideo != null) {
                                    btnUploadVideo.setImageResource(R.drawable.book);
                                }
                            } catch (SecurityException e) {
                                Log.e(TAG, "Error taking permission", e);
                                Toast.makeText(this, "Gagal mendapatkan izin video", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

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

    private void showBottomSheetForm() {
        View view = LayoutInflater.from(this).inflate(R.layout.form_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextView tvFormTitle = view.findViewById(R.id.tvFormTitle);
        EditText etJudul = view.findViewById(R.id.etJudul);
        EditText etDeskripsi = view.findViewById(R.id.etDeskripsi);
        Button btnSimpan = view.findViewById(R.id.btnSimpan);
        btnUploadVideo = view.findViewById(R.id.btnPilihVideo);

        tvFormTitle.setText(isArtikelActive ? "Tambah Artikel" : "Tambah Video");
        btnUploadVideo.setVisibility(isArtikelActive ? View.GONE : View.VISIBLE);
        selectedVideoUri = "";

        btnUploadVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            videoPickerLauncher.launch(intent);
        });

        btnSimpan.setOnClickListener(v -> {
            String judul = etJudul.getText().toString().trim();
            String deskripsi = etDeskripsi.getText().toString().trim();

            if (judul.isEmpty() || deskripsi.isEmpty() || (!isArtikelActive && selectedVideoUri.isEmpty())) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.insertEdukasi(judul, deskripsi, isArtikelActive ? "artikel" : "video", selectedVideoUri);
            int newId = dbHelper.getLastInsertedId();

            if (isArtikelActive) {
                tambahArtikelKeLayout(judul, deskripsi, newId);
            } else {
                tambahVideoKeLayout(judul, newId, selectedVideoUri, deskripsi);
            }

            Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
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

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.END);
        buttonLayout.setPadding(0, 12, 0, 0);

        ImageButton btnEdit = new ImageButton(this);
        btnEdit.setImageResource(R.drawable.pensiledit);
        btnEdit.setBackgroundResource(android.R.color.transparent);
        btnEdit.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
        btnEdit.setOnClickListener(v -> showEditForm(id, tvJudul, tvDeskripsi, "artikel", artikelItem, null));

        ImageButton btnHapus = new ImageButton(this);
        btnHapus.setImageResource(R.drawable.hapus);
        btnHapus.setBackgroundResource(android.R.color.transparent);
        btnHapus.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
        btnHapus.setOnClickListener(v -> showDeleteConfirmationDialog(id, artikelItem));

        buttonLayout.addView(btnEdit);
        buttonLayout.addView(btnHapus);

        artikelItem.addView(tvJudul);
        artikelItem.addView(tvDeskripsi);
        artikelItem.addView(buttonLayout);

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
                runOnUiThread(() ->
                        Toast.makeText(this, "Gagal memutar video", Toast.LENGTH_SHORT).show());
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "Error loading video", e);
            tvDeskripsi.setText("[Video tidak dapat dimuat]");
        }

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.END);
        buttonLayout.setPadding(0, 12, 0, 0);

        ImageButton btnEdit = new ImageButton(this);
        btnEdit.setImageResource(R.drawable.pensiledit);
        btnEdit.setBackgroundColor(Color.TRANSPARENT);
        btnEdit.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
        btnEdit.setOnClickListener(v -> showEditForm(id, tvTitle, tvDeskripsi, "video", videoItem, videoUri));

        ImageButton btnHapus = new ImageButton(this);
        btnHapus.setImageResource(R.drawable.hapus);
        btnHapus.setBackgroundColor(Color.TRANSPARENT);
        btnHapus.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
        btnHapus.setOnClickListener(v -> showDeleteConfirmationDialog(id, videoItem));

        buttonLayout.addView(btnEdit);
        buttonLayout.addView(btnHapus);

        videoItem.addView(tvTitle);
        videoItem.addView(tvDeskripsi);
        videoItem.addView(videoView);
        videoItem.addView(buttonLayout);

        layoutVideo.addView(videoItem);
    }

    private void showDeleteConfirmationDialog(int id, View itemView) {
        new AlertDialog.Builder(this)
                .setMessage("Apakah Anda yakin ingin menghapus item ini?")
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> {
                    dbHelper.deleteEdukasi(id);
                    ViewGroup parent = (ViewGroup) itemView.getParent();
                    if (parent != null) {
                        parent.removeView(itemView);
                    }
                    Toast.makeText(this, "Item berhasil dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void showEditForm(int id, TextView tvJudulView, TextView tvDeskripsiView, String tipe, View itemView, String videoUri) {
        View view = LayoutInflater.from(this).inflate(R.layout.form_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextView tvFormTitle = view.findViewById(R.id.tvFormTitle);
        EditText etJudul = view.findViewById(R.id.etJudul);
        EditText etDeskripsi = view.findViewById(R.id.etDeskripsi);
        Button btnSimpan = view.findViewById(R.id.btnSimpan);
        btnUploadVideo = view.findViewById(R.id.btnPilihVideo);

        tvFormTitle.setText("Edit " + (tipe.equals("artikel") ? "Artikel" : "Video"));
        etJudul.setText(tvJudulView.getText().toString());
        if (tvDeskripsiView != null) {
            etDeskripsi.setText(tvDeskripsiView.getText().toString());
        }

        selectedVideoUri = videoUri != null ? videoUri : "";
        btnUploadVideo.setVisibility(tipe.equals("artikel") ? View.GONE : View.VISIBLE);

        if (tipe.equals("video") && !selectedVideoUri.isEmpty()) {
            btnUploadVideo.setImageResource(R.drawable.book);
        }

        btnUploadVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            videoPickerLauncher.launch(intent);
        });

        btnSimpan.setOnClickListener(v -> {
            String newJudul = etJudul.getText().toString().trim();
            String newDeskripsi = etDeskripsi.getText().toString().trim();

            if (newJudul.isEmpty() || (tipe.equals("artikel") && newDeskripsi.isEmpty()) || (tipe.equals("video") && selectedVideoUri.isEmpty())) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.updateEdukasi(id, newJudul, newDeskripsi, tipe, selectedVideoUri);

            tvJudulView.setText(newJudul);
            if (tvDeskripsiView != null) {
                tvDeskripsiView.setText(newDeskripsi);
            }

            if (tipe.equals("video")) {
                VideoView videoView = findVideoViewInLayout((LinearLayout) itemView);
                if (videoView != null) {
                    try {
                        Uri uri = Uri.parse(selectedVideoUri);
                        videoView.setVideoURI(uri);
                        videoView.seekTo(1);
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating video", e);
                    }
                }
            }

            Toast.makeText(this, "Data berhasil di-edit", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
    }
}