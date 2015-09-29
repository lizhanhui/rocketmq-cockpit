package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.admin.api.MessageTrack;
import com.ndpmedia.rocketmq.cockpit.model.CockpitMessage;
import com.ndpmedia.rocketmq.cockpit.model.CockpitMessageFlow;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.CockpitMessageMapper;
import com.ndpmedia.rocketmq.cockpit.util.MessageTranslate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by robert.xu on 2015/4/7.
 */
@Controller
@RequestMapping(value = "/api/message")
public class CockpitMessageServiceController {

    private Logger logger = LoggerFactory.getLogger(CockpitMessageServiceController.class);

    @Autowired
    private CockpitMessageMapper cockpitMessageMapper;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CockpitMessage getMessageByID(@PathVariable("id") String id) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            MessageExt messageExt = defaultMQAdminExt.viewMessage(id);
            return MessageTranslate.translateFrom(messageExt);
        } catch (MQClientException e) {
            logger.warn("[CockpitMessageServiceController] try to get message by id failed." + e);
        } catch (InterruptedException e) {
            logger.warn("[CockpitMessageServiceController] try to get message by id failed." + e);
        } catch (RemotingException e) {
            logger.warn("[CockpitMessageServiceController] try to get message by id failed." + e);
        } catch (MQBrokerException e) {
            logger.warn("[CockpitMessageServiceController] try to get message by id failed." + e);
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public List<MessageTrack> getConsumerTypeByID(@PathVariable("id") String id) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        MessageExt messageExt = null;
        try {
            defaultMQAdminExt.start();
            messageExt = defaultMQAdminExt.viewMessage(id);
            return defaultMQAdminExt.messageTrackDetail(messageExt);
        } catch (Exception e) {
            logger.warn("[CockpitMessageServiceController] can not get message track by id" + id);
        } finally {
            defaultMQAdminExt.shutdown();
        }

        return null;
    }

    @RequestMapping(value = "/{topic}/{key}", method = RequestMethod.GET)
    @ResponseBody
    public List<CockpitMessage> getMessageByKey(@PathVariable("topic") String topic, @PathVariable("key") String key) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            QueryResult queryResult = defaultMQAdminExt.queryMessage(topic, key, 32, 0, Long.MAX_VALUE);
            List<CockpitMessage> result = new ArrayList<CockpitMessage>();
            for (MessageExt messageExt : queryResult.getMessageList()) {
                result.add(MessageTranslate.translateFrom(messageExt));
            }
            return result;
        } catch (MQClientException e) {
            logger.warn("[CockpitMessageServiceController] try to get message by key failed." + e);
        } catch (InterruptedException e) {
            logger.warn("[CockpitMessageServiceController] try to get message by key failed." + e);
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return null;
    }

    @RequestMapping(value = "/download/{msgId}", method = RequestMethod.GET)
    public void downloadMsg(@PathVariable("msgId") String msgId, HttpServletRequest request, HttpServletResponse response) {
        CockpitMessage cockpitMessage = this.getMessageByID(msgId);

        response.setContentType("application/x-download");
        String tempFileName = null;
        try {
            tempFileName = URLEncoder.encode(msgId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn("[CockpitMessageServiceController] try to get message body failed." + e);
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + tempFileName);
        byte[] buffer = cockpitMessage.getBody();

        try {
            response.getOutputStream().write(buffer, 0, buffer.length);
            response.getOutputStream().flush();
        } catch (IOException e) {
            logger.warn("[CockpitMessageServiceController] try to get message body failed." + e);
        }
    }

    @RequestMapping(value = "/flow/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<CockpitMessageFlow> getFlowByID(@PathVariable("id") String id){
        String tracerId = cockpitMessageMapper.tracerId(id);
        return cockpitMessageMapper.list(id, tracerId);
    }

    @RequestMapping(value = "/query/{topic}", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> getLinkedConsumerByTopic(@PathVariable("topic") String topic){
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            return defaultMQAdminExt.queryTopicConsumeByWho(topic).getGroupList();

        } catch (MQClientException e) {
            logger.warn("[CockpitMessageServiceController] try to get consumer connection failed." + e);
        } catch (InterruptedException e) {
            logger.warn("[CockpitMessageServiceController] try to get consumer connection failed." + e);
        } catch (RemotingException e) {
            logger.warn("[CockpitMessageServiceController] try to get consumer connection failed." + e);
        } catch (MQBrokerException e) {
            logger.warn("[CockpitMessageServiceController] try to get consumer connection failed." + e);
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return null;
    }

    @RequestMapping(value = "/resend/{consumerGroup}/{client}/{msgId}", method = RequestMethod.GET)
    @ResponseBody
    public String resendMsg(@PathVariable("consumerGroup") String consumerGroup, @PathVariable("client") String client,
                          @PathVariable("msgId") String msgId){
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        try {
            defaultMQAdminExt.start();
            return defaultMQAdminExt.consumeMessageDirectly(consumerGroup, client, msgId).toString();
        } catch (MQClientException e) {
            logger.warn("[CockpitMessageServiceController] try to resend message failed." + e);
        } catch (InterruptedException e) {
            logger.warn("[CockpitMessageServiceController] try to resend message failed." + e);
        } catch (RemotingException e) {
            logger.warn("[CockpitMessageServiceController] try to resend message failed." + e);
        } catch (MQBrokerException e) {
            logger.warn("[CockpitMessageServiceController] try to resend message failed." + e);
        } finally {
            defaultMQAdminExt.shutdown();
        }

        return " error ";
    }
}
