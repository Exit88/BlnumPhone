package com.example.blnumphone.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.blnumphone.MyDialog.MyDialog;
import com.example.blnumphone.R;
import com.example.blnumphone.Utils.Utils;
import com.example.blnumphone.dbdao.BlnumDao;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    //switch开关状态
    public static boolean bsSwitch;
    public static boolean bsSwitch2;
    public static boolean bsSwitch3;
    public static int visibility = View.INVISIBLE;
    public static EditText et_selfsetting;                      //自定义拦截编辑框
    public static String str_et_selfsetting;

    private Button btn_blnum;
    private Button btn_messges;
    private Button btn_add;
    private Button btn_record;
    private Button btn_setting;
    private Button btn_self_sub;                                //自定义拦截确定按钮
    private Button btn_self_cancel;                             //自定义拦截取消按钮

    private SwitchCompat sSwitch;                                //黑名单开关
    private SwitchCompat sSwitch2;                               //拦截400开关
    private SwitchCompat sSwitch3;                               //自定义拦截开关

    private LinearLayout self_line;                              //自定义拦截编辑框，按钮等组件

    MyDialog myDialog;
    BlnumDao blnumDao;

    //保存switch等的状态，数据
    SharedPreferences prefere;
    SharedPreferences.Editor editor;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Settinginit();
        myDialog = new MyDialog(this);
        blnumDao = new BlnumDao(this);
        saveSwitch();;

        sSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (true == b) {
                    // Toast.makeText(SettingActivity.this, "拦截已开启", 1000).show();
                } else {
                    new AlertDialog.Builder(SettingActivity.this)
                            .setTitle("提示！")
                            .setMessage("关闭后将无法实行拦截")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sSwitch2.setChecked(false);
                                    sSwitch3.setChecked(false);
                                    //Toast.makeText(SettingActivity.this, "拦截已关闭", 1000).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sSwitch.setChecked(true);
                                    if (bsSwitch2 == true) {
                                        sSwitch2.setChecked(true);
                                    } else if (bsSwitch3 == true) {
                                        sSwitch3.setChecked(true);
                                    }
                                }
                            })
                            .show();
                }
                bsSwitch = b;
                editor = prefere.edit();
                editor.putBoolean("bsSwitch", bsSwitch);
                editor.commit();

                if (bsSwitch == true) {
                    sSwitch2.setClickable(bsSwitch);
                    sSwitch3.setClickable(bsSwitch);
                } else {
                    sSwitch2.setClickable(bsSwitch);
                    sSwitch3.setClickable(bsSwitch);
                }
            }
        });

        sSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (true == b) {
                    //Toast.makeText(SettingActivity.this, "开关已打开AA", 1000).show();
                } else {
                    //Toast.makeText(SettingActivity.this, "开关已关闭BB", 1000).show();
                }
                bsSwitch2 = b;
                editor = prefere.edit();
                editor.putBoolean("bsSwitch2", bsSwitch2);
                editor.commit();
            }
        });

        sSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (true == b) {
                    //Toast.makeText(SettingActivity.this, "开关已打开AA", 1000).show();
                } else {
                    //Toast.makeText(SettingActivity.this, "开关已关闭BB", 1000).show();
                }
                bsSwitch3 = b;
                editor = prefere.edit();
                editor.putBoolean("bsSwitch3", bsSwitch3);
                editor.commit();
                if (bsSwitch3 == true) {
                    visibility = View.VISIBLE;
                    self_line.setVisibility(visibility);
                } else {
                    visibility = View.INVISIBLE;
                    self_line.setVisibility(visibility);
                }
                editor = prefere.edit();
                editor.putInt("visibility", visibility);
                editor.commit();
            }
        });
    }


    public void Settinginit(){
        btn_blnum = (Button) findViewById(R.id.btn_blnum);               //黑名单
        btn_messges = (Button) findViewById(R.id.btn_messges);           //拦截记录
        btn_add = (Button) findViewById(R.id.btn_add);                   //添加黑名单
        btn_record = (Button) findViewById(R.id.btn_record);             //录音记录
        btn_setting = (Button) findViewById(R.id.btn_setting);           //设置
        btn_self_sub = (Button)findViewById(R.id.btn_self_sub);
        btn_self_cancel = (Button)findViewById(R.id.btn_self_cancel);

        sSwitch = (SwitchCompat) this.findViewById(R.id.slide_switch);               //开启/关闭黑名单
        sSwitch2 = (SwitchCompat) this.findViewById(R.id.slide_switch2);             //开启/关闭指定拦截"400*"
        sSwitch3 = (SwitchCompat) this.findViewById(R.id.slide_switch3);             //开启/关闭自定义拦截

        self_line = (LinearLayout)findViewById(R.id.self_line);
        et_selfsetting = (EditText)findViewById(R.id.et_black_phone_type);


        btn_blnum.setOnClickListener(this);
        btn_messges.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_self_sub.setOnClickListener(this);
        btn_self_cancel.setOnClickListener(this);

    }

    //保存switch等的状态
    public void saveSwitch(){
        prefere = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        bsSwitch = prefere.getBoolean("bsSwitch",bsSwitch);
        bsSwitch2 = prefere.getBoolean("bsSwitch2",bsSwitch2);
        bsSwitch3 = prefere.getBoolean("bsSwitch3",bsSwitch3);
        visibility = prefere.getInt("visibility",visibility);
        str_et_selfsetting = prefere.getString("str_et_selfsetting",str_et_selfsetting);

        sSwitch.setChecked(bsSwitch);
        sSwitch2.setChecked(bsSwitch2);
        sSwitch3.setChecked(bsSwitch3);
        sSwitch2.setClickable(bsSwitch);
        sSwitch3.setClickable(bsSwitch);
        self_line.setVisibility(visibility);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_blnum:
                Intent setting = new Intent(this,BlnumActivity.class);         //黑名单页
                startActivity(setting);
                break;
            case R.id.btn_messges:
                Intent messges = new Intent(this, MainActivity.class);         //拦截记录页
                startActivity(messges);
                break;
            case R.id.btn_add:
                myDialog.addBlackNumDialog();                                                   //添加黑名单
                break;
            case R.id.btn_record:
                Intent record = new Intent(this, RecordActivity.class);         //录音记录页
                startActivity(record);
                break;
            case R.id.btn_self_sub:     //自定义拦截确定按钮
                String str = et_selfsetting.getText().toString().trim();
                System.out.println("wwwwwwwwwwwwwwwwww"+str.length());
                if (Utils.checkCellphonetype(str)||Utils.checkTelephonetype(str)) {
                    str_et_selfsetting =  str;
                    Utils.showToast("添加成功！", SettingActivity.this);
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>"+selfsetting);
                    editor = prefere.edit();
                    editor.putString("str_et_selfsetting", str_et_selfsetting);
                    editor.commit();

                    visibility = View.INVISIBLE;
                    self_line.setVisibility(visibility);
                } else {
                    Utils.showToast("请输入正确的手机号码或拦截类型", SettingActivity.this);
                }
                editor = prefere.edit();
                editor.putInt("visibility", visibility);
                editor.commit();
                break;
            case R.id.btn_self_cancel:      //自定义拦截取消按钮
                visibility = View.INVISIBLE;
                self_line.setVisibility(visibility);
                bsSwitch3 = false;
                sSwitch3.setChecked(bsSwitch3);
                editor = prefere.edit();
                editor.putInt("visibility", visibility);
                editor.putBoolean("bsSwitch3",bsSwitch3);
                editor.commit();
                break;
        }
    }
}


