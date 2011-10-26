package com.example.activityinfo;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.widget.Toast;

public class ActivityInfoExampleActivity extends Activity {

    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    hoge();
                    handler.postDelayed(this, 3000);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }

    void hoge() throws RemoteException {

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        System.out.println("runningAppProcesses:" + appProcesses);

        List<RunningTaskInfo> runningTasks = am.getRunningTasks(32);

        for (RunningAppProcessInfo ai : appProcesses) {

            if (ai.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                continue;

            boolean found = false;
            ComponentName topActivity = null;
            for (RunningTaskInfo task : runningTasks) {

                String pkgName = task.topActivity.getPackageName();
                if (ai.processName.equals(pkgName)) {
                    found = true;
                    topActivity = task.topActivity;
                    break;
                }
            }

            if (!found)
                continue;

            StringBuilder b = new StringBuilder();
            b.append("Package: ");
            b.append('\n');
            b.append('\t');
            b.append(topActivity.getPackageName());
            b.append('\n');
            b.append('\n');
            b.append("Activity: ");
            b.append('\n');
            b.append('\t');
            b.append(topActivity.getClassName());

            Toast.makeText(getApplicationContext(), b.toString(), Toast.LENGTH_SHORT).show();

            break;
        }
    }
}