
package com.example.actionbar_tabs_test;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

    public static final class MyFragment extends Fragment {
        public static Fragment newInstance(Context context, int i) {
            Bundle args = new Bundle();
            args.putInt("position", i);
            Fragment f = Fragment.instantiate(context, MyFragment.class.getName(), args);
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_main, container, false);
            TextView text = (TextView) v.findViewById(R.id.text);
            //text.setText(Integer.toString(getArguments().getInt("position")));
            StringBuilder b = new StringBuilder();
            b.append("Tab No.");
            b.append(Integer.toString(getArguments().getInt("position")));
            b.append("\n");
            for(int i = 0; i < 100; ++i){
                b.append("The quick brown fox jumps over the lazy dog ");
            }
            text.setText(b.toString());
            return v;
        }
    }

    private static final class MyHandler extends Handler {
        private final WeakReference<Activity> mOwner;

        MyHandler(Activity owner) {
            mOwner = new WeakReference<Activity>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Activity owner = mOwner.get();
                    if(owner != null){
                        owner.startActivity(new Intent(owner, SubActivity.class));
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private final Handler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 別のアクティビティを起動します
        mHandler.sendEmptyMessage(0);

        //Support package の Fragment を使うときの注意点 - http://y-anz-m.blogspot.jp/2013/05/support-package-fragment.html
        setContentView(new FrameLayout(this));

        // タブを作成します
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 0; i < 3; ++i) {
            actionBar.addTab(actionBar.newTab()
                                      .setText(Integer.toString(i))
                                      .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.replace(android.R.id.content, MyFragment.newInstance(this, tab.getPosition()));
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
