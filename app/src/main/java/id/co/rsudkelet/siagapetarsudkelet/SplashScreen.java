package id.co.rsudkelet.siagapetarsudkelet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private int waktu_loading=4000;
    //4000=4 detik

    TextView tvCurrentVersion, tvLatesVersion;
    String sCurrentVersion, sLatestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //menghilangkan ActionBar
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //setelah loading maka akan langsung berpindah ke home activity
                Intent home=new Intent(SplashScreen.this, MainActivity.class);
                startActivity(home);
                finish();

            }
        },waktu_loading);

        tvCurrentVersion = findViewById(R.id.tv_current_version);
        tvLatesVersion  = findViewById(R.id.tv_latest_version);

        // Get latest version from play store
        new GetLatestVersion().execute();
    }

    private class GetLatestVersion extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                sLatestVersion = Jsoup
                        .connect("https://play.store.com/store/apps/details?id=" + getPackageName())
                        .timeout(30000)
                        .get()
                        .select("div.hAyfc:nth-child(4)>" +
                                "span:nth-child(2) > div:nth->child(1)" +
                                "> span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sLatestVersion;
        }



        @Override
        protected void onPostExecute(String s) {
            sCurrentVersion = BuildConfig.VERSION_NAME;
            tvCurrentVersion.setText(sCurrentVersion);
            tvLatesVersion.setText(sLatestVersion);

            if (sLatestVersion != null) {
                // Version convert to float
                float cVersion = Float.parseFloat(sCurrentVersion);
                float lVersion = Float.parseFloat(sLatestVersion);

                // Check condition (latest version is greater than current version)
                if (lVersion > cVersion) {
                    // Create update AlertDialog
                    updateAlertDialog();
                }
            }
        }
    }

    private void updateAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage("Update Available");
        builder.setCancelable(false);

        // On update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open play store
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id" + getPackageName())));
                // Dismiss AlertDialog
                dialog.dismiss();
            }
        });

        // On cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel AlertDialog
                dialog.cancel();
            }
        });

        // Show AlertDialog
        builder.show();
    }
}