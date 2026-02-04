package com.oil.utils;

import com.oil.entity.User;

public class UserHolder {
    private static final ThreadLocal<Integer> tl = new ThreadLocal<>();

    public static void saveUser(Integer id){
        tl.set(id);
    }

    public static Integer getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}


