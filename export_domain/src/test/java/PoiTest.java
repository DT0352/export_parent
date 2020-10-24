import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.sound.midi.SoundbankResource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhiDong
 * <p>
 * 2020/10/17
 */
public class PoiTest {
    public static void main(String[] args) throws IOException, InvalidFormatException {
//        创建工作薄
        Workbook workbook = new XSSFWorkbook(new File("C:\\Users\\ThinkPad\\Desktop\\feiq\\Recv Files\\资料(2)\\3-货物导入模板\\上传货物模板.xlsx"));
        Sheet sheet = workbook.getSheetAt(0);
        List<ContractProduct> list = new ArrayList<>(sheet.getLastRowNum());
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);
            Object[] productDesc = new Object[9];
            for (int j = 1; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                productDesc[j-1] = getCellValue(cell);
            }
            ContractProduct contractProduct = new ContractProduct(productDesc);
            list.add(contractProduct);
            System.out.println();
        }
        for (ContractProduct contractProduct : list) {
            System.out.println(contractProduct);
        }
        workbook.close();
    }
    public static Object getCellValue(Cell cell) {
        Object obj = null;
        CellType cellType = cell.getCellType(); //获取单元格数据类型
        switch (cellType) {
            case STRING: {
                obj = cell.getStringCellValue();//字符串
                break;
            }
            //excel默认将日期也理解为数字
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    obj = cell.getDateCellValue();//日期
                } else {
                    obj = cell.getNumericCellValue(); // 数字
                }
                break;
            }
            case BOOLEAN: {
                obj = cell.getBooleanCellValue(); // 布尔
                break;
            }
            default: {
                break;
            }
        }
        return obj;
    }
}
