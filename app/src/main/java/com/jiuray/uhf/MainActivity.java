package com.jiuray.uhf;

import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuray.uhf.command.UhfCommandHelper;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ExpandableListView lvLeftMenu;
    private TextView help, guanyu, tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ///////////////////////////////////
        UhfCommandHelper helper = new UhfCommandHelper();
        helper.reset() ;
        helper.getFirwaremVersion() ;
        helper.inventory(255) ;
        helper.getOutPower() ;

        //////////////////////////////////

        findViews();

        toolbar.setTitle("Toolbar");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        //toolbar.setNavigationIcon(R.mipmap.ic_menu);//设置导航的图标
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //菜单
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "菜单", Toast.LENGTH_SHORT).show();
//            }
//        });
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) ;
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            private String[] title = new String[]{"切换设备", "账户管理"};
            private String[][] title_t = new String[][]{
                    {"设备1", "设备2", "设备3"},
                    {"账户1", "账户2", "账户3"},
            };

            TextView getTextView() {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(MainActivity.this);
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(36, 20, 36, 20);
                textView.setTextSize(20);
                textView.setTextColor(Color.BLACK);
                return textView;
            }

            @Override
            public int getGroupCount() {
                return title.length;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return title_t[groupPosition].length;
            }

            @Override
            public Object getGroup(int groupPosition) {
                return title[groupPosition];
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return title_t[groupPosition][childPosition];
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                LinearLayout ll = new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                TextView textView = getTextView();
                textView.setText(getGroup(groupPosition).toString());
                ll.addView(textView);
                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                LinearLayout ll = new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                TextView textView = getTextView();
                textView.setTextColor(Color.parseColor("#aaaaaa"));
                textView.setText(getChild(groupPosition, childPosition).toString());
                ll.addView(textView);
                return ll;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };

        lvLeftMenu.setAdapter(adapter);
        //去掉自带箭头
        //lvLeftMenu.setGroupIndicator(null);
        lvLeftMenu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                tv.setText(adapter.getChild(groupPosition, childPosition).toString());
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("帮助");
                mDrawerLayout.closeDrawers();
            }
        });
        guanyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("关于");
                mDrawerLayout.closeDrawers();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Toast.makeText(MainActivity.this, "AAAA打开了", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        lvLeftMenu = (ExpandableListView) findViewById(R.id.lv_left_menu);
        help = (TextView) findViewById(R.id.help);
        guanyu = (TextView) findViewById(R.id.guanyu);
        tv = (TextView) findViewById(R.id.tv);
    }

}
