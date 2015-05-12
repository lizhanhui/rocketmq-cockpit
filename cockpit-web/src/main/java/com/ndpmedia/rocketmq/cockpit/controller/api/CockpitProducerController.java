package com.ndpmedia.rocketmq.cockpit.controller.api;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by robert.xu on 2015/4/7.
 */
@Controller
@RequestMapping(value = "/api/producer")
public class CockpitProducerController {

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
        catch (MQClientException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (RemotingException e) {
            e.printStackTrace();
        }
        catch (MQBrokerException e) {
            e.printStackTrace();
        }finally {
            producer.shutdown();
        }

        return null;
    }
}
