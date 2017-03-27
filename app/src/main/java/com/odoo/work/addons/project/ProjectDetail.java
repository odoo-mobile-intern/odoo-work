package com.odoo.work.addons.project;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.odoo.work.R;
import com.odoo.work.addons.project.utils.CardFragmentPagerAdapter;
import com.odoo.work.addons.project.utils.CardItem;
import com.odoo.work.addons.project.utils.CardPagerAdapter;
import com.odoo.work.addons.project.utils.ShadowTransformer;
import com.odoo.work.core.support.OdooActivity;

/**
 * Created by Vedant on 27-03-2017.
 */

public class ProjectDetail extends OdooActivity {

    private ViewPager viewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_detail);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.text_1));
        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager());

        mCardShadowTransformer = new ShadowTransformer(viewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(viewPager, mFragmentCardAdapter);

        viewPager.setAdapter(mCardAdapter);
        viewPager.setPageTransformer(false, mCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
    }

}
