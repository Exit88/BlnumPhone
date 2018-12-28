package com.example.blnumphone.MyDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.blnumphone.R;
import com.example.blnumphone.Utils.Utils;
import com.example.blnumphone.activity.BlnumActivity;
import com.example.blnumphone.dbdao.BlnumDao;

public final class MyDialog extends Dialog{

    EditText editText;                      //黑名单号码输入框
    public static EditText et_black_phone_code;          //拦截类型
    BlnumDao blnumDao;
    BlnumActivity blnumActivity;

    public MyDialog(@NonNull Context context) {
        super(context);
        blnumDao = new BlnumDao(context);
        blnumActivity = new BlnumActivity();
    }

    /*
    添加黑名单dialog
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void addBlackNumDialog() {
        final AlertDialog alertdialog=new AlertDialog.Builder(getContext()).create();
        alertdialog.setCanceledOnTouchOutside(true);
        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_black_num,null,false);
        alertdialog.show();
        //dialog的edittext默认不能显示软键盘，加上这句唤起软键盘
        alertdialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertdialog.setContentView(view);
        alertdialog.getWindow().setGravity(Gravity.CENTER);
        Button surebutton= (Button) view.findViewById(R.id.bt_sure);
        Button cancelbutton= (Button) view.findViewById(R.id.bt_cancel);
        editText = (EditText) view.findViewById(R.id.et_black_phone_num);
        et_black_phone_code = (EditText) view.findViewById(R.id.et_black_phone_code);

        /*
        确认添加黑名单
         */
        surebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String blacknum = editText.getText().toString().trim();
                String code = et_black_phone_code.getText().toString().trim();

                if ((Utils.checkCellphone(blacknum) || Utils.checkTelephone(blacknum)) && (Utils.checkcode(code))) {
                    alertdialog.cancel();
                    blnumDao.insertTelNum(blacknum,code);
                    Utils.showToast("添加成功！", getContext());
                } else {
                    Utils.showToast("请输入正确的手机号码或拦截类型", getContext());
                }
            }
        });
        /*
        取消添加黑名单
         */
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.cancel();
            }
        });
    }
}
