package edu.usc.secondkill.common.dynamicquery;

import edu.usc.secondkill.common.entities.Seckill;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamicQueryImplTest {

    @Autowired
    DynamicQueryImpl dq ;

    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void save() {
        dq.save(new Seckill(1L, "iphone", 100,
                new Timestamp(new Date().getTime()),
                new Timestamp(new Date().getTime()),
                new Timestamp(new Date().getTime()), 1));
    }

    @Test
    public void update() {

    }

    @Test
    public void delete() {
    }

    @Test
    public void delete1() {
    }

    @Test
    public void nativeQueryList() {
    }

    @Test
    public void nativeQueryListMap() {
    }

    @Test
    public void nativeQueryListModel() {
    }

    @Test
    public void nativeQueryObject() {
    }

    @Test
    public void nativeQueryArray() {
    }

    @Test
    public void nativeExecuteUpdate() {
        String nativeSql = "SELECT number FROM seckill WHERE seckill_id=1000";

        Object count = dq.nativeQueryObject(nativeSql);

        System.out.println(count);
    }

    @Test
    public void getLogger() {
    }

    @Test
    public void getEm() {
    }

}