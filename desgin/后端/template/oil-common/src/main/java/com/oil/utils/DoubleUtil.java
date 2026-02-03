package com.oil.utils;

/**
 * ClassName: DoubleUtil
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/16
 * @Version 1.0
 */
public class DoubleUtil {
    /**
     * 对Double类型的数值进行保留小数操作
     * @param num Double类型的数值
     * @param n 保留的小数位数
     * @return
     */
    public static Double round(Double num, int n){
        return Double.parseDouble(String.format("%."+n+"f", num));
    }
}
