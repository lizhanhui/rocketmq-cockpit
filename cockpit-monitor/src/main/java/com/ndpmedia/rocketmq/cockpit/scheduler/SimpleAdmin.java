package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.srvutil.ServerUtil;
import com.alibaba.rocketmq.tools.command.SubCommand;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.DownTopicCommand;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.SyncConsumerGroupCommand;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.SyncTopicCommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 2015/5/29.
 */
public class SimpleAdmin {
    private static List<SubCommand> cmdList = new ArrayList<>();

    static{
        cmdList.add(new SyncTopicCommand());
        cmdList.add(new SyncConsumerGroupCommand());
        cmdList.add(new DownTopicCommand());
    }

    public static void main(String[] args){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:applicationContextCommon.xml");
        main0(args);
    }

    public static void main0(String[] args){
        switch (args.length){
            case 0 : printHelp(); ;break;
            case 2 : ;break;
            case 1 :
                SubCommand cmd = findSubCommand(args[0]);
                if (null != cmd){
                    // 将main中的args转化为子命令的args（去除第一个参数）
                    String[] subargs = parseSubArgs(args);

                    // 解析命令行
                    Options options = ServerUtil.buildCommandlineOptions(new Options());
                    final CommandLine commandLine =
                            ServerUtil.parseCmdLine("mqadmin " + cmd.commandName(), subargs,
                                    cmd.buildCommandlineOptions(options), new PosixParser());
//                    if (null == commandLine) {
//                        System.out.println("nothing input ?l");
//                        System.exit(-1);
//                        return;
//                    }

                    if (null != commandLine && commandLine.hasOption('n')) {
                        String namesrvAddr = commandLine.getOptionValue('n');
                        System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, namesrvAddr);
                    }

                    cmd.execute(commandLine, options, null);
                }else{
                    System.err.println("[args] wrong args " + args[0]);
                }

            default: ;break;
        }
    }

    private static String[] parseSubArgs(String[] args) {
        if (args.length > 1) {
            String[] result = new String[args.length - 1];
            System.arraycopy(args, 1, result, 0, args.length - 1);
            return result;
        }
        return null;
    }

    private static SubCommand findSubCommand(final String name) {
        for (SubCommand cmd : cmdList) {
            if (cmd.commandName().toUpperCase().equals(name.toUpperCase())) {
                return cmd;
            }
        }

        return null;
    }

    private static void printHelp() {
        System.out.println("The most commonly used admin commands are:");

        for (SubCommand cmd : cmdList) {
            System.out.printf("   %-20s %s\n", cmd.commandName(), cmd.commandDesc());
        }

        System.out.println("\nSee more information on a specific command.");
    }
}
