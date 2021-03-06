package cn.jwjg.jwpd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import cn.jwjg.jwpd.Utils.*;
import cn.jwjg.jwpd.entity.Code;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.jmx.LoggerDynamicMBean;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private Context context;
    private String code;
    private Long number;
    private String productState;
    private Integer lineNo;


    private Long n=0L;
    private int deleteNum;

    private int rowNo;
    private int autoId_code;
    private int autoId2_number;

    private int latestRowNo;
    //存储在excel中最新的ID
    private int LID;

    private int pageIndex=1;
    private int jumpIndex;

    private String fileName;
    private ConstraintLayout constraintLayout;

    private boolean isCanInsert;

    RadioGroup productStateRG1;
    RadioGroup productStateRG2;
    TextView linePositionET;
    EditText lineNoET;

//    @Override
//    protected void onRestoreInstanceState(Bundle outState) {
//        // TODO Auto-generated method stub
//        super.onRestoreInstanceState(outState);
//        Log.d("调用savedInstanceState：", outState+"");
//        outState.putString("code", code);
//        outState.putLong("number", number);
//    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("生命周期：", "onCreate called.");




        setContentView(R.layout.activity_main);
        context = MainActivity.this;          //获得整个Activity的上下文

        //获取读写权限，不能只在AndroidManifest里设置，要手动获取
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


        //创建目录
        String folderName = "EXPORT_PanDian";
        File sdCardDir = new File(getExternalFilesDir(null), folderName);
        if (!sdCardDir.exists()) {
            try {
                if (sdCardDir.mkdirs()) {
                    System.out.println("创建目录成功");
                } else {
                    System.out.println("创建目录失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        //注册所有组件
        final TextView codeNum=findViewById(R.id.codeNum);
//        TextView codeTV=findViewById(R.id.code);
//        TextView numberTV=findViewById(R.id.number);
        final EditText codeTextET=findViewById(R.id.codeText);
        final EditText numberTextET=findViewById(R.id.numberText);
        Button insertBtn=findViewById(R.id.insert);
        EditText userTextET=findViewById(R.id.userText);
//        TextView codeShowTV=findViewById(R.id.codeShow);
//        TextView numberShowTV=findViewById(R.id.numberShow);
        Button exportBtn=findViewById(R.id.export);
        ImageView topPageIV=findViewById(R.id.TopPage);
        ImageView bottomPageIV=findViewById(R.id.BottomPage);
        ImageView lastPageIV=findViewById(R.id.LastPage);
        ImageView nextPageIV=findViewById(R.id.NextPage);
        Button pageJumpBtn=findViewById(R.id.PageJump);
        final EditText CurrentPageIndexET =findViewById(R.id.CurrentPageIndex);
        EditText TotalPageIndexET =findViewById(R.id.TotalPageIndex);
        productStateRG1 =findViewById(R.id.rg_1);
        productStateRG2 =findViewById(R.id.rg_2);
        linePositionET=findViewById(R.id.linePosition);
        lineNoET=findViewById(R.id.lineNo);
        //获取ConstraintLayout对象
        constraintLayout=findViewById(R.id.root);

        String filePath=sdCardDir.getAbsolutePath();
        fileName ="/"+ new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()) + "盘点数据"+".xls";
        fileName=filePath+fileName;
        if (productStateRG1.getCheckedRadioButtonId()==-1){
            if (productStateRG2.getCheckedRadioButtonId()==-1){

            }else{
                productState=((RadioButton)findViewById(productStateRG2.getCheckedRadioButtonId())).getText().toString();
            }
        }else {
            productState=((RadioButton)findViewById(productStateRG1.getCheckedRadioButtonId())).getText().toString();
        }
        Log.d("单选ID：", ""+productStateRG2.getCheckedRadioButtonId());
//        productState=((RadioButton)findViewById(productStateRG.getCheckedRadioButtonId())).getText().toString();

        codeNum.setVisibility(View.GONE);
        CurrentPageIndexET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                    if ( (s.length()!=0)) {
//                        pageIndex = Integer.parseInt(s.toString());
//
//                    }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if ( (s.length()!=0)) {
                        jumpIndex = Integer.parseInt(s.toString());
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        productStateRG1.setOnCheckedChangeListener(this);
        productStateRG2.setOnCheckedChangeListener(this);


//        productStateRG2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.d("TAG2", "onCheckedChanged: ");
////
////                switch (checkedId){
////                    case R.id.rb_1:
////                        //                        System.out.println(((RadioButton)(findViewById(R.id.rb_1))).getText().toString());
////                        productState=((RadioButton)(findViewById(R.id.rb_1))).getText().toString();
////                        break;
////                    case R.id.rb_2:
////                        //                        System.out.println(((RadioButton)(findViewById(R.id.rb_2))).getText().toString());
////                        productState=((RadioButton)(findViewById(R.id.rb_2))).getText().toString();
////                        break;
////                    case R.id.rb_3:
////                        //                        System.out.println(((RadioButton)(findViewById(R.id.rb_3))).getText().toString());
////                        productState=((RadioButton)(findViewById(R.id.rb_3))).getText().toString();
////                        break;
////                    default:
////                        System.out.println("");
////                }
//            }
//        });

        /*检查本地是否已经有盘点数据*/
        int sum=ExcelUtil.getTotalNumber(fileName);
        LID=ExcelUtil.getLatestID(fileName);
        if (sum>0) {
            latestRowNo=sum;
            codeNum.setText(String.format(getResources().getString(R.string.codeNum), latestRowNo));
            codeNum.setVisibility(View.VISIBLE);

            int currentPage=1;
            int totalPage=(sum-1)/10+1;

            CurrentPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex),currentPage));
            TotalPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex),totalPage));

            List<Code> codeList =ExcelUtil.selectAllByPage(fileName,1);
            System.out.println(codeList);
            userTextET.setText(codeList.get(0).getUser());

            //如果有就加载第一页到视图
            for (int i = 1; i <=codeList.size() ; i++) {
                int rowNo=i;
                Long tempid=i*2L-1;
                Long tempid2=i*2L;
                Integer id=Integer.parseInt(tempid.toString());
                Integer id2=Integer.parseInt(tempid2.toString());
                Log.d("id：", ""+id);
                Log.d("id2：", ""+id2);

                //添加codeView
                TextView codeTextView = new TextView(context);
                codeTextView.setId(id);
                codeTextView.setText(String.format(getResources().getString(R.string.codeValue), codeList.get(i-1).getCodeNo()));
                //文字居中
                codeTextView.setGravity(Gravity.START);
                //改变字体大小
                codeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                //设置单行
                codeTextView.setSingleLine();
                constraintLayout.addView(codeTextView);

                //添加numberView
                TextView numberTextView = new TextView(context);
                numberTextView.setId(id2);
                numberTextView.setText(String.format(getResources().getString(R.string.numberValue), codeList.get(i-1).getNumber()));
                numberTextView.setGravity(Gravity.END);

                numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                  numberTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                codeTextView.setSingleLine();
                constraintLayout.addView(numberTextView);

                //添加点击事件
                codeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Integer clickId=v.getId();
                        Log.d("clickId:", ""+v.getId());

                        final Intent intent = new Intent(MainActivity.this, ModifyActivity.class);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Code code=ExcelUtil.selectById(fileName,clickId/2+1L);
//                                    Code code=DBUtils.selectById(clickId/2+1L);
                                    intent.putExtra("codeData",code);
                                    startActivityForResult(intent, 0);
                                }
                            }).start();



                    }
                });

                numberTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Integer clickId=v.getId();
                        Log.d("clickId:", ""+v.getId());

                        final Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Code code=ExcelUtil.selectById(fileName,clickId/2L);
