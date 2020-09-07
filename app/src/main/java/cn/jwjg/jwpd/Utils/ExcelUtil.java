package cn.jwjg.jwpd.Utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import cn.jwjg.jwpd.entity.Code;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    private static WritableFont arial14font = null;

    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Excel
     *
     * @param fileName 导出excel存放的地址（目录）
     * @param colName excel中包含的列名（可以有多个）
     */
    public static void initExcel(String fileName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            //设置表格的名字
            WritableSheet sheet = workbook.createSheet("盘点数据", 0);

            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, fileName, arial14format));
            for (int col = 0; col < colName.length; col++  ) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            //设置行高
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<Code> objList, String fileName, Context c) {

        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writebook.getSheet(0);

                for (int j = 0; j < objList.size(); j++  ) {
                    Code code = (Code) (objList.get(j));
                    List<String> list = new ArrayList<>();
                    list.add(code.getId().toString());
                    list.add(code.getCodeNo());
                    list.add(code.getNumber().toString());
                    list.add(code.getUser());
                    list.add(code.getProductState());

//                    list.add(storageMessageEx.getStorage_man());
//                    list.add(storageMessageEx.getStorageDate().toString());
//                    list.add(storageMessageEx.getCreateTime().toString());
//                    list.add(storageMessageEx.getOperatorAndTimes().toString());
//                    Toast.makeText(c, list+"", Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < list.size(); i++  ) {
                        sheet.addCell(new Label(i, j +  1, list.get(i), arial12format));
                        if (list.get(i).length() <= 4) {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length()  + 8);
                        } else {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() +  5);
                        }
                    }
                    //设置行高
                    sheet.setRowView(j  + 1, 350);
                }

                writebook.write();
