package cn.jwjg.jwpd.Utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.net.IDN;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jwjg.jwpd.entity.Code;

public class DBUtils {
    private static String IP = "192.168.5.250";
    private static String DBName = "ESJW1";

    private static String USER = "sa";
    private static String PWD = "Jw888888";

    private static Connection conn=null;
    private static Statement stmt=null;
    private static ResultSet rs=null;
    private static PreparedStatement pstmt = null ;
    private static SimpleDateFormat simpleDateFormat;




    /** 创建数据库对象 */

    //修改 private

    public static Connection getSQLConnection() {
        Connection con = null;
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            //加上 useunicode=true;characterEncoding=UTF-8 防止中文乱码


//            con = DriverManager.getConnection("jdbc:mysql://111.229.143.161:3306/TestConnection", "root", "Rr921102");
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + IP + ":1433/" + DBName + ";useunicode=true;characterEncoding=UTF-8", USER, PWD);


//            Looper.prepare();
//            Toast.makeText(context,"连接成功"+con,Toast.LENGTH_SHORT).show();
//            Looper.loop();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        } catch (SQLException e) {
            e.printStackTrace();
//            Looper.prepare();
//            Toast.makeText(context,"连接超时",Toast.LENGTH_LONG).show();
//            Looper.loop();

        }

        return con;
    }
    /** 关闭连接*/
    private static void closeAll(){
        try{
            if (rs!=null){
                rs.close();
            }
            if (stmt!=null){
                stmt.close();
            }
            if (conn!=null){
                conn.close();
            }
            System.out.println("连接正常关闭");
        }catch (Exception e){
            System.out.println(e);
        }

    }


    public static int insert(Code code){
        int i=0;
        String sql = "INSERT INTO dbo.TestPandian(id,条码,数量,用户) VALUES(?,?,?,?)" ;
        conn=getSQLConnection();
        try {
            if(conn!=null) {
                Log.d("insert", "数据库连接成功");
                pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1,code.getId());
                pstmt.setString(2, code.getCodeNo());
                pstmt.setLong(3, code.getNumber());
                pstmt.setString(4, code.getUser());
//                pstmt.setDate(4, new java.sql.Date(new Date().getTime()));

                i = pstmt.executeUpdate();

            }
        } catch (Exception e1){
            e1.printStackTrace();

        } finally{
            closeAll();
        }
        return  i;

    }

    public static int update(Code code){
        String sql = "UPDATE dbo.TestPandian SET 条码=?, 数量=?, 用户=?  where id=?" ;
        conn =getSQLConnection();
        int i=0;


        try {
            if(conn!=null) {
                Log.d("update", "数据库连接成功");
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, code.getCodeNo());
                pstmt.setLong(2, code.getNumber());
                pstmt.setString(3, code.getUser());
                pstmt.setLong(4, code.getId());
//            pstmt.setString(2,"d");
                i = pstmt.executeUpdate();
                Log.d("加载驱动", "更新成功");
            }
        } catch (Exception e1){
            e1.printStackTrace();
        } finally{
            closeAll();
        }
        return  i;
    }


    public static Code selectById(Long id){
        String sql = "SELECT * FROM dbo.TestPandian where id=?" ;
        conn =getSQLConnection();
        Code code=null;
        try {
            if(conn!=null) {
                Log.d("update", "数据库连接成功");
                pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, id);
                rs=pstmt.executeQuery();


                while (rs.next()) {
                    String codeNo = rs.getString("条码");
                    Long number=rs.getLong("数量");
                    String user=rs.getString("用户");
                    code=new Code(id,codeNo,number,user);


                }
            }
        } catch (Exception e1){
            System.out.println(e1);
        } finally{
            closeAll();
        }
        return code;
    }
}
