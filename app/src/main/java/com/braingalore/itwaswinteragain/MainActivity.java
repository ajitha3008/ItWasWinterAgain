package com.braingalore.itwaswinteragain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.braingalore.itwaswinteragain.curl.CurlPage;
import com.braingalore.itwaswinteragain.curl.CurlView;

public class MainActivity extends AppCompatActivity {

    private CurlView mCurlView;

    private SharedPreferences sharedpreferences;

    public static final String MY_PREF = "MyPref";

    public static final String INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.gotopage) {
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(MainActivity.this);
                    localBuilder.setTitle(getResources().getString(R.string.page_dialog_title));
                    final EditText pageNumberEditText = new EditText(MainActivity.this);
                    pageNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    localBuilder.setView(pageNumberEditText);
                    localBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            String data = pageNumberEditText.getText().toString();
                            if (!TextUtils.isEmpty(data)) {
                                int index = Integer.parseInt(data);
                                if (index > 0 && index < 401) {
                                    sharedpreferences.edit().putInt(INDEX, index).commit();
                                    mCurlView = (CurlView) findViewById(R.id.curl);
                                    mCurlView.setPageProvider(new PageProvider());
                                    mCurlView.setCurrentIndex(sharedpreferences.getInt(INDEX, 0));
                                    mCurlView.setBackgroundColor(getResources().getColor(R.color.curl_bg_color));
                                } else {
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_message), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_message), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    localBuilder.show();
                }
                return false;
            }
        });

        sharedpreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);

        mCurlView = (CurlView) findViewById(R.id.curl);
        mCurlView.setPageProvider(new PageProvider());
        mCurlView.setSizeChangedObserver(new SizeChangedObserver());
        mCurlView.setCurrentIndex(sharedpreferences.getInt(INDEX, 0));
        mCurlView.setBackgroundColor(getResources().getColor(R.color.curl_bg_color));
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurlView.onPause();
        sharedpreferences.edit().putInt(INDEX, mCurlView.getCurrentIndex()).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurlView.onResume();
    }

    /**
     * Bitmap provider.
     */
    private class PageProvider implements CurlView.PageProvider {

        // Bitmap resources.
        private int[] mBitmapIds;

        PageProvider() {
            int i;
            mBitmapIds = new int[401];
            for (i = 0; i <= 400; i++) {
                String drawableName = "a" + i;
                mBitmapIds[i] = getResources().getIdentifier(drawableName, "drawable", getPackageName());
            }
            ;
        }

        @Override
        public int getPageCount() {
            return 401;
        }

        private Bitmap loadBitmap(int width, int height, int index) {
            Bitmap b = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            b.eraseColor(0xFFFFFFFF);
            Canvas c = new Canvas(b);
            Drawable d = getResources().getDrawable(mBitmapIds[index]);

            int margin = 7;
            int border = 3;
            Rect r = new Rect(margin, margin, width - margin, height - margin);

            int imageWidth = r.width() - (border * 2);
            int imageHeight = imageWidth * d.getIntrinsicHeight()
                    / d.getIntrinsicWidth();
            if (imageHeight > r.height() - (border * 2)) {
                imageHeight = r.height() - (border * 2);
                imageWidth = imageHeight * d.getIntrinsicWidth()
                        / d.getIntrinsicHeight();
            }

            r.left += ((r.width() - imageWidth) / 2) - border;
            r.right = r.left + imageWidth + border + border;
            r.top += ((r.height() - imageHeight) / 2) - border;
            r.bottom = r.top + imageHeight + border + border;

            Paint p = new Paint();
            p.setColor(0xFFC0C0C0);
            c.drawRect(r, p);
            r.left += border;
            r.right -= border;
            r.top += border;
            r.bottom -= border;

            d.setBounds(r);
            d.draw(c);

            return b;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {
            page.setTexture(loadBitmap(width, height, index), 1);
            page.setColor(Color.rgb(180, 180, 180), 2);
        }
    }

    /**
     * CurlView size changed observer.
     */
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {
            if (w > h) {
                mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
                mCurlView.setMargins(0.0F, 0.0F, 0.0F, 0.0F);
            } else {
                mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
                mCurlView.setMargins(0.0F, 0.0F, 0.0F, 0.0F);
            }
        }
    }
}
