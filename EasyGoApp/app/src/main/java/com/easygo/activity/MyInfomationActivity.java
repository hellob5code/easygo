package com.easygo.activity;

import android.app.DatePickerDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easygo.application.MyApplication;
import com.easygo.beans.Order;
import com.easygo.beans.User;
import com.easygo.utils.UpYunException;
import com.easygo.utils.UpYunUtils;
import com.easygo.utils.Uploader;
import com.easygo.view.MyPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

public class MyInfomationActivity extends AppCompatActivity implements View.OnClickListener {
    //第三方图片存储
    private static final String DEFAULT_DOMAIN = "easygo.b0.upaiyun.com";// 空间域名
    private static final String API_KEY = "3AtEDO2ByBUZ7qGVTLPUnuKLOWM="; // 表单api验证密钥
    private static final String BUCKET = "easygo";// 空间名称
    private static final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10; // 过期时间，必须大于当前时间

    public static final int TAKE_PHOTO = 1;
    public static final int TAKE_CAMERA = 0;
    private ImageView mReturnImageView;
    private RelativeLayout mChangeHeadLayout, mChangeUsernameLayout, mChangeRealnameLayout, mChangeCityLayout, mChangeSexLayout, mChangeLabelLayout, mChangeAutographLayout, mChangeDateBirthLayout, mChangePasswordLayout;
    private TextView mChangeHeadTextView, mChangeUsernameTextView, mChangeRealnameTextView, mChangeCityTextView, mChangeSexTextView, mChangeLabelTextView, mChangeAutographTextView, mChangeDateBirthTextView, mChangePasswordTextView;
    private Button mSuccessButton;
    private MyPopupWindow popMenus;

    //复选框
    boolean[] flags = new boolean[]{false, false, false, false, false, false, false, false, false, false};


    public static final String TYPE = "type";
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    private File mCurrentPhotoFile;//获取当前相册选中的图片文件
    private String mphotopath;//相册图片的地址
    private String mloadpath;//图片上传到服务器的地址
    private ImageView mshowImageView;//显示用户头像
    private ClipboardManager clipboard;
    //nohttp
    public static final int WHAT = 1;
    private String mUrl;
    private RequestQueue mRequestQueue;//请求队列

    private int user_id = 1;
    private User mUser;

