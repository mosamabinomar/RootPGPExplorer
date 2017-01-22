package com.cryptopaths.cryptofm.filemanager.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.FragmentCallbacks;
import com.cryptopaths.cryptofm.filemanager.PagerAdapter;
import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptofm.filemanager.UiUtils;
import com.cryptopaths.cryptofm.filemanager.listview.AdapterCallbacks;
import com.cryptopaths.cryptofm.utils.FileUtils;

public class FilemanagerTabs extends AppCompatActivity implements AdapterCallbacks, FragmentCallbacks{
    private boolean                 isEmptyFolder=false;
    private TabsFragmentOne         mCurrentFragment;
    private PagerAdapter            mPagerAdapter;
    private static final String TAG=FilemanagerTabs.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemanager_tabs);

        setToolbar();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.items_view_menu_item){

            }else{


            }

        return true;
    }

    @Override
    public void onBackPressed() {
        //if user has not switched the page
        if(mCurrentFragment==null){
            mCurrentFragment=mPagerAdapter.getCurrentFragment(0);
        }
        if(isEmptyFolder) {
            removeNoFilesFragment();
        }
        String path=mCurrentFragment.getmCurrentPath();
        Log.d(TAG, "onBackPressed: Current path is: "+path);
        if(mCurrentFragment.getmCurrentPath().equals(SharedData.FILES_ROOT_DIRECTORY)){
            super.onBackPressed();
        }else{
            path		   = path.substring(0,path.lastIndexOf('/'));
            path 		   = path.substring(0,path.lastIndexOf('/')+1);
            FileUtils.CURRENT_PATH = path;
            Log.d(TAG, "onBackPressed: Changing directory");
            mCurrentFragment.changeDirectory(path);
        }

    }

    @Override
    public void init() {

    }

    @Override
    public void finishActionMode() {
        if(this.actionMode!=null){
            this.actionMode.finish();
            this.actionMode=null;
        }else {
            SharedData.SELECTION_MODE=false;
        }
    }

    @Override
    public void tellNoFiles() {
        showNoFilesFragment();
    }

    private void setToolbar(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
       Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        toolbar.setSubtitle("/storage/emulated/0");
       setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), 2);
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragment=mPagerAdapter.getCurrentFragment(position);
                if(actionMode!=null){
                    actionMode.finish();
                    actionMode=null;
                }
                FileUtils.CURRENT_PATH=mCurrentFragment.getmCurrentPath();
                changeTitle(mCurrentFragment.getmCurrentPath());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * adapter callbacks section starting
     */

    ActionMode actionMode;
    @Override
    public void onLongClick() {
        //if user has not switched the page
        if(mCurrentFragment==null){
            mCurrentFragment=mPagerAdapter.getCurrentFragment(0);
        }
        actionMode = startActionMode(mCurrentFragment.getmActionViewHandler());
        UiUtils.actionMode=actionMode;
    }
    @Override
    public void incrementSelectionCount(){
        actionMode.setTitle(++SharedData.SELECT_COUNT+"");
        if(SharedData.SELECT_COUNT>1){
            actionMode.getMenu().removeItem(R.id.rename_menu_item);
        }
    }

    @Override
    public void decrementSelectionCount() {
        if(actionMode!=null){
            actionMode.setTitle(--SharedData.SELECT_COUNT+"");
            if(SharedData.SELECT_COUNT==0){
                actionMode.finish();
            }
            else if(SharedData.SELECT_COUNT<2){
                actionMode.getMenu().add(0,R.id.rename_menu_item,0,"rename");
            }
        }
    }

    @Override
    public void changeTitle(String path) {
        String tmp=path;
        //change the current path in fragment
        if(mCurrentFragment==null){
            mCurrentFragment=mPagerAdapter.getCurrentFragment(0);
        }
        mCurrentFragment.setmCurrentPath(path);


        if(path.equals(SharedData.FILES_ROOT_DIRECTORY)){
            path="Home";
        }else{
            path=path.substring(0,path.lastIndexOf('/'));
            path=path.substring(path.lastIndexOf('/')+1);
        }
        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle(path);
        getSupportActionBar().setSubtitle(tmp);
    }

    @Override
    public void showNoFilesFragment() {
        Log.d("google", "showNoFilesFragment: no files show");
        isEmptyFolder=true;
        FrameLayout layout=(FrameLayout)findViewById(R.id.no_files_frame_fragment);
        View view= getLayoutInflater().inflate(R.layout.no_files_layout,null);
        layout.addView(view);
    }

    @Override
    public void removeNoFilesFragment() {
        isEmptyFolder=false;
        FrameLayout layout=(FrameLayout)findViewById(R.id.no_files_frame_fragment);
        layout.removeAllViews();
    }
}
