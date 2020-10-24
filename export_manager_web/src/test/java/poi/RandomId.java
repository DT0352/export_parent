package poi;

import com.itheima.domain.system.Dept;

import java.util.UUID;

/**
 * @author ZhiDong
 * <p>
 * 2020/10/17
 */
public class RandomId {
    public static String randomId(){
        return UUID.randomUUID().toString().substring(0, 4) + UUID.randomUUID().toString().substring(4,8);
    }

    public static void main(String[] args) {
        Dept dept = new Dept();
        dept = null;
        Dept dept1 = new Dept();
        dept1.setId("123");
        dept.setParent(dept1);
        System.out.println((dept.getParent().getId() != null ? dept.getParent().getId() : "") + "111");
        String a = new String("123");
    }
}
