package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Level;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Warning;
import org.junit.Assert;
import org.junit.Before;
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


    @Before
    public void cleanUpWarningTable() {
        jdbcTemplate.execute("DELETE FROM warning");
    }

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
        warning.setLevel(Level.CRITICAL);
        warningMapper.create(warning);
        Assert.assertTrue(warning.getId() > 0);
        deleteWarning(warning.getId());
    }


    private void insertWarning(long id, String text) {
        jdbcTemplate.update("INSERT INTO warning(id, msg, create_time, status, level) VALUES (?, ?, CURRENT_TIMESTAMP, 5, 1)", id, text);
    }

    private void deleteWarning(long id) {
        jdbcTemplate.update("DELETE FROM warning WHERE id = ? ", id);
    }

    @Test
    public void testGet() throws Exception {
        insertWarning(100000, "Test");

        Warning warning = warningMapper.get(100000);
        Assert.assertEquals("Test", warning.getMsg());

        deleteWarning(100000);
    }

    @Test
    public void testList() throws Exception {
        insertWarning(100000, "Test 100");
        insertWarning(100001, "Test 101");

        List<Warning> warningList = warningMapper.list(null, Status.ACTIVE);

        Assert.assertTrue(warningList.size() >= 2);

        deleteWarning(100000);
        deleteWarning(100001);
    }

    @Test
    public void testMark() throws Exception {
        insertWarning(100000, "Test");
        warningMapper.mark(100000, Status.DELETED);
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT status FROM warning WHERE id = ?", 100000);
        Assert.assertEquals(Status.DELETED.ordinal(), list.get(0).get("status"));
        deleteWarning(100000);
    }
}