//                Toast.makeText(c, "导出Excel成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }



    public static void AddListToExcel(String filePath,List<Code> codeList){
        System.out.println(filePath);
        format();
        Workbook wb=null;
        WritableWorkbook workbook=null;
        File file=null;
        File fileM=null;
        Log.d("AddListToExcel:codeList", "  "+codeList.get(0));
        String filePathM=filePath.replace("盘点数据","盘点数据临时修改");
        try {
            file=new File(filePath);
            wb = Workbook.getWorkbook(file); // 获得原始文档
            Log.d("AddListToExcel:workbook", "  "+wb);
            fileM=new File(filePathM);
            workbook = Workbook.createWorkbook(fileM,wb);
            WritableSheet sheet = workbook.getSheet(0);
//            workbook.removeSheet(2); // 移除多余的标签页
//            workbook.removeSheet(3);
            int startRowNo=Integer.parseInt(codeList.get(0).getId().toString());
            for (int j = startRowNo-1; j <= startRowNo-1; j++  ) {
                Code code = (Code) (codeList.get(j-startRowNo+1));
                List<String> list = new ArrayList<>();
                list.add(code.getId().toString());
                list.add(code.getCodeNo());
                list.add(code.getNumber().toString());
                list.add(code.getUser());
                list.add(code.getProductState());

//                    list.add(storageMessageEx.getStorage_man());
//                    list.add(storageMessageEx.getStorageDate().toString());
//                    list.add(storageMessageEx.getCreateTime().toString());
//                    list.add(storageMessageEx.getOperatorAndTimes().toString());
//                    Toast.makeText(c, list+"", Toast.LENGTH_SHORT).show();

                for (int i = 0; i < list.size(); i++  ) {
                    sheet.addCell(new Label(i, j +  1, list.get(i), arial12format));
                    if (list.get(i).length() <= 4) {
                        //设置列宽
                        sheet.setColumnView(i, list.get(i).length()  + 8);
                    } else {
                        //设置列宽
                        sheet.setColumnView(i, list.get(i).length() +  5);
                    }
                }
                //设置行高
                sheet.setRowView(j  + 1, 350);
            }

            workbook.write();
            workbook.close();
            FileUtils.deleteFolder(file);
            fileM.renameTo(file);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }


    public  static int updateExcel(String fileName,Code code,Context context){
        int flag=0;
        format();
        if (code != null ) {
            WritableWorkbook writebook = null;
            File file=new File(fileName);
            try {
                if (code.getCodeNo()==null || code.getNumber()==null || code.getUser()==null){
                    throw new NullPointerException("条码和数量不能为空，修改失败");
                }

                Workbook workbook = Workbook.getWorkbook(file);
                writebook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writebook.getSheet(0);
                List<String> list=new ArrayList<>();

                list.add(code.getId().toString());
                list.add(code.getCodeNo());
                list.add(code.getNumber().toString());
                list.add(code.getUser());
                list.add(code.getProductState());
                Log.d("ExcelUtil id", ""+code.getId());
                for (int i = 0; i <list.size() ; i++) {
                    //adCell方法是会覆盖原有数据的
                    sheet.addCell(new Label(i, Integer.parseInt(list.get(0)), list.get(i), arial12format));
//                    WritableCell cell=sheet.getWritableCell(i,Integer.parseInt(list.get(0))+1);
//                    if(cell.getType() == CellType.LABEL){
//                        Label l = (Label)cell;
//                        l.setString(list.get(i));//对单元格里面的内容进行设置
//                    }
                }


                writebook.write();
                writebook.close();
                flag=1;
            }catch (Exception e){
                if (e instanceof  NullPointerException){

                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

                }else {
                    e.printStackTrace();
                }

            }
        }
        return flag;
    }

    public static Code selectById(String fileName,Long id){
        Code code=null;
        WritableWorkbook writebook = null;
        File file=new File(fileName);

        try {
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet=workbook.getSheet(0);

            int Rows=sheet.getRows()-1;
            int Columns=sheet.getColumns();
            List<String> list=new ArrayList<>();
            for (int j = 1; j <= Rows; j++) {
                Cell idCell =sheet.getCell(0,j);
                Log.d("idcell:", ""+idCell.getContents());
                Log.d("id", ""+id);
                if (idCell.getContents().equals(id.toString())){
                    for (int i = 0; i <Columns ; i++) {
                        //取得ID列的单元格
                        Cell cell =sheet.getCell(i,j);
                        list.add(cell.getContents());

                    }
                }
                else {
//                    Log.d("selectById", "第"+(j+1)+"行没有找到id");
                }

            }

            code=new Code();
            code.setId(Long.parseLong(list.get(0)));
            code.setCodeNo(list.get(1));
            code.setNumber(Long.parseLong(list.get(2)));
            code.setUser(list.get(3));
            code.setProductState(list.get(4));

//            Log.d("ExcelUtil id", ""+code.getId());




        }catch (Exception e){
            e.printStackTrace();
        }
        return code;
    }


    public static List<Code> selectAll(String fileName){
        Code code=null;
        WritableWorkbook writebook = null;
        File file=new File(fileName);
        List<Code> codeList=null;

        try {
            codeList=new ArrayList<Code>();
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet=workbook.getSheet(0);

            int Rows=sheet.getRows()-1;
            int Columns=sheet.getColumns();

            for (int j = 1; j <= Rows; j++) {
                List<String> list=new ArrayList<>();

                    for (int i = 0; i <Columns ; i++) {
                        Cell cell =sheet.getCell(i,j);
                        list.add(cell.getContents());

                    }
                code=new Code();
                code.setId(Long.parseLong(list.get(0)));
                code.setCodeNo(list.get(1));
                code.setNumber(Long.parseLong(list.get(2)));
                code.setUser(list.get(3));
                code.setProductState(list.get(4));

                codeList.add(code);

            }



//            Log.d("ExcelUtil id", ""+code.getId());




        }catch (Exception e){
            e.printStackTrace();
        }
        return codeList;
    }

    //根据页码查询数据
    public static List<Code> selectAllByPage(String fileName,Integer pageIndex){
        Code code=null;
        WritableWorkbook writebook = null;
        File file=new File(fileName);
        List<Code> codeList=null;
        int startRow;
        int endRow;

        int sum=getTotalNumber(fileName);
        if (sum>=pageIndex*10){
            startRow=2+(pageIndex-1)*10;
            endRow=11+(pageIndex-1)*10;
        }else{
            startRow=2+(pageIndex-1)*10;
            endRow=sum+1;
        }

        try {
            codeList=new ArrayList<Code>();
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet=workbook.getSheet(0);


//            int Rows=sheet.getRows()-1;
            int Columns=sheet.getColumns();

            for (int j = startRow; j <= endRow; j++) {
                List<String> list=new ArrayList<>();

                for (int i = 0; i <Columns ; i++) {
                    Cell cell =sheet.getCell(i,j-1);
                    list.add(cell.getContents());

                }
                code=new Code();
                code.setId(Long.parseLong(list.get(0)));
                code.setCodeNo(list.get(1));
                code.setNumber(Long.parseLong(list.get(2)));
                code.setUser(list.get(3));
                code.setProductState(list.get(4));

                codeList.add(code);

            }



//            Log.d("ExcelUtil id", ""+code.getId());




        }catch (Exception e){
            e.printStackTrace();
        }
        return codeList;

    }

    //查询条码数
    public static int getTotalNumber(String fileName){
        File file=new File(fileName);
        Workbook workbook = null;
        try {
            workbook = Workbook.getWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } catch (BiffException e) {
            e.printStackTrace();
            return 0;
        }
        Sheet sheet=workbook.getSheet(0);
        int Rows=sheet.getRows();
        return Rows-1;
    }
}
