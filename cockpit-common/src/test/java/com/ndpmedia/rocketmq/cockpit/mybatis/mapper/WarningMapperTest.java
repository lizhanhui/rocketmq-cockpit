package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Warning;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class WarningMapperTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WarningMapper warningMapper;


    @Test
    public void testSetUp() {
        Assert.assertNotNull(jdbcTemplate);
        Assert.assertNotNull(warningMapper);
    }

    @Test
    public void testCreate() throws Exception {
        Warning warning = new Warning();
        warning.setMsg("Test Warning");
        warning.setStatus(Status.ACTIVE);
        warning.setCreateTime(new Date());
        warningMapper.create(warning);
        Assert.assertTrue(warning.getId() > 0);
        deleteWarning(warning.getId());
    }


    private void insertWarning(long id, String text) {
        jdbcTemplate.update("INSERT INTO warning(id, msg, create_time, status) VALUES (?, ?, CURRENT_TIMESTAMP, 5)", id, text);
    }

    private void deleteWarning(long id) {
        jdbcTemplate.update("DELETE FROM warning WHERE id = ? ", id);
    }

    @Test
    public void testGet() throws Exception {
        insertWarning(100, "Test");

        Warning warning = warningMapper.get(100);
        Assert.assertEquals("Test", warning.getMsg());

        deleteWarning(100);
    }

    @Test
    public void testList() throws Exception {
        insertWarning(100, "Test 100");
        insertWarning(101, "Test 101");

        List<Warning> warningList = warningMapper.list(Status.ACTIVE);

        Assert.assertEquals(2, warningList.size());

        deleteWarning(100);
        deleteWarning(101);
    }

    @Test
    public void testMark() throws Exception {
        insertWarning(100, "Test");
        warningMapper.mark(100, Status.DELETED);
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT status FROM warning WHERE id = ?", 100);
        Assert.assertEquals(Status.DELETED.ordinal(), list.get(0).get("status"));
        deleteWarning(100);
    }
}