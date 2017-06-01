package com.siputimut.rifansnack;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    private boolean exit = false;
    private String tmpUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // making notification bar transparent
        changeStatusBarColor();

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.loadUrl("http://www.risfansnack.com/");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
        tmpUri = url;
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        // Panggil telp, mail to dll
        shouldOverrideUrlLoading(mWebView, failingUrl);

        // Refresh url before error page
        mWebView.loadUrl(tmpUri);

//        Log.d("*** Url", tmpUri);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }


    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http:") || url.startsWith("https:")) {
            return false;
        }

        // Otherwise allow the OS to handle it
        else if (url.startsWith("tel:")) {
            Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            startActivity(tel);
            return true;
        } else if (url.startsWith("sms:")) {
            Intent sms = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(sms);
            return true;
        } else if (url.startsWith("whatsapp:")) {
            try {
                String[] arrUrl = url.split("=");
                String message = arrUrl[1];
                message = message.replace("%20", " ");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
                return true;
            } catch (Exception e) {
                // returns null if application is not installed
            }
        } else if (url.startsWith("bbmi:")) {
            String bbm = "com.bbm";
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage(bbm);
                startActivity(intent);
            } catch (Exception e) {
                // returns null if application is not installed
            }
        }

        return true;
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onBackPressed() {
        // this method to back to prev web view
//        if (!mWebView.onBackPressed()) {
//            return;
//        }

        // this method to exit
//        exitAppDouble();
        dialogApp();
    }

    private void exitAppDouble() {
        if (exit) {
            super.onBackPressed();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        this.exit = true;
        Toast.makeText(this, "Press twice to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }

    private void dialogApp() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("KELUAR");

        // Setting Dialog Message
        alertDialog.setMessage("Apakah Anda ingin keluar?");

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
