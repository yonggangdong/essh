package test.eryansky.dao;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eryansky.common.utils.reflection.MyBeanUtils;
import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.eryansky.entity.base.Resource;
import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.eryansky.common.model.User;
import com.eryansky.common.orm.jdbc.JdbcDao;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.service.base.RoleManager;

/**
 * Dao单元测试.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-4-16 下午8:12:40 
 * @version 1.0
 */
public class DaoTest {
	
	@SuppressWarnings("unused")
    private static Properties pro;
	
	private static JdbcDao jdbcDao;
	
	private static RoleManager roleManager;
	
	@BeforeClass
	public static void init() throws Exception{
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
		jdbcDao = (JdbcDao)context.getBean("jdbcDao");
		roleManager = (RoleManager)context.getBean("roleManager");
		pro = new PropertiesLoader("/appconfig.properties").getProperties();
	}
	
	
	@Test
    public void update(){
		try {
			String sql = "update T_BASE_ROLE set name = ? where id = ?";
			Object[] obj = new Object[]{"11",1};
			int count = 0;
			count = jdbcDao.update(sql, obj);
			System.out.println(count);

//		    roleManager.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    @Test
    public void page(){
//        try {
//            String sql = "select * from T_BASE_RESOURCE";
//            List<Map<String,Object>> list1 = jdbcDao.queryForList(sql, 1, 5);
//            for(Map<String,Object> map:list1){
//                System.out.println(map);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
