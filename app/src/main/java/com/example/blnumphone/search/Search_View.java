package com.example.blnumphone.search;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blnumphone.R;
import com.example.blnumphone.Utils.Utils;
import com.example.blnumphone.activity.BlnumActivity;
import com.example.blnumphone.dbdao.TelInfodb;


public class Search_View extends LinearLayout {

    private Context context;

    /*UI组件*/
    private TextView tv_clear_history;
    private EditText et_search;
    private EditText dialog_update_blacknum;        //修改框
    private EditText dialog_update_blackcode;        //修改框
    private TextView tv_tip;
    private ImageView iv_search;
    private ImageView iv_back;
    private ImageView iv_cha;
    private View view_2;

    /*列表及其适配器*/
    private Search_Listview listView;
    private BaseAdapter adapter;

    /*数据库变量*/
    private TelInfodb telInfodb;
    private SQLiteDatabase sqLiteDatabase;
    private String code;



    /*三个构造函数*/
    //在构造函数里直接对搜索框进行初始化 - init()
    public Search_View(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Search_View(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public Search_View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    /*初始化搜索框*/
    private void init() {

        //初始化UI组件
        initView();


        //实例化数据库SQLiteOpenHelper子类对象
        telInfodb = new TelInfodb(context);

        // 第一次进入时查询所有的历史记录
        queryData("");

        //"清空搜索历史"按钮
        tv_clear_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //清空数据库
                deleteData();
                queryData("");
            }
        });

