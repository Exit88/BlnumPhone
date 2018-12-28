package com.example.blnumphone.activity;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blnumphone.MyDialog.MyDialog;
import com.example.blnumphone.MyFragment.SwipeListLayout;
import com.example.blnumphone.R;
import com.example.blnumphone.Service.ListenPhoneService;
import com.example.blnumphone.Utils.Utils;
import com.example.blnumphone.dbdao.BlnumDao;
import com.example.blnumphone.dbdao.TelInfodb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class BlnumActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listview;
    private Set<SwipeListLayout> sets = new HashSet();
    public ArrayList<String> list;


    private Button btn_blnum;               //黑名单
    private Button btn_messges;             //拦截记录
    private Button btn_add;                 //添加黑名单
    private Button btn_record;              //录音记录
    private Button btn_setting;             //设置
    private Button btn_search;              //搜索
    private Button delete_all;              //清空
    private Button shareall;                //分享

    private SwipeRefreshLayout swipeRefreshLayout;      //刷新

    private EditText dialog_update_blacknum;            //修改黑名单号码框
    private EditText dialog_update_blackcode;           //修改黑名单拦截类型框
    public String code;

    MyDialog myDialog;                                  //自定义dialog
    TelInfodb telInfodb;                                //数据库创建类
    BlnumDao blnumDao;                                  //数据库操作类
    SQLiteDatabase sqLiteDatabase;
    BlackNumBaseAdapter adapter;                        //黑名单适配器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blnum);
        telInfodb = new TelInfodb(this);
        myDialog = new MyDialog(this);
        blnumDao = new BlnumDao(this);
        adapter = new BlackNumBaseAdapter(list);


        btn_blnum = (Button) findViewById(R.id.btn_blnum);               //黑名单
        btn_messges = (Button) findViewById(R.id.btn_messges);           //拦截记录
        btn_add = (Button) findViewById(R.id.btn_add);                   //添加黑名单
        btn_record = (Button) findViewById(R.id.btn_record);             //录音记录
        btn_setting = (Button) findViewById(R.id.btn_setting);           //设置
        btn_search = (Button) findViewById(R.id.btn_search);             //搜索
        delete_all = (Button) findViewById(R.id.delete_all);             //清空
        shareall = (Button) findViewById(R.id.share_all);                //分享
        listview = (ListView) findViewById(R.id.lv_black_num);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        btn_blnum.setOnClickListener(this);
        btn_messges.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        delete_all.setOnClickListener(this);
        shareall.setOnClickListener(this);

        showBlackNum();
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {//左划
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //当listview开始滑动时，若有item的状态为Open，则Close，然后移除
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (sets.size() > 0) {
                            for (SwipeListLayout s : sets) {
                                s.setStatus(SwipeListLayout.Status.Close, true);
                                sets.remove(s);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadDataThread().start();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(BlnumActivity.this,"刷新成功",Toast.LENGTH_LONG).show();
                    showBlackNum();
                    if (swipeRefreshLayout.isRefreshing()){
                        // adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    }
                    break;
            }
        }
    };

    /**
     * 模拟加载数据的线程
     */
    class LoadDataThread extends  Thread{
        @Override
        public void run() {
            initData();
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(1);//通过handler发送一个更新数据的标记
        }
        private void initData() {
            //list.addAll(Arrays.asList("Json","XML","UDP","http"));
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_messges:
                Intent messges = new Intent(this,MainActivity.class);         //拦截记录页
                startActivity(messges);
                break;
            case R.id.btn_blnum:
                showBlackNum();
                break;
            case R.id.btn_add:
                myDialog.addBlackNumDialog();
                break;
            case R.id.btn_record:
                Intent record = new Intent(this,RecordActivity.class);         //录音记录页
                startActivity(record);
                break;
            case R.id.btn_setting:
                Intent setting = new Intent(this,SettingActivity.class);         //设置页
                startActivity(setting);
                break;
            case R.id.btn_search:
                Intent search = new Intent(this,SearchActivity.class);         //设置页
                startActivity(search);
                break;
            case R.id.share_all:
                if (list.size() == 0){
                    Toast.makeText(BlnumActivity.this,"黑名单为空！",Toast.LENGTH_LONG).show();
                }else {
                    shareText("分享所有黑名单","黑名单","黑名单号码:"+"\n"+ String.valueOf(list));
                }

                break;
            case R.id.delete_all:
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>"+list.size());
                if (list.size()==0){
                    Toast.makeText(BlnumActivity.this,"黑名单很干净喔！",Toast.LENGTH_LONG).show();
                }else {
                    new AlertDialog.Builder(BlnumActivity.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("提示")
                            .setMessage("确定要清空黑名单吗")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    blnumDao.deleteall();
                                    showBlackNum();
                                    Toast.makeText(BlnumActivity.this,"黑名单已清空！",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
        }
    }

    //黑名单列表
    public void showBlackNum() {
        sqLiteDatabase = telInfodb.getReadableDatabase();
        String array[] = {"telnum"};
        Cursor cursor = sqLiteDatabase.query("telnuminfo", array, null, null, null, null, null);
        int blacknumindex = cursor.getColumnIndex("telnum");
        Log.i(">>>", blacknumindex + "");
        cursor.moveToFirst();
        list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            String blacknum = cursor.getString(blacknumindex);
            list.add(blacknum);
            cursor.moveToNext();
            Log.i(">>>", ">>>>");
            Log.i("Simon", list.toString());
        }
        if (list != null) {
            listview.setAdapter(new BlackNumBaseAdapter(list));
        }
    }

    public void searchnumcode(String incomingNumber){
        sqLiteDatabase = telInfodb.getReadableDatabase();
        Cursor codecursor = sqLiteDatabase.rawQuery("select code from telnuminfo where telnum=?",new String[]{incomingNumber});
        codecursor.moveToNext();
        code = codecursor.getString(codecursor.getColumnIndex("code"));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+code);
        codecursor.close();
        sqLiteDatabase.close();
    }

    class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }

    }
    class BlackNumBaseAdapter extends BaseAdapter {
        private ArrayList<String> list=new ArrayList<String>();
        public BlackNumBaseAdapter(ArrayList<String> list)
        {
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView==null)
            {
                viewHolder=new ViewHolder();
                convertView= LayoutInflater.from(BlnumActivity.this).inflate(R.layout.item_black_num,null);
                viewHolder.textView= (TextView) convertView.findViewById(R.id.tv_black_num);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(list.get(position));
            TextView textViewdelete= (TextView) convertView.findViewById(R.id.tv_delete);
            TextView textViewup= (TextView) convertView.findViewById(R.id.tv_up);
            TextView textViewshsre= (TextView) convertView.findViewById(R.id.tv_share);
            final SwipeListLayout swipeListLayout= (SwipeListLayout) convertView.findViewById(R.id.sll_main);
            swipeListLayout.setOnSwipeStatusListener(new MyOnSlipStatusListener(swipeListLayout));

            /*
            黑名单删除
             */
            textViewdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeListLayout.setStatus(SwipeListLayout.Status.Close, true);
                    sqLiteDatabase = telInfodb.getReadableDatabase();
                    Log.i(">>>",position+"po");
                    Log.i(">>>",list.get(position));
                    String [] args = new String[]{list.get(position).toString()};
                    String [] type = new String[]{};
                    sqLiteDatabase.delete("telnuminfo", "telnum=?",args);
                    showBlackNum();
                    Toast.makeText(BlnumActivity.this,"删除成功！",Toast.LENGTH_LONG).show();
                    sqLiteDatabase.close();
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

             /*
            黑名单修改
             */
            textViewup.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    swipeListLayout.setStatus(SwipeListLayout.Status.Close, true);
                    View mView = View.inflate(BlnumActivity.this, R.layout.dialog_update_blacknum, null);
                    dialog_update_blacknum = (EditText) mView.findViewById(R.id.dialog_update_blacknum); //要用对应布局的view对象去findViewById获取控件对象
                    dialog_update_blackcode = (EditText) mView.findViewById(R.id.dialog_update_blackcode);
                    dialog_update_blacknum.setText(viewHolder.textView.getText());  //获取所选item的号码
                    String number = dialog_update_blacknum.getText().toString().trim();
                    searchnumcode(number);
                    dialog_update_blackcode.setText(code);
                    //System.out.println(">>>>>>>>>>>>>>>>"+dialog_update_blacknum);
                    //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+viewHolder.textView.getText());
                    new AlertDialog.Builder(BlnumActivity.this)
                            .setTitle("提示")
                            .setMessage("是否修改数据")
                            .setView(mView)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    swipeListLayout.setStatus(SwipeListLayout.Status.Close, true);
                                    String blacknum=dialog_update_blacknum.getText().toString().trim();
                                    String upcode = dialog_update_blackcode.getText().toString().trim();

                                    if ((Utils.checkCellphone(blacknum))&&(Utils.checkcode(upcode))) {
                                        sqLiteDatabase = telInfodb.getReadableDatabase();
                                        Log.i(">>>",position+"po");
                                        Log.i(">>>",list.get(position));
                                        ContentValues values = new ContentValues();
                                        String update = dialog_update_blacknum.getText().toString();
                                        String updatecode = dialog_update_blackcode.getText().toString();
                                        values.put("telnum",update);
                                        values.put("code",updatecode);
                                        sqLiteDatabase.update("telnuminfo",values,"telnum=?",new String[]{String.valueOf(viewHolder.textView.getText())});
                                        showBlackNum();
                                        Utils.showToast("修改成功！",BlnumActivity.this);
                                        sqLiteDatabase.close();
                                        notifyDataSetChanged();
                                    }else {
                                        Utils.showToast("请输入正确的手机号码或拦截类型",BlnumActivity.this);
                                    }


                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }

            });

            /*
            黑名单分享
             */
            textViewshsre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeListLayout.setStatus(SwipeListLayout.Status.Close, true);
                    shareText("黑名单分享","黑名单","黑名单号码:"+ String.valueOf(viewHolder.textView.getText()));
                }
            });

            return convertView;
        }
        private class ViewHolder{
            private TextView textView;
        }
    }

    /**
     * 分享文字内容
     *
     * @param dlgTitle
     *            分享对话框标题
     * @param subject
     *            主题
     * @param content
     *            分享内容（文字）
     */
    private void shareText(String dlgTitle, String subject, String content) {
        if (content == null || "".equals(content)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        intent.putExtra(Intent.EXTRA_TEXT, content);

        // 设置弹出框标题
        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
            startActivity(Intent.createChooser(intent, dlgTitle));
        } else { // 系统默认标题
            startActivity(intent);
        }
    }
}