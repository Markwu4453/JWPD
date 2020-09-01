package cn.jwjg.jwpd.Utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jxl.common.Logger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadUtils {
    private static final String BOUNDARYSTR = "--------aifudao7816510d1hq";
    private static final String END = "\r\n";
    private static final String LAST = "--";
    /**
     * @param path 附件本地地址
     * @param url  附件存储的服务器地址
     */
    public static  void ShangChuanClass(String path, String url) {
        try {
            URL Url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setRequestMethod("POST");
            //必须为post connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-type", "multipart/form-data;boundary=" + BOUNDARYSTR);
            //固定格式
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            StringBuffer sb = new StringBuffer();
            /**
             * 写入文本数据
             * */
            sb.append(LAST + BOUNDARYSTR + END);
            sb.append("Content-Disposition: form-data; name=\"data\"" + END + END);
            sb.append(path + END);
            /**
             * 循环写入文件
             *
             **/
            String[] split = path.split("\\.");
            String s = split[1];
            Log.i("weicypath","weikai"+s);

            sb.append(LAST + BOUNDARYSTR + END);
            sb.append("Content-Disposition:form-data;Content-Type:application/octet-stream;name=\"file\";");
            //判断传入的格式
            if (s.equals("jpg")){
                sb.append("filename=\""+"map_image."+s+"\""+END+END);
                Log.i("weicypath","jpg");
            }else {
                sb.append("filename=\""+"map_image."+s+"\""+END+END);
                Log.i("weicypath","mp4");
            }
            // sb.append("filename=\""+"map_image.jpg"+"\""+END+END);
            dos.write(sb.toString().getBytes("utf-8"));
            FileInputStream fis = new FileInputStream(path);
            if (fis != null) {
                byte[] b = new byte[1024];
                int len;
                while ((len = fis.read(b)) != -1) {
                    dos.write(b, 0, len);
                }
                dos.write(END.getBytes());
            }
            dos.write((LAST + BOUNDARYSTR + LAST + END).getBytes());
            dos.flush();
            sb = new StringBuffer();
            if (connection.getResponseCode() == 200) {
                //请求成功
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                Log.i("weicypath", "成功");
            } else {
                Log.i("weicypath", "失败");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

















    public static void getSyn(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)//访问连接
                            .get()
                            .build();
                    Call call =client.newCall(request);
                    Response response=call.execute();
                    if (response.isSuccessful()){
                        System.out.println(response.body().string());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static  void uploadFile(String url, File file){
        OkHttpClient client=new OkHttpClient().newBuilder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .build();;
        RequestBody body=RequestBody.create(MediaType.get("application/xml"),file);
        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response rs=client.newCall(request).execute();
            Log.i("res:", rs.message());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call,  IOException e) {
//                Log.i("upload:" ,"onFailure:"+e.getStackTrace());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                //非主线程
//                if (response.isSuccessful()) {
//                    String str = response.body().string();
//                    Log.i("res:", response.message() + " , body " + str);
//
//                } else {
//                    Log.i("res:" ,response.message() + " error : body " + response.body().string());
//                }
//            }
//        });

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String uploadImage(File file, String RequestURL) {
        String result = "error";
        String resMsg="";
        String BOUNDARY = UUID.randomUUID().toString();//边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";//内容类型.
        System.out.println(file.getName());
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(50000);
            conn.setConnectTimeout(50000);
            conn.setDoInput(true);//允许输入流
            conn.setDoOutput(true);//允许输出流
            conn.setUseCaches(false);//不允许使用缓存
            conn.setRequestMethod("POST");//请求方式
            conn.setRequestProperty("Charset", "UTF-8");//设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setChunkedStreamingMode(0);
            conn.connect();

            if (file != null) {
                //当文件不为空，把文件包装并且上传

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.write((PREFIX + BOUNDARY + LINE_END).getBytes());
                dos.write(("Content-Disposition: form-data; " + "name=\"inputName\";filename=\"" + file.getName() + "\"" + LINE_END).getBytes());
                dos.writeBytes(LINE_END);

                FileInputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());

                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /*
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if (res == 200) {

                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
//                        result=new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        result=line;

                    }


                    resMsg=result.split(",")[0]+"\"";
                    System.out.println(resMsg);
                    String filepathnow=file.getPath();
//                    String newfilename=filepathnow.substring(0,filepathnow.lastIndexOf("."))+"-已上传.log";
//                    file.renameTo(new File(newfilename) );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resMsg;
    }
}

