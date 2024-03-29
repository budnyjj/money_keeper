package budny.moneykeeper.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import budny.moneykeeper.R;
import budny.moneykeeper.bl.presenters.IPresenterActivityBalanceChangeEdit;
import budny.moneykeeper.bl.presenters.impl.PresenterActivityBalanceChangeEdit;
import budny.moneykeeper.ui.fragments.IFragmentEdit;
import budny.moneykeeper.ui.misc.listeners.IContentEditListener;

/**
 * An activity used to edit specified balance change.
 * Edit action type, index parent account as well as index of balance
 * are specified by arguments bundle.
 */
public class ActivityBalanceChangeEdit extends AppCompatActivity {
    private IContentEditListener mEditListener;
    private IPresenterActivityBalanceChangeEdit mPresenter;

    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar mToolbar;
    @SuppressWarnings("FieldCanBeLocal")
    private TabLayout mTabs;
    @SuppressWarnings("FieldCanBeLocal")
    private ViewPager mPagerView;
    @SuppressWarnings("FieldCanBeLocal")
    private AdapterViewPager mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // pass arguments bundle to presenter
        mPresenter = new PresenterActivityBalanceChangeEdit(this, getIntent().getExtras());
        setContentView(R.layout.activity_balance_change_edit);
        initViews();
        initEditListener();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_input_item_save:
                if (mEditListener.onEditContent()) {
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDefaultDisplayHomeAsUpEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        // view pager
        mTabs = (TabLayout) findViewById(R.id.activity_balance_change_edit_tabs);
        mPagerView = (ViewPager) findViewById(R.id.activity_balance_change_edit_view_pager);
        mPagerAdapter = new AdapterViewPager(getSupportFragmentManager(), mPresenter);
        mPagerView.setAdapter(mPagerAdapter);
        mPagerView.addOnPageChangeListener(new PageChangeListener(mPagerAdapter));
        mTabs.setupWithViewPager(mPagerView);
    }

    /**
     * Initializes edit listener by instantiating the default fragment in owned view pager.
     */
    private void initEditListener() {
        int editType = mPresenter.getSuggestedEditType();
        Object pagerItem = mPagerAdapter.instantiateItem(mPagerView, editType);
        mPagerAdapter.setPrimaryItem(mPagerView, editType, pagerItem);
        mEditListener = mPagerAdapter.getCurrentEditListener(mPagerView.getCurrentItem());
        mPagerView.setCurrentItem(editType);
    }

    /**
     * A view pager adapter that provides access to the active {@linkplain IContentEditListener}.
     */
    private static class AdapterViewPager extends FragmentStatePagerAdapter {
        private final SparseArray<IContentEditListener> mListeners = new SparseArray<>();
        private final IPresenterActivityBalanceChangeEdit mPresenter;

        public AdapterViewPager(@NonNull FragmentManager manager, IPresenterActivityBalanceChangeEdit presenter) {
            super(manager);
            mPresenter = presenter;
        }

        @Override
        public int getItemPosition(Object object) {
            // this is needed to perform full redraw when the dataset is updated
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return mPresenter.getFragmentEdit(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPresenter.getFragmentEditName(position);
        }

        @Override
        public int getCount() {
            return mPresenter.getNumEditTypes();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            IFragmentEdit fragment = (IFragmentEdit) super.instantiateItem(container, position);
            mListeners.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mListeners.remove(position);
            super.destroyItem(container, position, object);
        }

        public IContentEditListener getCurrentEditListener(int position) {
            return mListeners.get(position);
        }
    }

    /**
     * A view page listener, which is used to update current edit listener.
     */
    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        private final AdapterViewPager mPagerAdapter;

        public PageChangeListener(AdapterViewPager pagerAdapter) {
            mPagerAdapter = pagerAdapter;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // update reference to the data change listener
            mEditListener = mPagerAdapter.getCurrentEditListener(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