        //搜索框的文本变化实时监听
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //输入后调用该方法
            @Override
            public void afterTextChanged(Editable s) {

                //每次输入后都查询数据库并显示
                //根据输入的值去模糊查询数据库中有没有数据
                String tempName = et_search.getText().toString();

                if (s.toString().trim().length() == 0) {
                    //若搜索框为空,则模糊搜索空字符,即显示所有的搜索历史
                    tv_tip.setText("搜索历史");
                    iv_cha.setVisibility(View.INVISIBLE);
                    tv_clear_history.setVisibility(View.VISIBLE);
                    view_2.setVisibility(View.VISIBLE);
                    queryData("");
                } else {
                    tv_tip.setText("搜索结果");
                    iv_cha.setVisibility(View.VISIBLE);
                    tv_clear_history.setVisibility(View.INVISIBLE);
                    view_2.setVisibility(View.INVISIBLE);
                    queryBlnumData(tempName);
                }
            }
        });


        // 搜索框的键盘搜索键
        // 点击回调
        et_search.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键


            // 修改回车键功能
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 隐藏键盘，这里getCurrentFocus()需要传入Activity对象，如果实际不需要的话就不用隐藏键盘了，免得传入Activity对象，这里就先不实现了
//                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
//                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                    boolean hasData = hasData(et_search.getText().toString().trim());
                    if (!hasData) {
                        insertData(et_search.getText().toString().trim());

                        queryBlnumData(et_search.getText().toString().trim());
                        queryData("");
                    }
                    //根据输入的内容模糊查询商品，并跳转到另一个界面，这个需要根据需求实现
                    // Toast.makeText(context, "点击搜索", Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });


        //列表监听
        //即当用户点击搜索历史里的字段后,会直接将结果当作搜索字段进行搜索
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (tv_tip.getText().equals("搜索历史")) {
                    //获取到用户点击列表里的文字,并自动填充到搜索框内
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    String name = textView.getText().toString();
                    et_search.setText(name);
                    Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
                } else if (tv_tip.getText().equals("搜索结果")) {
                    //Toast.makeText(context, "888", Toast.LENGTH_SHORT).show();

                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    final String name = textView.getText().toString();
                    boolean hasData = hasData(et_search.getText().toString().trim());

                    new AlertDialog.Builder(context)
                            .setTitle("提示！")
                            .setMessage("删除/修改此号码："+name)
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    sqLiteDatabase = telInfodb.getWritableDatabase();
                                    sqLiteDatabase.delete("telnuminfo","telnum=?",new String[]{name});
                                    Toast.makeText(context,"删除成功！",Toast.LENGTH_LONG).show();
                                    sqLiteDatabase.close();
                                    queryBlnumData(et_search.getText().toString().trim());
                                }
                            })
                            .setNegativeButton("修改", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    View mView = View.inflate(context, R.layout.dialog_update_blacknum, null);
                                    dialog_update_blacknum = (EditText) mView.findViewById(R.id.dialog_update_blacknum); //要用对应布局的view对象去findViewById获取控件对象
                                    dialog_update_blackcode = (EditText) mView.findViewById(R.id.dialog_update_blackcode); //要用对应布局的view对象去findViewById获取控件对象
                                    dialog_update_blacknum.setText(name);  //获取所选item的号码
                                    String number = dialog_update_blacknum.getText().toString().trim();
                                    searchnumcode(number);
                                    dialog_update_blackcode.setText(code);
                                    new AlertDialog.Builder(context)
                                            .setTitle("提示")
                                            .setMessage("以下是您将要修改的号码！")
                                            .setView(mView)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String blacknum=dialog_update_blacknum.getText().toString().trim();
                                                    String blackcode = dialog_update_blackcode.getText().toString().trim();
                                                    if ((Utils.checkCellphone(blacknum))&&(Utils.checkcode(blackcode))) {
                                                        sqLiteDatabase = telInfodb.getReadableDatabase();
                                                        ContentValues values = new ContentValues();
                                                        String update = dialog_update_blacknum.getText().toString();
                                                        String upcode = dialog_update_blackcode.getText().toString();
                                                        values.put("telnum",update);
                                                        values.put("code",upcode);
                                                        sqLiteDatabase.update("telnuminfo",values,"telnum=?",new String[]{name});
                                                        Utils.showToast("修改成功！",context);
                                                        sqLiteDatabase.close();
                                                        queryBlnumData(et_search.getText().toString().trim());
                                                    }else {
                                                        Utils.showToast("请输入正确的手机号码",context);
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", null)
                                            .show();
                                }
                            })
                            .show();
                    if (!hasData) {
                        insertData(et_search.getText().toString().trim());
                        // Toast.makeText(context, "333", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        //点击搜索按钮后的事件
        iv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasData = hasData(et_search.getText().toString().trim());
                if ((!hasData)&&(!et_search.getText().equals(null))) {
                    insertData(et_search.getText().toString().trim());

                    //搜索后显示数据库里所有搜索历史是为了测试
                    queryBlnumData(et_search.getText().toString().trim());

                }
                //根据输入的内容模糊查询商品，并跳转到另一个界面，这个根据需求实现
               // Toast.makeText(context, "clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        //点击返回按钮后的事件
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent();
                back.setClass(getContext(),BlnumActivity.class);
                context.startActivity(back);
            }
        });

        //点击清空et_search
        iv_cha.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText(null);
            }
        });

    }

    /**
     * 封装的函数
     */

    /*初始化组件*/
    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.search_layout, this);
        et_search = (EditText) findViewById(R.id.et_search);
        tv_clear_history = (TextView) findViewById(R.id.tv_clear_history);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        listView = (Search_Listview) findViewById(R.id.listView);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_cha = (ImageView)findViewById(R.id.iv_cha);
        view_2 = (View)findViewById(R.id.view_2);
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

    /*插入数据*/
    private void insertData(String tempName) {
        sqLiteDatabase = telInfodb.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into history(name) values('" + tempName + "')");
        sqLiteDatabase.close();
    }

    /*模糊查询数据 并显示在ListView列表上*/
    private void queryData(String tempName) {

        //模糊搜索
        Cursor cursor = telInfodb.getReadableDatabase().rawQuery(
                "select id as _id,name from history where name like '%" + tempName + "%' order by id desc ", null);
        // 创建adapter适配器对象,装入模糊搜索的结果
        adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, cursor, new String[]{"name"},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /*模糊查询数据 并显示在ListView列表上*/
    private void queryBlnumData(String tempName) {

        //模糊搜索
        Cursor cursor = telInfodb.getReadableDatabase().rawQuery(
                "select id as _id,telnum from telnuminfo where telnum like '%" + tempName + "%' order by id desc ", null);
        // 创建adapter适配器对象,装入模糊搜索的结果
        adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, cursor, new String[]{"telnum"},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /*检查数据库中是否已经有该条记录*/
    private boolean hasData(String tempName) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = telInfodb.getReadableDatabase().rawQuery(
                "select id as _id,name from history where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /*清空数据*/
    private void deleteData() {
        sqLiteDatabase = telInfodb.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from history");
        sqLiteDatabase.close();
    }
}