    private OnResponseListener<String> mOnResponseListener = new OnResponseListener<String>() {
        @SuppressWarnings("unused")
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == WHAT) {
                String result = response.get();
                //把JSON格式的字符串改为Student对象
                Gson gson = new Gson();
                Type type = new TypeToken<List<Order>>() {
                }.getType();
//                mList = gson.fromJson(result,type);
                // mList.addAll((List<Order>)gson.fromJson(result,type));
                // mCustomOrderAdapter.notifyDataSetChanged();
                //表示刷新完成
//                mPullToRefreshListView.onRefreshComplete();
                // Log.e("list",mList.toString());
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_infomation);
        initView();
        addListeners();
        /*//测试 网络图片可以
        Glide.with(MyInfomationActivity.this).load("http://img5.imgtn.bdimg.com/it/u=3921194872,1207178069&fm=21&gp=0.jpg").into(mshowImageView);
*/
    }

    private void initView() {
        mReturnImageView = (ImageView) findViewById(R.id.myinformation_return);

        mChangeHeadLayout = (RelativeLayout) findViewById(R.id.change_head);
        mChangeUsernameLayout = (RelativeLayout) findViewById(R.id.change_username);
        mChangeRealnameLayout = (RelativeLayout) findViewById(R.id.change_realname);
        mChangeCityLayout = (RelativeLayout) findViewById(R.id.change_city);
        mChangeSexLayout = (RelativeLayout) findViewById(R.id.change_sex);
        mChangeLabelLayout = (RelativeLayout) findViewById(R.id.change_label);
        mChangeAutographLayout = (RelativeLayout) findViewById(R.id.change_autograph);
        mChangeDateBirthLayout = (RelativeLayout) findViewById(R.id.change_date_birth);
        mChangePasswordLayout = (RelativeLayout) findViewById(R.id.change_password);

        mSuccessButton = (Button) findViewById(R.id.myinformation_success);

        mChangeUsernameTextView = (TextView) findViewById(R.id.change_username_textView);
        mChangeRealnameTextView = (TextView) findViewById(R.id.change_realname_textView);
        mChangeSexTextView = (TextView) findViewById(R.id.change_sex_textView);
        mChangeCityTextView = (TextView) findViewById(R.id.change_city_textView);
        mChangeLabelTextView = (TextView) findViewById(R.id.change_label_textView);
        mChangeAutographTextView = (TextView) findViewById(R.id.change_autograph_textView);
        mChangeDateBirthTextView = (TextView) findViewById(R.id.change_date_birth_textView);
        mChangePasswordTextView = (TextView) findViewById(R.id.change_password_textView);
        mshowImageView = (ImageView) findViewById(R.id.photo_show);
    }

    private void addListeners() {
        mReturnImageView.setOnClickListener(this);
        mChangeHeadLayout.setOnClickListener(this);
        mChangeUsernameLayout.setOnClickListener(this);
        mChangeRealnameLayout.setOnClickListener(this);
        mChangeCityLayout.setOnClickListener(this);
        mChangeSexLayout.setOnClickListener(this);
        mChangeLabelLayout.setOnClickListener(this);
        mChangeAutographLayout.setOnClickListener(this);
        mChangeDateBirthLayout.setOnClickListener(this);
        mChangePasswordTextView.setOnClickListener(this);
        //mSuccessButton.setOnClickListener(this);
    }

    public void success(View view) {
        //第一个参数：偏好设置文件的名称；第二个参数：文件访问模式
        mSharedPreferences = getSharedPreferences(TYPE, MODE_PRIVATE);
        //向偏好设置文件中保存数据
        mEditor = mSharedPreferences.edit();
        mEditor.putInt("type", 2);
        //提交保存结果
        mEditor.commit();
        Intent intent = new Intent();
        intent.putExtra("flag", "me");
        intent.setClass(MyInfomationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.myinformation_return:
                Intent intent = new Intent();
                intent.putExtra("flag", "me");
                intent.setClass(MyInfomationActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.change_head:
                showChangeHeadDialog();
                break;
            case R.id.change_username:
                showChangeUsernameDialog();
                break;
            case R.id.change_realname:
                showChangeRealnameDialog();
                break;
            case R.id.change_city:
                showChangeCityDialog();
                break;
            case R.id.change_sex:
                showChangeSexDialog();
                break;
            case R.id.change_label:
                showChangeLabelDialog();
                break;
            case R.id.change_autograph:
                showChangeAutographDialog();
                break;
            case R.id.change_date_birth:
                showChangeDateBirthDialog();
                break;
            case R.id.change_password:
                showChangePasswordDialog();
                break;
            case R.id.myinformation_success:
                //点击完成按钮
                MyApplication myApplication = (MyApplication) this.getApplication();
                mUrl = myApplication.getUrl();
                //创建请求队列，默认并发3个请求，传入你想要的数字可以改变默认并发数，例如NoHttp.newRequestQueue(1);
                mRequestQueue = NoHttp.newRequestQueue();
                //创建请求对象
                Request<String> request = NoHttp.createStringRequest(mUrl, RequestMethod.GET);
                //添加请求参数
                request.add("methods", "addHouse");
                request.add("user_photo", mloadpath);
                break;
        }
    }

    //填写自己的真实姓名
    private void showChangeRealnameDialog() {
        final EditText text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入你的真实姓名：")
                .setView(text)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChangeRealnameTextView.setText(text.getText().toString());
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }

    //更换密码
    private void showChangePasswordDialog() {

    }

    //选择出生日期
    private void showChangeDateBirthDialog() {
        int year, monthOfYear, dayOfMonth;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(MyInfomationActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mChangeDateBirthTextView.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, year, monthOfYear, dayOfMonth);

        datePickerDialog.show();
    }

    //编辑自己的个性签名
    private void showChangeAutographDialog() {
        final EditText text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入你的个性签名：")
                .setView(text)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChangeAutographTextView.setText(text.getText().toString());
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }

    //选择标签
    private void showChangeLabelDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择标签");
        builder.setMultiChoiceItems(R.array.label, flags, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String label = getResources().getStringArray(R.array.label)[which];
                mChangeLabelTextView.setText(label);
            }
        });
        //添加一个确定按钮
        builder.setPositiveButton(" 确 定 ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    //更换性别
    private void showChangeSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(R.array.sex, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sexStr = getResources().getStringArray(R.array.sex)[which];
                mChangeSexTextView.setText(sexStr);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //选择所在地
    private void showChangeCityDialog() {

    }

    //更换昵称
    private void showChangeUsernameDialog() {
        final EditText text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入昵称：")
                .setView(text)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChangeUsernameTextView.setText(text.getText().toString());
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }

    //更换头像
    private void showChangeHeadDialog() {
        //自定义的弹出框类
        mChangeHeadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popMenus = new MyPopupWindow(MyInfomationActivity.this, itemsOnClick);
                popMenus.showAtLocation(MyInfomationActivity.this.findViewById(R.id.change_head),
                        Gravity.BOTTOM, 0, 0);
            }
        });

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            popMenus.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    //拍照上传
                    Toast.makeText(MyInfomationActivity.this, "点击了拍照", Toast.LENGTH_SHORT).show();
                    takeCamera();
                    break;
                case R.id.btn_pick_photo:
                    //从相册获取
                    Toast.makeText(MyInfomationActivity.this, "点击了从相册获取", Toast.LENGTH_SHORT).show();
                    takePhoto();
                    break;
                default:
                    break;
            }
        }
    };

    //拍照
    private void takeCamera() {
        String photoDir = Environment.getExternalStorageDirectory().getPath() + "/devstore_image";
        try {
            File photo_dir = new File(photoDir);
            if (!photo_dir.exists()) {
                photo_dir.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(photo_dir, fileName);
            // 开启相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, TAKE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //相册上传
    private void takePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    //上传图片
    public class UploadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String str = null;
            try {
                Log.i("test", "文件大小：" + new File(params[0]).length() / (1024 * 1024f) + "M");
                // 设置服务器上保存文件的目录和文件名，如果服务器上同目录下已经有同名文件会被自动覆盖的。
                String SAVE_KEY = File.separator + "user" + File.separator + System.currentTimeMillis() + ".jpg";
                // 取得base64编码后的policy
                String policy = null;
                policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);
                // 根据表单api签名密钥对policy进行签名
                // 通常我们建议这一步在用户自己的服务器上进行，并通过http请求取得签名后的结果。
                String signature = UpYunUtils.signature(policy + "&" + API_KEY);
                long startTime = System.currentTimeMillis();
                Log.i("test", "开始上传：" + startTime);
                // 上传文件到对应的bucket中去。
                str = DEFAULT_DOMAIN + Uploader.upload(policy, signature, BUCKET, params[0]);
//                Toast.makeText(MyInfomationActivity.this, "上传用时"+(System.currentTimeMillis() - startTime) + "ms", Toast.LENGTH_SHORT).show();
                Log.i("test", "上传完成：" + (System.currentTimeMillis() - startTime) + "ms");
            } catch (UpYunException e) {
                e.printStackTrace();
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mloadpath = result;//得到上传后的图片地址
            Log.e("上传地址", result);
        }
    }

   /* private void photoshow() {
        //显示本本地图片，显示网络图片有延迟（异步）
        if (mphotopath != null) {
            Glide.with(MyInfomationActivity.this).load(mphotopath).into(mshowImageView);
            mphotopath = null;
        } else if (mloadpath != null) {
            Glide.with(MyInfomationActivity.this).load(mCurrentPhotoFile.getAbsolutePath()).into(mshowImageView);
            mloadpath = null;
            mphotopath = null;
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                //相册选取
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        Glide.with(MyInfomationActivity.this).load(getPhotoPath(this, selectedImage)).into(mshowImageView);
                        //得到该图片在手机中的路径
                       // new UploadTask().execute(getPhotoPath(this, selectedImage));
                    }
                }
                break;
            case TAKE_CAMERA:
                Glide.with(MyInfomationActivity.this).load(mCurrentPhotoFile.getAbsolutePath()).into(mshowImageView);
                //拍照选取
                //new UploadTask().execute(mCurrentPhotoFile.getAbsolutePath());
                break;
        }
    }

    //相册图片上传地址
    private String getPhotoPath(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        //光标
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        mphotopath = picturePath;
        cursor.close();
        return picturePath;
    }
}
