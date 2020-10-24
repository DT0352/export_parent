package poi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author ZhiDong
 * <p>
 * 2020/10/20
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String a = "123";
        a.replace(a,"1");
        System.out.println(a);
    }
}
