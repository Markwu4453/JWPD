package cn.jwjg.jwpd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.jwjg.jwpd.entity.Code;

public class ModifyActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{


    private String codeM;
    private Long numberM;
    private String userM;
    private String productStateM;
    private Integer lineNoM;

    private EditText codeETM;
    private EditText numberETM;
    private EditText userETM;
    private RadioGroup productStateRGM1;
    private RadioGroup productStateRGM2;
    private EditText lineNoETM;
    private TextView linePositionM;

    //拦截返回键，先执行setResult()方法再手动执行finish（）方法
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK){
//           setResult(RESULT_OK);
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

//        Bundle bundle=getIntent().getExtras();
//        if (bundle!=null) {
//            id=bundle.getInt("id");
//            number= bundle.getLong("number");
//            code=bundle.getString("code");
//        }
        Intent intent=getIntent();

        Code codeClass=(Code)getIntent().getSerializableExtra("codeData");
        codeM=codeClass.getCodeNo();
        numberM=codeClass.getNumber();
        userM=codeClass.getUser();
        productStateM=codeClass.getProductState();
        lineNoM=codeClass.getLineNo();
        Long rowNo=getIntent().getLongExtra("rowNo",0);
        System.out.println(lineNoM);
        //注册组件
        codeETM=findViewById(R.id.codeTextM);
        numberETM=findViewById(R.id.numberTextM);
        userETM=findViewById(R.id.userTextM);
        productStateRGM1=findViewById(R.id.rgM_1);
        productStateRGM2=findViewById(R.id.rgM_2);
        lineNoETM=findViewById(R.id.lineNoM);
        linePositionM=findViewById(R.id.linePositionM);
        //给输入框赋值

        codeETM.setText(codeClass.getCodeNo());
        numberETM.setText(String.format(getResources().getString(R.string.numberValue),codeClass.getNumber()));
        userETM.setText(codeClass.getUser());
        if (lineNoM==null){
            lineNoETM.setText("");
        }else {
            lineNoETM.setText(String.format(getResources().getString(R.string.numberValue), codeClass.getLineNo()));
        }
        switch (productStateM){
            case "半成品" :
                productStateRGM1.check(R.id.rbM_2);
                break;
            case "正常品" :
                productStateRGM1.check(R.id.rbM_1);
                break;
            case "黄卡" :
                productStateRGM2.check(R.id.rbM_3);
                break;
            case "待车品":
                productStateRGM2.check(R.id.rbM_4);
                lineNoETM.setVisibility(View.VISIBLE);
                linePositionM.setVisibility(View.VISIBLE);
        }

        //监测输入框数值变化
        codeETM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()>0){
                    codeM=s.toString();
                }else{
                    codeM=null;
                }
                System.out.println(codeM);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numberETM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0){
                    numberM=Long.parseLong(s.toString());
                }else{
                    numberM=null;
                }
                System.out.println(numberM);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userETM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0){
                    userM=s.toString();
                }else{
                    userM=null;
                }
                System.out.println(numberM);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //获得单选框的值
        productStateRGM1.setOnCheckedChangeListener(this);
        productStateRGM2.setOnCheckedChangeListener(this);



        lineNoETM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length()==0){
                        lineNoM=null;
                    }else{
                        lineNoM=Integer.parseInt(s.toString());
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button modifyButton=findViewById(R.id.buttonM);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String codeM=code;
//                Long numberM=number;
                Intent intent=getIntent();
//                intent.getExtras().putString("code",codeM);
                Code codeClass=(Code)getIntent().getSerializableExtra("codeData");
                Code codeDataM;
                if (productStateM.equals("待车品")) {
                     codeDataM= new Code(codeClass.getId(), codeM, numberM, userM, productStateM, lineNoM);
                }else{
                    codeDataM= new Code(codeClass.getId(), codeM, numberM, userM, productStateM, null);
                }
                intent.putExtra("codeDataM",codeDataM);
//                bundleM.putString("code",code);
//                bundleM.putLong("number",number);
//                bundleM.putInt("id",id);
//                bundleM.putString("type","modify");
//                intent.putExtras(bundleM);

//                intent.getExtras().putLong("number",numberM);
                setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                finish();
            }
        });


//        Button deleteButton=findViewById(R.id.buttonD);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=getIntent();
//                Bundle bundleM =new Bundle();
//
//                bundleM.putLong("id",codeClass.getId());
//
//                intent.putExtras(bundleM);
//
//
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()){
            case R.id.rgM_1:

                linePositionM.setVisibility(View.INVISIBLE);

                lineNoETM.setVisibility(View.INVISIBLE);
                productStateRGM2.setOnCheckedChangeListener(null);
                productStateRGM2.clearCheck();
                productStateRGM2.setOnCheckedChangeListener(this);
                productStateM=((RadioButton)findViewById(checkedId)).getText().toString();
                break;
            case R.id.rgM_2:

                if (checkedId==R.id.rbM_4){
                    linePositionM.setVisibility(View.VISIBLE);
                    lineNoETM.setVisibility(View.VISIBLE);
                }else{

                    linePositionM.setVisibility(View.INVISIBLE);
                    lineNoETM.setVisibility(View.INVISIBLE);
                }
                productStateRGM1.setOnCheckedChangeListener(null);
                productStateRGM1.clearCheck();
                productStateRGM1.setOnCheckedChangeListener(this);
                productStateM=((RadioButton)findViewById(checkedId)).getText().toString();
                break;
        }
    }
}

