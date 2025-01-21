package com.zebra.zebraidentityguardianwrappersample;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zebra.zebraidentitiyguardianwrapper.AuthenticationStatusObserver;
import com.zebra.zebraidentitiyguardianwrapper.IGCRHelper;
import com.zebra.zebraidentitiyguardianwrapper.IIGAuthenticationResultCallback;
import com.zebra.zebraidentitiyguardianwrapper.IIGSessionResultCallback;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    String   mStatus = "";
    TextView tvStatus;
    ScrollView svStatus;


    boolean mOptmizeRefresh = true;

    AuthenticationStatusObserver mAuthenticationStatusObserver;

    /*
    Handler and runnable to scroll down textview
 */
    private Handler mScrollDownHandler = null;
    private Runnable mScrollDownRunnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvStatus = findViewById(R.id.tv_status);
        svStatus = findViewById(R.id.sv_status);

        findViewById(R.id.btCurrentSession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentSession();
            }
        });

        findViewById(R.id.btPreviousSession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreviousSession();
            }
        });

        findViewById(R.id.btLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });


        findViewById(R.id.btLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mAuthenticationStatusObserver = new AuthenticationStatusObserver(this, new AuthenticationStatusObserver.IStatusChangeCallback() {
            @Override
            public void onAuthenticationStatusChanged(String status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addLineToStatus("*************Authentication changed**************");
                        addLineToStatus(status);
                        addLineToStatus("*************************************************");
                    }
                });
            }

            @Override
            public void onError(String error) {
                addLineToStatus(error);
            }

            @Override
            public void onDebugStatus(String message) {
                addLineToStatus(message);
            }
        });
    }

    @Override
    protected void onResume() {
        mScrollDownHandler = new Handler(Looper.getMainLooper());
        mAuthenticationStatusObserver.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mScrollDownRunnable != null)
        {
            mScrollDownHandler.removeCallbacks(mScrollDownRunnable);
            mScrollDownRunnable = null;
            mScrollDownHandler = null;
        }
        mAuthenticationStatusObserver.stop();
        super.onPause();
    }

    private void getCurrentSession()
    {
        IGCRHelper.getCurrentSession(this, new IIGSessionResultCallback() {
            @Override
            public void onSuccess(Cursor result) {
                addLineToStatus("************Current Session**************");
                addLineToStatus(IGCRHelper.cursorToString(result));
                addLineToStatus("*****************************************");
            }

            @Override
            public void onError(String message) {
                addLineToStatus("************Current Session**************");
                addLineToStatus(message);
                addLineToStatus("*****************************************");
            }

            @Override
            public void onDebugStatus(String message) {
                addLineToStatus(message);
            }
        });
    }

    private void getPreviousSession()
    {
        IGCRHelper.getPreviousSession(this, new IIGSessionResultCallback() {
            @Override
            public void onSuccess(Cursor result) {
                addLineToStatus("************Previous Session*************");
                addLineToStatus(IGCRHelper.cursorToJson(result).toString());
                addLineToStatus("*****************************************");
            }

            @Override
            public void onError(String message) {
                addLineToStatus("************Previous Session*************");
                addLineToStatus("*****************ERROR*******************");
                addLineToStatus(message);
                addLineToStatus("*****************************************");
            }

            @Override
            public void onDebugStatus(String message) {
                addLineToStatus(message);
            }
        });
    }

    private void login()
    {
        IGCRHelper.sendAuthenticationRequest(this, IGCRHelper.EAuthenticationScheme.authenticationScheme1, IGCRHelper.EAuthenticationFlag.blocking, new IIGAuthenticationResultCallback() {
            @Override
            public void onSuccess(String message) {
                addLineToStatus("************Send Auth Request*************");
                addLineToStatus(message);
                addLineToStatus("******************************************");
            }

            @Override
            public void onError(String message) {
                addLineToStatus("************Send Auth Request*************");
                addLineToStatus("*****************ERROR*******************");
                addLineToStatus(message);
                addLineToStatus("******************************************");
            }

            @Override
            public void onDebugStatus(String message) {
                addLineToStatus(message);
            }
        });
    }

    private void logout()
    {
        IGCRHelper.logout(this, new IIGAuthenticationResultCallback() {
            @Override
            public void onSuccess(String message) {
                addLineToStatus("***************Logout*******************");
                addLineToStatus(message);
                addLineToStatus("*****************************************");
            }

            @Override
            public void onError(String message) {
                addLineToStatus("***************Logout*******************");
                addLineToStatus("*****************ERROR*******************");
                addLineToStatus(message);
                addLineToStatus("*****************************************");
            }

            @Override
            public void onDebugStatus(String message) {
                addLineToStatus(message);
            }
        });
    }

    private void addLineToStatus(final String lineToAdd)
    {
        mStatus += lineToAdd + "\n";
        updateAndScrollDownTextView();
    }

    private void updateAndScrollDownTextView()
    {
        if(mOptmizeRefresh)
        {
            if(mScrollDownRunnable == null)
            {
                mScrollDownRunnable = new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvStatus.setText(mStatus);
                                svStatus.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        svStatus.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                            }
                        });
                    }
                };
            }
            else
            {
                // A new line has been added while we were waiting to scroll down
                // reset handler to repost it....
                mScrollDownHandler.removeCallbacks(mScrollDownRunnable);
            }
            mScrollDownHandler.postDelayed(mScrollDownRunnable, 300);
        }
        else
        {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(mStatus);
                    svStatus.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

    }
}