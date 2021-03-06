package dk.aau.cs.giraf.pictocreator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.analytics.tracking.android.EasyTracker;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PrintDialogActivity extends Activity {
    private static final String PRINT_DIALOG_URL = "https://www.google.com/cloudprint/dialog.html";
    private static final String JS_INTERFACE = "AndroidPrintDialog";
    private static final String CONTENT_TRANSFER_ENCODING = "base64";

    private static final String ZXING_URL = "http://zxing.appspot.com";
    private static final int ZXING_SCAN_REQUEST = 65743;

    /**
     * Post message that is sent by Print Dialog web page when the printing dialog
     * needs to be closed.
     */
    private static final String CLOSE_POST_MESSAGE_NAME = "cp-dialog-on-close";
    private static Uri bitmapToPrint;

    /**
     * Web view element to show the printing dialog in.
     */
    private WebView dialogWebView;

    /**
     * Intent that started the action.
     */
    Intent cloudPrintIntent;

    public static void setBitmapToPrint(Uri uriOfBitmap)
    {
        bitmapToPrint = uriOfBitmap;
    }

    @JavascriptInterface
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.print_dialog);
        dialogWebView = (WebView) findViewById(R.id.webview);
        cloudPrintIntent = this.getIntent();

        WebSettings settings = dialogWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        dialogWebView.addJavascriptInterface(
                new PrintDialogJavaScriptInterface(), JS_INTERFACE);

        dialogWebView.setWebViewClient(new PrintDialogWebClient());

        dialogWebView.loadUrl(PRINT_DIALOG_URL);
    }

    //Google analytics - start logging
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Start logging
    }
    //Google analytics - Stop logging
    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // stop logging
    }

    @JavascriptInterface
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ZXING_SCAN_REQUEST && resultCode == RESULT_OK) {
            dialogWebView.loadUrl(intent.getStringExtra("SCAN_RESULT"));
        }
    }

    final class PrintDialogJavaScriptInterface {
        @JavascriptInterface
        public String getType() {
            return cloudPrintIntent.getType();
        }

        @JavascriptInterface
        public String getTitle() {
            return cloudPrintIntent.getExtras().getString("title");
        }

        @JavascriptInterface
        public String getContent() {
            try {
                ContentResolver contentResolver = getContentResolver();
                InputStream is = contentResolver.openInputStream(cloudPrintIntent.getData());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[4096];
                int n = is.read(buffer);
                while (n >= 0) {
                    baos.write(buffer, 0, n);
                    n = is.read(buffer);
                }
                is.close();
                baos.flush();

                return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @JavascriptInterface
        public String getEncoding() {
            return CONTENT_TRANSFER_ENCODING;
        }

        @JavascriptInterface
        public void onPostMessage(String message) {
            if (message.startsWith(CLOSE_POST_MESSAGE_NAME)) {
                finish();
                // Added to delete local copy of the printed pictogram
                getContentResolver().delete(bitmapToPrint, null, null);
            }
        }
    }


    private final class PrintDialogWebClient extends WebViewClient {
        @JavascriptInterface
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(ZXING_URL)) {
                Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
                intentScan.putExtra("SCAN_MODE", "QR_CODE_MODE");
                try {
                    startActivityForResult(intentScan, ZXING_SCAN_REQUEST);
                } catch (ActivityNotFoundException error) {
                    view.loadUrl(url);
                }
            } else {
                view.loadUrl(url);
            }
            return false;
        }

        @JavascriptInterface
        @Override
        public void onPageFinished(WebView view, String url) {
            if (PRINT_DIALOG_URL.equals(url)) {
                // Submit print document.

                view.loadUrl("javascript:printDialog.setPrintDocument(printDialog.createPrintDocument("
                        + "window." + JS_INTERFACE + ".getType(),window." + JS_INTERFACE + ".getTitle(),"
                        + "window." + JS_INTERFACE + ".getContent(),window." + JS_INTERFACE + ".getEncoding()))");

                // Add post messages listener.
                view.loadUrl("javascript:window.addEventListener('message',"
                        + "function(evt){window." + JS_INTERFACE + ".onPostMessage(evt.data)}, false)");
            }
        }
    }
}