package com.vision.tool.kit.util;

import java.lang.management.ManagementFactory;
import java.util.List;

public class DebugUtil {

    public static boolean isDebugMode() {
        List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : arguments) {
            if (arg.contains("-agentlib:jdwp")) {
                return true; // JVM 正在通过 JDWP 调试接口运行
            }
        }
        return false;
    }
}
