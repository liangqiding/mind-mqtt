package mind.common.utils;


import cn.hutool.core.util.StrUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Mqtt Topic工具类
 *
 * @author qiding
 */
public final class TopicUtil {

    public static final char TOPIC_ONE = '+';
    public static final char TOPIC_MORE = '#';

    private static final Map<String, Pattern> TOPIC_PATTERN = new ConcurrentHashMap<>(32);

    /**
     * 判断 topicFilter topicName 是否匹配
     *
     * @param topicFilter topicFilter 缓存的订阅关系
     * @param pubTopic    要转发的topic
     * @return 是否匹配
     */
    public static boolean match(String topicFilter, String pubTopic) {
        if (validTopicFilter(topicFilter)) {
            return getTopicPattern(topicFilter).matcher(pubTopic).matches();
        }
        return false;
    }

    /**
     * mqtt topicFilter 转正则
     *
     * @param topicFilter topicFilter
     * @return Pattern
     */
    public static Pattern getTopicPattern(String topicFilter) {
        return TOPIC_PATTERN.computeIfAbsent(topicFilter, TopicUtil::getTopicFilterPattern);
    }

    /**
     * mqtt topic过滤转正则并缓存
     *
     * @param topicFilter t
     * @return p
     */
    public static Pattern getTopicFilterPattern(String topicFilter) {
        // 处理mqtt的分享主题 $share/{ShareName}/{filter}
        String topicRegex = topicFilter.startsWith("$") ? "\\" + topicFilter : topicFilter;
        return Pattern.compile(topicRegex
                .replace("+", "[^/]+")
                .replace("#", ".+")
                .concat("$")
        );
    }

    /**
     * 获取处理完成之后的 topic
     *
     * @param topicTemplate topic 模板
     * @return 获取处理完成之后的 topic
     */
    public static String getTopicFilter(String topicTemplate) {
        // 替换 ${name} 为 + 替换 #{name} 为 #
        return topicTemplate.replaceAll("\\$\\{[\\s\\w.]+}", "+")
                .replaceAll("#\\{[\\s\\w.]+}", "#");
    }

    /**
     * topic校验
     */
    public static boolean validTopicFilter(String topicFilter) {
        // 以#或+符号开头的、以/符号结尾的订阅按非法订阅处理, 这里没有参考标准协议
        if (topicFilter.indexOf(TOPIC_ONE) == 0 || topicFilter.indexOf(TOPIC_MORE) == 0) {
            return false;
        }
        // 如果出现多个#符号的订阅按非法订阅处理
        if (StrUtil.count(topicFilter, TOPIC_MORE) > 1) {
            return false;
        }
        //如果+符号和/+字符串出现的次数不等的情况按非法订阅处理
        return StrUtil.count(topicFilter, TOPIC_ONE) == StrUtil.count(topicFilter, "/+");
    }

}

