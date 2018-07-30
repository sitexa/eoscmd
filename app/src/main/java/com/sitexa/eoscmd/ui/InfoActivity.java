package com.sitexa.eoscmd.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.sitexa.eoscmd.BuildConfig;
import com.sitexa.eoscmd.R;
import com.sitexa.eoscmd.ui.base.BaseActivity;

public class InfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        ((TextView) findViewById(R.id.tv_desc_under_logo)).setText(
                String.format(getString(R.string.app_title_with_ver_fmt), BuildConfig.VERSION_NAME)); // version name
    }
}
