package com.oil.exception;

/**
 * ClassName: ParamNotFoundException
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/5
 * @Version 1.0
 */
public class ParamNotFoundException extends BaseException{
    public ParamNotFoundException() {
    }

    public ParamNotFoundException(String msg) {
        super(msg);
    }
}
