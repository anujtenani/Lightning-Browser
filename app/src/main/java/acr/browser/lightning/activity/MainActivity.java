package acr.browser.lightning.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.io.File;

import acr.browser.lightning.R;
import acr.browser.lightning.react.Action;
import acr.browser.lightning.react.Observable;
import acr.browser.lightning.react.Subscriber;

@SuppressWarnings("deprecation")
public class MainActivity extends BrowserActivity {

    @Override
    public Observable<Void> updateCookiePreference() {
        return Observable.create(new Action<Void>() {
            @Override
            public void onSubscribe(@NonNull Subscriber<Void> subscriber) {
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.createInstance(MainActivity.this);
                }
                cookieManager.setAcceptCookie(mPreferences.getCookiesEnabled());
                subscriber.onComplete();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (isPanicTrigger(intent)) {
            panicClean();
        } else {
            try {
                String str = intent.getExtras().getString("myPictures");
                File vault = new File(str).getParentFile();
                if (str != null) {
                    mPreferences.setDownloadDirectory(vault.getAbsolutePath());
                }
            }catch (Exception e){
                e.printStackTrace();;
            }
            handleNewIntent(intent);
            super.onNewIntent(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveOpenTabs();
    }

    @Override
    public void updateHistory(@Nullable String title, @NonNull String url) {
        addItemToHistory(title, url);
    }

    @Override
    public boolean isIncognito() {
        return false;
    }

    @Override
    public void closeActivity() {
        closeDrawers(new Runnable() {
            @Override
            public void run() {
                performExitCleanUp();
                moveTaskToBack(true);
            }
        });
    }


}
