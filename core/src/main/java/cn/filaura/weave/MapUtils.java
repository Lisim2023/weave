package cn.filaura.weave;


import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    /**
     * 根据预期元素数量计算HashMap的推荐初始容量
     *
     * @param size 预期需要存储的元素数量
     * @return 推荐的HashMap初始容量。计算公式为：max( (size/0.75)+1 , 16 )
     */
    public static int calculateHashMapCapacity(int size) {
        return Math.max((int) (size / 0.75) + 1, 16);
    }

    /**
     * 反转Map的键值对（将原Map的value作为key，原key作为value）
     *
     * <p>注意：当原Map存在重复value时，反转后的Map将保留最后一次出现的映射关系，
     * 建议仅在确定原Map值唯一时使用本方法。
     *
     * @param map 需要反转的原始Map
     * @return 新的反转Map。
     */
    public static <K, V> Map<V, K> invertMap(final Map<K, V> map) {
        if (map == null) {
            return null;
        }

        Map<V, K> reverse = new HashMap<>(map.size());
        map.forEach((key, value) -> {
            reverse.put(value, key);
        });
        return reverse;
    }

}
