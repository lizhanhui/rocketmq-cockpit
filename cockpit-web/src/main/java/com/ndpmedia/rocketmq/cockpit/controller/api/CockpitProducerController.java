package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by robert.xu on 2015/4/7.
 */
@Controller
@RequestMapping(value = "/api/producer")
public class CockpitProducerController {

    private Logger logger = LoggerFactory.getLogger(CockpitProducerController.class);

    @RequestMapping(value = "/{producerGroup}/{topic}/{tag}/{key}/{body}", method = RequestMethod.GET)
    @ResponseBody
    public String sendMessage(@PathVariable("producerGroup") String producerGroup, @PathVariable("topic") String
            topic, @PathVariable("tag") String tag, @PathVariable("body") String body, @PathVariable("key") String k){
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);

        try {
            producer.start();
            Message msg = new Message(topic,// topic
                    tag,// tag
                    (body).getBytes()// body
            );

            //Unique key may be used to query message using command line and web console.
            msg.setKeys(k);

            SendResult sendResult = producer.send(msg);
            return sendResult.toString();
        }
        catch (MQClientException | InterruptedException | MQBrokerException | RemotingException e) {
            logger.warn("[CockpitProducerController]try to send message failed." + e);
        } finally {
            producer.shutdown();
        }

        return null;
    }
}