//                                  Code code=DBUtils.selectById(clickId/2L);
                                    intent.putExtra("codeData",code);
                                    startActivityForResult(intent, 0);
                                }
                            }).start();


                    }
                });


                //新建样式
                ConstraintSet set = new ConstraintSet();
                set.clone(constraintLayout);

                if (id == 1) {
                    set.connect(codeTextView.getId(), ConstraintSet.TOP, R.id.codeShow, ConstraintSet.BOTTOM);
                } else {
//                       set.connect(codeTextView.getId(), ConstraintSet.TOP, id - 2, ConstraintSet.BOTTOM);
                    set.connect(id - 2, ConstraintSet.TOP, id, ConstraintSet.BOTTOM);
                    set.connect(id, ConstraintSet.TOP, R.id.codeShow, ConstraintSet.BOTTOM);
                }
                set.connect(codeTextView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                if (id2 == 2) {
                    set.connect(numberTextView.getId(), ConstraintSet.TOP, R.id.numberShow, ConstraintSet.BOTTOM);
                } else {
//                       set.connect(numberTextView.getId(), ConstraintSet.TOP, id2 - 2, ConstraintSet.BOTTOM);
                    set.connect(id2 - 2, ConstraintSet.TOP, id2, ConstraintSet.BOTTOM);
                    set.connect(id2, ConstraintSet.TOP, R.id.numberShow, ConstraintSet.BOTTOM);
                }
                set.connect(numberTextView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                set.connect(codeTextView.getId(), ConstraintSet.END, numberTextView.getId(), ConstraintSet.START);
                set.connect(numberTextView.getId(), ConstraintSet.START, codeTextView.getId(), ConstraintSet.END);
                set.constrainWidth(codeTextView.getId(), 380);
                set.constrainWidth(numberTextView.getId(), 0);
                set.constrainHeight(codeTextView.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainHeight(numberTextView.getId(), ConstraintSet.WRAP_CONTENT);

                //应用布局样式
                set.applyTo(constraintLayout);
                //显示视图
                setContentView(constraintLayout);
            }

        }



        codeTextET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()>0){
                    code=s.toString();
                    if(StringUtils.checkString(code)){
                        //获得焦点
                        numberTextET.setVisibility(View.VISIBLE);
                        numberTextET.setFocusable(true);
                        numberTextET.setFocusableInTouchMode(true);
                        numberTextET.requestFocus();
                        //自动弹出键盘
                        InputMethodManager imm = (InputMethodManager) numberTextET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    };
                }else{
                    code=null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numberTextET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0){
                    number=Long.parseLong(s.toString());
                }else{
                    number=null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        insertBtn.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                isCanInsert=false;

                if (code==null) {
                    Toast.makeText(context, "条码不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(number == null){
                    Toast.makeText(context, "数量不能为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (productState==null){
                        Toast.makeText(context, "请选择产品状态", Toast.LENGTH_SHORT).show();
                    }else {

                        isCanInsert=true;
                        if (((RadioButton)findViewById(R.id.rb_4)).getText().toString().equals(productState)   ){

                            if ((lineNoET.getText().toString()).trim().length()==0){
                                Toast.makeText(context, "线位不能为空", Toast.LENGTH_SHORT).show();
                                isCanInsert=false;
                            }
                        }

                    }
                }
                if (isCanInsert) {
                    //判断是否是最大页数，是则直接写入数据，否则先跳转到最大页数再写入数据
                    if (!(CurrentPageIndexET.getText().toString().equals(TotalPageIndexET.getText().toString()))) {
                        System.out.println(pageIndex);
                        int index = Integer.parseInt(TotalPageIndexET.getText().toString());
                        cleanview(pageIndex);

                        List<Code> codeList = ExcelUtil.selectAllByPage(fileName, index);
                        addViewByPage(codeList);
                        CurrentPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex), index));
                        pageIndex = index;
                    }
                    //判断行数是否等于10，是的话删除所有条码

                    Log.d("行数：", "" + (n + latestRowNo) % 10);
                    if ((n + latestRowNo) % 10 == 0) {
                        if (n + latestRowNo != 0) {
                            for (int i = 1; i <= 10; i++) {
                                int line = (pageIndex - 1) * 10 + i;
                                constraintLayout.removeView(findViewById(2 * line - 1));
                                constraintLayout.removeView(findViewById(2 * line));


                            }
                            pageIndex++;
                        }

                        //setText参数使用Integer时是作为resId去string.xml文件找对应的资源
                        CurrentPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex), pageIndex));
                        TotalPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex), pageIndex));
                    }


                    //生成ViewID,latestRowNo是最新的行号
                    autoId_code = ViewCompat.generateViewId();
                    autoId2_number = ViewCompat.generateViewId();
                    int id = latestRowNo * 2 + autoId_code;
                    int id2 = latestRowNo * 2 + autoId2_number;
                    Log.d("新创建id：", "" + id);
                    Log.d("新创建id2：", "" + id2);


                    //添加codeView
                    TextView codeTextView = new TextView(context);

                    codeTextView.setId(id);
                    codeTextView.setText(String.format(getResources().getString(R.string.codeValue), code));
                    //文字居中
                    codeTextView.setGravity(Gravity.START);
                    //改变字体大小
                    codeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    //设置单行
                    codeTextView.setSingleLine();
                    constraintLayout.addView(codeTextView);


                    //添加numberView
                    TextView numberTextView = new TextView(context);
                    numberTextView.setId(id2);
                    numberTextView.setText(String.format(getResources().getString(R.string.numberValue), number));
                    numberTextView.setGravity(Gravity.END);

                    numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                  numberTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    codeTextView.setSingleLine();
                    constraintLayout.addView(numberTextView);

                    //添加点击事件
                    codeTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Integer clickId = v.getId();
                            Log.d("clickId:", "" + v.getId());

                            final Intent intent = new Intent(MainActivity.this, ModifyActivity.class);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Code code = ExcelUtil.selectById(fileName, clickId / 2 + 1L);
