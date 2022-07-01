import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.reggie.ReggieApplicaion;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest(classes = ReggieApplicaion.class)
public class test01 {
    @Resource
    private EmployeeMapper employeeMapper;
    @Test
    public void test01(){

//        Employee employee = employeeMapper.selectById(1L);
//        System.out.println("employee = " + employee);
//        UpdateWrapper<Employee> wrapper = new UpdateWrapper<>();
        Employee employee = new Employee();
        employee.setUsername("zhangsan");
        employee.setName("张三");
        employee.setPassword("123123");
        employee.setPhone("17860399525");
        employee.setCreateUser(1L);
        employee.setUpdateUser(1L);
        employee.setSex("1");
        employee.setIdNumber("123123123123");
        employee.setCreateTime(null);
        employee.setUpdateTime(null);

        int count = employeeMapper.insert(employee);
        System.out.println("count = " + count);
    }
}
