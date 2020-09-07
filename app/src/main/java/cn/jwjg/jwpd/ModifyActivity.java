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

import cn.jwjg.jwpd.entity.Code;

public class ModifyActivity extends AppCompatActivity {


    private String codeM;
    private Long numberM;
    private String userM;
    private String productStateM;

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
        Code codeClass=(Code)getIntent().getSerializableExtra("codeData");
        codeM=codeClass.getCodeNo();
        numberM=codeClass.getNumber();
        userM=codeClass.getUser();
        productStateM=codeClass.getProductState();
        final EditText codeETM=findViewById(R.id.codeTextM);
        final EditText numberETM=findViewById(R.id.numberTextM);
        final EditText userETM=findViewById(R.id.userTextM);
        final RadioGroup productStateRG=findViewById(R.id.rgM_1);
        //给输入框赋值

        codeETM.setText(codeClass.getCodeNo());
        numberETM.setText(String.format(getResources().getString(R.string.numberValue),codeClass.getNumber()));
        userETM.setText(codeClass.getUser());
        switch (productStateM){
            case "半成品" :
                productStateRG.check(R.id.rbM_2);
                break;
            case "正常品" :
                productStateRG.check(R.id.rbM_1);
                break;
            case "黄卡" :
                productStateRG.check(R.id.rbM_3);
                break;
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

        productStateRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println( ( (RadioButton)( findViewById(checkedId) ) ).getText() );
                productStateM=( (RadioButton)( findViewById(checkedId) ) ).getText().toString();
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
                Code codeDataM =new Code(codeClass.getId(),codeM,numberM,userM,productStateM);
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
//                bundleM.putString("code",code);
//                bundleM.putLong("number",number);
//                bundleM.putInt("id",id);
//                bundleM.putString("type","delete");
//                intent.putExtras(bundleM);
//
//
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
    }
}