//                                    Code code=DBUtils.selectById(clickId/2+1L);
                                    intent.putExtra("rowNo",clickId / 2 + 1L);
                                    intent.putExtra("codeData", code);

                                    startActivityForResult(intent, 0);
                                }
                            }).start();


                        }
                    });

                    numberTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final Integer clickId = v.getId();
                            Log.d("clickId:", "" + v.getId());

                            final Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Code code = ExcelUtil.selectById(fileName, clickId / 2L);
//                                  Code code=DBUtils.selectById(clickId/2L);
                                    intent.putExtra("codeData", code);
                                    startActivityForResult(intent, 0);
                                }
                            }).start();


                        }
                    });
                    //新建样式
                    ConstraintSet set = new ConstraintSet();
                    set.clone(constraintLayout);

                    if (id % 20 == 1) {
                        set.connect(codeTextView.getId(), ConstraintSet.TOP, R.id.codeShow, ConstraintSet.BOTTOM);
                    } else {
//                       set.connect(codeTextView.getId(), ConstraintSet.TOP, id - 2, ConstraintSet.BOTTOM);
                        set.connect(id - 2, ConstraintSet.TOP, id, ConstraintSet.BOTTOM);
                        set.connect(id, ConstraintSet.TOP, R.id.codeShow, ConstraintSet.BOTTOM);
                    }
                    set.connect(codeTextView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);


                    if (id2 % 20 == 2) {
                        set.connect(numberTextView.getId(), ConstraintSet.TOP, R.id.numberShow, ConstraintSet.BOTTOM);
                    } else {
//                       set.connect(numberTextView.getId(), ConstraintSet.TOP, id2 - 2, ConstraintSet.BOTTOM);
                        set.connect(id2 - 2, ConstraintSet.TOP, id2, ConstraintSet.BOTTOM);
                        set.connect(id2, ConstraintSet.TOP, R.id.numberShow, ConstraintSet.BOTTOM);

                    }
                    set.connect(numberTextView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                    set.connect(codeTextView.getId(), ConstraintSet.END, numberTextView.getId(), ConstraintSet.START);
                    set.connect(numberTextView.getId(), ConstraintSet.START, codeTextView.getId(), ConstraintSet.END);
                    set.constrainWidth(codeTextView.getId(), 380);
                    set.constrainWidth(numberTextView.getId(), 0);
                    set.constrainHeight(codeTextView.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(numberTextView.getId(), ConstraintSet.WRAP_CONTENT);


                    //应用布局样式

                    set.applyTo(constraintLayout);

                    //显示视图
//                    Log.d("测试", "onClick: ");
                    setContentView(constraintLayout);

                    //添加到本地
                    EditText userET = findViewById(R.id.userText);
                    if (lineNoET.getText().length()==0){
                        lineNo=null;
                    }else{
                        lineNo = Integer.parseInt(lineNoET.getText().toString());
                    }

                    Code codeClass = new Code(((autoId_code) / 2 + 1L + LID), code, number, userET.getText().toString(), productState,lineNo);

                    //创建文件

                    File saveFile = new File(fileName);

                    String[] title = {"id", "条码", "数量", "用户", "产品状态","线位"};
                    ArrayList<Code> codeList = new ArrayList<Code>();
                    codeList.add(codeClass);
                    if (!saveFile.exists()) {
                        Log.d("Excel:", "文件不存在，新增");
                        ExcelUtil.initExcel(fileName, title);
                        ExcelUtil.writeObjListToExcel(codeList, fileName, context);
                    } else {
                        Log.d("Excel:", "文件存在，可以添加数据");
                        ExcelUtil.AddListToExcel(fileName, codeList);
                    }

                    //添加到数据库


//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            DBUtils.insert(codeClass);
//                        }
//                    }).start();
                    n++;
                    //显示已扫条码数
                    codeNum.setText(String.format(getResources().getString(R.string.codeNum), n + latestRowNo));
                    codeNum.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "添加成功", Toast.LENGTH_LONG).show();


                    //清空数据

                    codeTextET.setText("");
                    numberTextET.setText("");
                    lineNoET.setText("");

                    codeTextET.setVisibility(View.VISIBLE);
                    codeTextET.setFocusable(true);
                    codeTextET.setFocusableInTouchMode(true);
                    codeTextET.requestFocus();

                }
            }
        });



        //设置图片点击事件
        topPageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(pageIndex);
                int index=1;
                cleanview(pageIndex);

                List<Code> codeList=ExcelUtil.selectAllByPage(fileName,index);
                addViewByPage(codeList);
                CurrentPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex),index));
                pageIndex=index;
            }
        });

        bottomPageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(pageIndex);
                int index=Integer.parseInt(TotalPageIndexET.getText().toString());
                cleanview(pageIndex);

                List<Code> codeList=ExcelUtil.selectAllByPage(fileName,index);
                addViewByPage(codeList);
                CurrentPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex),index));
                pageIndex=index;
            }
        });

        lastPageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(pageIndex);
                int index=pageIndex-1;
                if (index>0){
                    cleanview(pageIndex);

                    List<Code> codeList=ExcelUtil.selectAllByPage(fileName,index);
                    addViewByPage(codeList);
                    CurrentPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex),index));
                    pageIndex=index;
                }

            }
        });

        nextPageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(pageIndex);
                int index=pageIndex+1;
                if (index<=Integer.parseInt(TotalPageIndexET.getText().toString())){
                    cleanview(pageIndex);

                    List<Code> codeList=ExcelUtil.selectAllByPage(fileName,index);
                    addViewByPage(codeList);
                    CurrentPageIndexET.setText(String.format(getResources().getString(R.string.pageIndex),index));
                    pageIndex=index;
                }

            }
        });

        //跳转
        pageJumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index=pageIndex;
                System.out.println(index);
                cleanview(index);

                int totalPage=(ExcelUtil.getTotalNumber(fileName)/10)+1;

                if (jumpIndex<=0){
                    jumpIndex=1;
                }else if(jumpIndex>totalPage){
                    jumpIndex=totalPage;
                }



                List<Code> codeList=ExcelUtil.selectAllByPage(fileName,jumpIndex);


                addViewByPage(codeList);

                pageIndex=jumpIndex;

            }
        });


        //导出按钮设置点击事件
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<Code> codeList=ExcelUtil.selectAll(fileName);
//                System.out.println(codeList);
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        String url="http://192.168.5.252:33118/api/excel";
                        File file=new File(fileName);
                        String msg=UploadUtils.uploadImage(file,url);
                        Looper.prepare();
                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });

    }
    //Activity创建或者从后台重新回到前台时被调用
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("生命周期：", "onStart called.");


    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("生命周期：", "onRestart called.");
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("生命周期：", "onResume called.");
    }

    //Activity窗口获得或失去焦点时被调用,在onResume之后或onPause之后
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "onWindowFocusChanged called.");
    }*/




    //Activity被覆盖到下面或者锁屏时被调用
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("生命周期：", "onPause called.");

        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
    }



    //退出当前Activity或者跳转到新Activity时被调用
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("生命周期：", "onStop called.");
    }

    //退出当前Activity时被调用,调用之后Activity就结束了
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("生命周期：", "onDestory called.");
    }



    private void addViewByPage(List<Code> codeList){
        for (int i = 0; i <codeList.size() ; i++) {
            Long tempid=codeList.get(i).getId()*2-1;
            Long tempid2=codeList.get(i).getId()*2;
            Integer id=Integer.parseInt(tempid.toString());
            Integer id2=Integer.parseInt(tempid2.toString());
            Log.d("id：", ""+id);
            Log.d("id2：", ""+id2);

            //添加codeView
            TextView codeTextView = new TextView(context);
            codeTextView.setId(id);
            codeTextView.setText(String.format(getResources().getString(R.string.codeValue), codeList.get(i).getCodeNo()));
            //文字居中
            codeTextView.setGravity(Gravity.START);
            //改变字体大小
            codeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            //设置单行
            codeTextView.setSingleLine();
            constraintLayout.addView(codeTextView);

            //添加numberView
            TextView numberTextView = new TextView(context);
            numberTextView.setId(id2);
            numberTextView.setText(String.format(getResources().getString(R.string.numberValue), codeList.get(i).getNumber()));
            numberTextView.setGravity(Gravity.END);

            numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                  numberTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            codeTextView.setSingleLine();
            constraintLayout.addView(numberTextView);

            //添加点击事件
            codeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Integer clickId=v.getId();
                    Log.d("clickId:", ""+v.getId());

                    final Intent intent = new Intent(MainActivity.this, ModifyActivity.class);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Code code=ExcelUtil.selectById(fileName,clickId/2+1L);
//                                    Code code=DBUtils.selectById(clickId/2+1L);
                            intent.putExtra("codeData",code);
                            startActivityForResult(intent, 0);
                        }
                    }).start();



                }
            });

            numberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Integer clickId=v.getId();
                    Log.d("clickId:", ""+v.getId());

                    final Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Code code=ExcelUtil.selectById(fileName,clickId/2L);
