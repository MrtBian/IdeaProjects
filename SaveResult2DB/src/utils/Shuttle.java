package utils;

import java.util.*;


public class Shuttle {
    // 打乱列表实现方法1
    public <T> void shuffle1(List<T> list) {
        int size = list.size();
        Random random = new Random();

        for(int i = 0; i < size; i++) {
            // 获取随机位置
            int randomPos = random.nextInt(size);

            // 当前元素与随机元素交换
            T temp = list.get(i);
            list.set(i, list.get(randomPos));
            list.set(randomPos, temp);
        }
    }

    // 打乱列表实现方法2
    public <T> void shuffle2(List<T> list) {
        int size = list.size();
        Random random = new Random();

        for(int i = 0; i < size; i++) {
            // 获取随机位置
            int randomPos = random.nextInt(size);

            // 当前元素与随机元素交换
            Collections.swap(list, i, randomPos);
        }
    }

    // 打乱列表实现方法3
    public <T> void shuffle3(List<T> list) {
        // 打乱顺序
        Collections.shuffle(list);
    }
}
