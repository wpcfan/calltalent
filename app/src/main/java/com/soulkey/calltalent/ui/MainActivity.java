package com.soulkey.calltalent.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.ui.adapter.MainViewPagerAdapter;
import com.soulkey.calltalent.ui.auth.LoginActivity;
import com.soulkey.calltalent.ui.fragment.FragmentOne;
import com.soulkey.calltalent.ui.fragment.FragmentThree;
import com.soulkey.calltalent.ui.fragment.FragmentTwo;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public final class MainActivity extends BaseActivity {

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.main_tabs_layout)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentOne(), "TabOne");//添加Fragment
        viewPagerAdapter.addFragment(new FragmentTwo(), "TabTwo");
        viewPagerAdapter.addFragment(new FragmentThree(), "TabThree");
        mViewPager.setAdapter(viewPagerAdapter);//设置适配器
        mTabLayout.addTab(mTabLayout.newTab().setText("TabOne"));//给TabLayout添加Tab
        mTabLayout.addTab(mTabLayout.newTab().setText("TabTwo"));
        mTabLayout.addTab(mTabLayout.newTab().setText("TabThree"));
        mTabLayout.setupWithViewPager(mViewPager);//给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
        FloatingActionButton signoutBtn = (FloatingActionButton) findViewById(R.id.SignoutBtn);
        assert signoutBtn != null;
        getSubsCollector().add(signOut(signoutBtn));
    }

    @Override
    protected void injectBaseActivityComponent(BaseActivityComponent component) {
        component.inject(this);
    }

    private Subscription signOut(View signoutBtn) {
        return RxView.clicks(signoutBtn)
                .flatMap(__ -> userModel.signOut())
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    userModel.clearMemoryAndDiskCache();
                    UIHelper.launchActivity(MainActivity.this, LoginActivity.class);
                    finish();
                });
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }
}