//                                  Code code=DBUtils.selectById(clickId/2L);
                            intent.putExtra("codeData",code);
                            startActivityForResult(intent, 0);
                        }
                    }).start();


                }
            });


            //新建样式
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);

            if (id%20 == 1) {
                set.connect(codeTextView.getId(), ConstraintSet.TOP, R.id.codeShow, ConstraintSet.BOTTOM);
            } else {
//                       set.connect(codeTextView.getId(), ConstraintSet.TOP, id - 2, ConstraintSet.BOTTOM);
                set.connect(id - 2, ConstraintSet.TOP, id, ConstraintSet.BOTTOM);
                set.connect(id, ConstraintSet.TOP, R.id.codeShow, ConstraintSet.BOTTOM);
            }
            set.connect(codeTextView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

            if (id2%20 == 2) {
                set.connect(numberTextView.getId(), ConstraintSet.TOP, R.id.numberShow, ConstraintSet.BOTTOM);
            } else {
//                       set.connect(numberTextView.getId(), ConstraintSet.TOP, id2 - 2, ConstraintSet.BOTTOM);
                set.connect(id2 - 2, ConstraintSet.TOP, id2, ConstraintSet.BOTTOM);
                set.connect(id2, ConstraintSet.TOP, R.id.numberShow, ConstraintSet.BOTTOM);
            }
            set.connect(numberTextView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

            set.connect(codeTextView.getId(), ConstraintSet.END, numberTextView.getId(), ConstraintSet.START);
            set.connect(numberTextView.getId(), ConstraintSet.START, codeTextView.getId(), ConstraintSet.END);
            set.constrainWidth(codeTextView.getId(), 380);
            set.constrainWidth(numberTextView.getId(), 0);
            set.constrainHeight(codeTextView.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(numberTextView.getId(), ConstraintSet.WRAP_CONTENT);

            //应用布局样式
            set.applyTo(constraintLayout);
            //显示视图
            setContentView(constraintLayout);


        }
    }

    //拦截返回键，改写方法使得其不执行finish（）方法，只退出到后台
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

//        Log.d("接收MainActivity参数", intent+"");
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                //ModifyActivity的请求码为0
                if( requestCode==0) {
//                    Bundle b = intent.getExtras(); //获取intent中的bundle
                    final Code codeDataM=(Code)intent.getSerializableExtra("codeDataM");
                    String codeM =codeDataM.getCodeNo();
                    Long numberM = codeDataM.getNumber();
                    Long id = codeDataM.getId();
                    System.out.println(codeDataM);

                    try {
                        if(ExcelUtil.updateExcel(fileName,codeDataM,context)==1) {
                            Integer lineNo = Integer.parseInt(codeDataM.getId().toString());
                            ((TextView) findViewById(lineNo * 2 - 1)).setText(codeM);
                            ((TextView) findViewById(lineNo * 2)).setText(String.format(getResources().getString(R.string.numberValue), numberM));

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
    }



    //清除条码view
    private void cleanview(int pageIndex){
        for (int i = 1; i <=10 ; i++) {
            int line=(pageIndex-1)*10+i;
            constraintLayout.removeView(findViewById(2*line-1));
            constraintLayout.removeView(findViewById(2*line));
        }
    }

    //单选框监听事件
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()){
            case R.id.rg_1:
                linePositionET.setVisibility(View.INVISIBLE);
                lineNoET.setVisibility(View.INVISIBLE);
                productStateRG2.setOnCheckedChangeListener(null);
                productStateRG2.clearCheck();
                productStateRG2.setOnCheckedChangeListener(this);
                productState=((RadioButton)findViewById(checkedId)).getText().toString();
                break;
            case R.id.rg_2:

                if (checkedId==R.id.rb_4){
                    linePositionET.setVisibility(View.VISIBLE);
                    lineNoET.setVisibility(View.VISIBLE);
                }else{
                    linePositionET.setVisibility(View.INVISIBLE);
                    lineNoET.setVisibility(View.INVISIBLE);
                }
                productStateRG1.setOnCheckedChangeListener(null);
                productStateRG1.clearCheck();
                productStateRG1.setOnCheckedChangeListener(this);
                productState=((RadioButton)findViewById(checkedId)).getText().toString();
                break;
        }

    }
}