package com.example.blnumphone.Utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blnumphone.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    public static void showToast(String string, Context context)
    {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.utils_toast,null,false);
        TextView textView= (TextView) view.findViewById(R.id.tv_toast);
        textView.setText(string);
        Toast toast=new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
    /**
      * 验证手机号码
      *
      * 移动号码段:139、138、137、136、135、134、147、150、151、152、157、158、159、178、182、183、184、187、188
      * 联通号码段:130、131、132、156、185、186、145、176
      * 电信号码段:133、153、177、180、181、189
      *
      * @param cellphone
      * @return
      */
    public static boolean checkCellphone(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[6-8])|(18[0-9]))\\d{8}$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(cellphone);
        return matcher.matches();
    }
    public static boolean checkCellphonetype(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[6-8])|(18[0-9]))";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(cellphone);
        return matcher.matches();
    }


    /**
      * 验证固话号码
      * @param telephone
      * @return
      */
    public static boolean checkTelephone(String telephone) {
        String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)|(([4|8]00)-\\d{7,8}(-\\d{1,4})?)$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(telephone);
        return matcher.matches();
    }

    public static boolean checkTelephonetype(String telephone) {
        String regex = "^(0\\d{2})|(0\\d{3})|(([4|8]00))";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(telephone);
        return matcher.matches();
    }

    /*
    验证拦截类型（1为电话拦截，2为短息拦截，3为都拦截）
     */
    public static boolean checkcode(String code) {
        String regex = "^(1|2|3)";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(code);
        return matcher.matches();
    }
}


