package com.example.blnumphone.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blnumphone.MyDialog.MyDialog;
import com.example.blnumphone.R;
import com.example.blnumphone.dbdao.BlnumDao;
import com.example.blnumphone.dbdao.TelInfodb;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_blnum;               //黑名单
    private Button btn_messges;             //拦截记录
    private Button btn_add;                 //添加黑名单号码
    private Button btn_record;              //录音记录
    private Button btn_setting;             //设置
    private Button main_deleteall;          //清空录音记录
    private Button cancle;                  //取消
    private Button delete;                  //多选删除
    private LinearLayout linelayout;        //底部导航条
    private LinearLayout layout;            //多选操作条

    private ListView listview;
    private Context context;
    private List<String> list = new ArrayList<String>();
    private List<String> selectid = new ArrayList<String>();
    private boolean isMulChoice = false; //是否多选
    private AdapterRecord adapter;

    private MediaPlayer mediaPlayer;
    private File sdDir;                 //获取录音路径
    private File path;

    private RelativeLayout recoder_pic;     //播放录音控件
    private ImageView record_img;           //播放录音图片
    private TextView currentTv;             //"当前时间"
    private int totalTime;                  //“歌曲总时长，用于获取歌曲时长”
    private boolean isStop;                 //是否在播放

    MyDialog myDialog;
    TelInfodb telInfodb;
    BlnumDao blnumDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        myDialog = new MyDialog(this);
        telInfodb = new TelInfodb(this);
        blnumDao = new BlnumDao(this);
        mediaPlayer = new MediaPlayer();
        context = this;

        sdDir = Environment.getExternalStorageDirectory();
        path = new File(sdDir+File.separator +"record");
        Recordinit();


        // 判断SD卡是否存在，并且是否具有读写权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getFileName(files);
        }
        adapter = new AdapterRecord(context,list);
        listview.setAdapter(adapter);
    }

    public void Recordinit(){
        btn_blnum = (Button)findViewById(R.id.btn_blnum);               //黑名单
        btn_messges = (Button)findViewById(R.id.btn_messges);           //拦截记录
        btn_add = (Button)findViewById(R.id.btn_add);                   //添加黑名单
        btn_record = (Button)findViewById(R.id.btn_record);             //录音记录
        btn_setting = (Button)findViewById(R.id.btn_setting);           //设置
        main_deleteall = (Button)findViewById(R.id.main_deleteall);     //清空
        cancle   = (Button)findViewById(R.id.cancle);
        delete   = (Button)findViewById(R.id.delete);

        linelayout = (LinearLayout)findViewById(R.id.linelayout);
        layout = (LinearLayout)findViewById(R.id.relative);

        listview = (ListView)findViewById(R.id.list);

        recoder_pic = (RelativeLayout)findViewById(R.id.recoder_pic);
        record_img = (ImageView)findViewById(R.id.record_img);
        currentTv = (TextView)findViewById(R.id.listen_current_tv);


        btn_blnum.setOnClickListener(this);
        btn_messges.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        cancle.setOnClickListener(this);
        delete.setOnClickListener(this);
        main_deleteall.setOnClickListener(this);
        record_img.setOnClickListener(this);
    }

    //遍历文件夹，获取文件名称
    private String getFileName(File[] files){
        String str = "";
        if (files != null) {	// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()){//检查此路径名的文件是否是一个目录(文件夹)
                    Log.i("zeng", "若是文件目录。继续读1" +file.getName().toString()+file.getPath().toString());
                    getFileName(file.listFiles());
                    Log.i("zeng", "若是文件目录。继续读2" +file.getName().toString()+ file.getPath().toString());
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(".amr")) {
                        String s=fileName.substring(0,fileName.lastIndexOf(".")).toString();
                        Log.i("zeng", "文件名txt：：   " + s);
                        //str += fileName.substring(0,fileName.lastIndexOf("."))+".amr"+"\n";
                        //list.add(fileName.substring(0,fileName.lastIndexOf("."))+".amr");
                        list.add(fileName.substring(0,fileName.lastIndexOf(".")));
                    }
                }
            }
            if (list != null) {
                listview.setAdapter(adapter);
            }
        }
        return str;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_blnum:
                Intent record = new Intent(this,BlnumActivity.class);         //黑名单页
                startActivity(record);
                break;
            case R.id.btn_messges:
                Intent messges = new Intent(this,MainActivity.class);         //拦截记录页
                startActivity(messges);
                break;
            case R.id.btn_add:
                myDialog.addBlackNumDialog();
                break;
            case R.id.btn_setting:
                Intent setting = new Intent(this,SettingActivity.class);         //设置页
                startActivity(setting);
                break;
            case R.id.record_img:
                recoder_pic.setVisibility(View.INVISIBLE);
                mediaPlayer.reset();
                isStop = true;
                break;
            case R.id.main_deleteall:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要清空记录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                Toast.makeText(RecordActivity.this,"小编正在努力建设中...",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.cancle:
                isMulChoice = false;
                selectid.clear();
                adapter = new AdapterRecord(context,list);
                listview.setAdapter(adapter);
                layout.setVisibility(View.INVISIBLE);
                linelayout.setVisibility(View.VISIBLE);
                break;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要删除所选的"+selectid.size()+"项吗？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                isMulChoice =false;
                                for(int i=0;i<selectid.size();i++){
                                    for(int j=0;j<list.size();j++){
                                        if(selectid.get(i).equals(list.get(j))){
                                            Toast.makeText(RecordActivity.this,"小编正在努力建设中...",Toast.LENGTH_LONG).show();
//                                            list.remove(j);
//                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                selectid.clear();
                                adapter = new AdapterRecord(context,list);
                                listview.setAdapter(adapter);
                                layout.setVisibility(View.INVISIBLE);
                                linelayout.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
        isStop = true;
    }

    ///////////////规定需要的时间形式///////////////////////
    private String formatTime(int length) {
        Date date = new Date(length);//调用Date方法获值
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");//规定需要形式
        String TotalTime = simpleDateFormat.format(date);//转化为需要形式
        return TotalTime;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            currentTv.setText(formatTime(msg.what));
        }
    };

    class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer != null && isStop == false) {
                // 将SeekBar位置设置到当前播放位置
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @author jingzi
     * 自定义AdapterRecord
     */
    class AdapterRecord extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater=null;
        private HashMap<Integer, View> mView ;
        public  HashMap<Integer, Integer> visiblecheck ;//用来记录是否显示checkBox
        public  HashMap<Integer, Boolean> ischeck_record;

        public List<String> list;

        public AdapterRecord(Context context,List<String> list)
        {
            this.context = context;
            this.list = list;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = new HashMap<Integer, View>();
            visiblecheck = new HashMap<Integer, Integer>();
            ischeck_record = new HashMap<Integer, Boolean>();
            System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqq123"+this.list.size());
            if(isMulChoice){
                for(int i=0;i<list.size();i++){
                    ischeck_record.put(i, false);
                    visiblecheck.put(i, CheckBox.VISIBLE);
                }
            }else{
                for(int i=0;i<list.size();i++)
                {
                    ischeck_record.put(i, false);
                    visiblecheck.put(i, CheckBox.INVISIBLE);
                }
            }
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        public View getView(final int position, final View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = mView.get(position);
            if(view==null)
            {
                view = inflater.inflate(R.layout.record_item_layout, null);
                TextView textView = (TextView)view.findViewById(R.id.txtName);
                final CheckBox checkBox = (CheckBox)view.findViewById(R.id.record_check);

                textView.setText(list.get(position));

                System.out.println("qqqqqqqqqqqqqqqqqqqqqq"+ischeck_record);
                checkBox.setChecked(ischeck_record.get(position));
                checkBox.setVisibility(visiblecheck.get(position));

                view.setOnLongClickListener(new Onlongclick());

                view.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if(isMulChoice){
                            if(checkBox.isChecked()){
                                checkBox.setChecked(false);
                                selectid.remove(list.get(position));
                            }else{
                                checkBox.setChecked(true);
                                selectid.add(list.get(position));
                            }
                        }else {
                            Toast.makeText(context, "点击了"+list.get(position), Toast.LENGTH_LONG).show();
                            try {
                                currentTv.setText("00:00");
                                recoder_pic.setVisibility(View.VISIBLE);
                                mediaPlayer.reset();//使处于异常的MediaPlayer重置到空闲状态
                                //System.out.println("666666666"+Environment.getExternalStorageDirectory()+"/record/"+list.get(position)+".amr");
                                mediaPlayer.setDataSource(Environment.getExternalStorageDirectory()+"/record/"+list.get(position)+".amr");
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.prepare(); //准备播放
                                mediaPlayer.start();//开始播放

                                totalTime = list.get(position).length();//获取歌曲时长
                                new Thread(new SeekBarThread()).start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            mediaPlayer.pause();//暂停播放，暂停播放之后想要恢复播放可以调用start
//                            mediaPlayer.stop();//停止播放，停止播放之后就算调用start也不能再次播放了。需要再次调用prepare再调用start才能再次播放
//                            mediaPlayer.reset();//使处于异常的MediaPlayer重置到空闲状态
//                            mediaPlayer.release();//释放MediaPlayer
                        }
                    }
                });

                mView.put(position, view);
            }
            return view;
        }

        class Onlongclick implements View.OnLongClickListener {

            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                isMulChoice = true;
                selectid.clear();
                layout.setVisibility(View.VISIBLE);
                for(int i=0;i<list.size();i++)
                {
                    linelayout.setVisibility(View.INVISIBLE);
                    adapter.visiblecheck.put(i, CheckBox.VISIBLE);
                }
                adapter = new AdapterRecord(context,list);
                listview.setAdapter(adapter);
                return true;
            }
        }
    }
}
