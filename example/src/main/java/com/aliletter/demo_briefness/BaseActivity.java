package com.aliletter.demo_briefness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import briefness.Briefness;
import briefness.Briefnessor;


/**
 * author  : Black Chopper
 * e-mail  : 4884280@qq.com
 * github  : http://github.com/BlackChopper
 * project : Briefness
 */

public class BaseActivity extends Activity {
    protected Briefnessor briefnessor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        briefnessor = Briefness.bind(this);

    }
}
