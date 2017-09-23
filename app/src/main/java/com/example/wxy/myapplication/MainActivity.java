package com.example.wxy.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText person;
    private EditText massage;
    private Button send;
    private String[] permissions = {Manifest.permission.SEND_SMS};
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                //showDialogTipUserRequestPermission();
                startRequestPermission();
            }
        }

    person=(EditText )findViewById(R.id.person ) ;
        massage =(EditText )findViewById(R.id.massage) ;
        send=(Button )findViewById(R.id.send) ;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MassageDetial=massage .getText().toString();
                String PhoneNumber=person.getText() .toString() ;
                int num=0,start=0,end=0,tol=0;
                String[] phone=new String[100];//一次最多写100条短信
                for(int i=0;i<PhoneNumber .length();i++){//代码核心（？）读到不是数字的东西就开始记录手机号
                    if(PhoneNumber .charAt(i)<48||PhoneNumber .charAt(i)>57 ){
                        end=i;
                        phone[num]=PhoneNumber .substring(start,end);
                        start=i+1;
                        num++;
                    }
                }
                for(int k=0;k<num;k++){
                    sendSMS(phone[k],MassageDetial);
                }
            }
        }) ;
    }private void showDialogTipUserRequestPermission() {

    new AlertDialog.Builder(this)
            .setTitle("SMS权限不可用")
            .setMessage("由于此应用需要获取发送短信的权限，请您授予该项权限；\n否则，您将无法正常使用它")
            .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startRequestPermission();
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setCancelable(false).show();
}

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("SMS权限不可用")
                .setMessage("请在-应用设置-权限-中，允许它使用SMS权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public final static String PHONE_PATTERN = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    public static boolean isMatchered(String PHONE_PATTERN, String input) {
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
    private void sendSMS(String phoneNum, String message) {//核心代码段之二，调用发送短信的接口
        //初始化发短信SmsManager类
        SmsManager smsManager = SmsManager.getDefault();
        if(phoneNum==null) {
            Toast.makeText(this, "You must edit phoneNumber!", Toast.LENGTH_SHORT).show();
        }
        else if(message.equals(""))
            Toast.makeText(this,"You must edit message!",Toast.LENGTH_SHORT).show();
        else if(!isMatchered(PHONE_PATTERN,phoneNum)){
            Toast.makeText(this,"Wrong Number!",Toast.LENGTH_SHORT).show();
        }
        else{
            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0);
        //如果短信内容长度超过70则分为若干条发
            if (message.length() > 70) {
                Toast.makeText(this, "字数超过70！请重新编写！", Toast.LENGTH_SHORT)
                        .show();
            } else {
                smsManager.sendTextMessage(phoneNum.trim(), null, message, pi, null);
                Toast.makeText(this, "Send Message Success!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}

