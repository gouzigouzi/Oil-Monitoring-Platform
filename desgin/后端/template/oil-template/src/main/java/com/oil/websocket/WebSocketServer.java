package com.oil.websocket;

import com.oil.constant.UserConstant;
import com.oil.mapper.postgres.TodoMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Component
@ServerEndpoint("/ws/{username}")
public class WebSocketServer {
    @Autowired
    TodoMapper todoMapper;

    //存放会话对象
    private static final Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("客户端：" + username + "建立连接");
        sessionMap.put(username, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        System.out.println("收到来自客户端：" + username + "的信息:" + message);
        if (username.equals(UserConstant.ADMIN)){
            // 如果是admin, 传过来的message是username-operation(agree) or username-operation-rejectReason
            String[] split = message.split("-");
            if (split.length == 0)return;
            String toUsername = split[0];
            String operation = split[1];
            if (split.length == 2){
                // 说明是通过申请
                String notification = "恭喜！" + operation + "的申请已经通过";
                sendToUser(toUsername, notification);
            }
            if (split.length == 3){
                // 说明是拒绝申请
                String rejectReason = split[2];
                String notification = "抱歉！" + operation + "的申请已经拒绝,拒绝理由是:" + rejectReason;
                sendToUser(toUsername, notification);
            }
        }else {
            // 如果是用户, 传过来的message是请求类型
            String notification = username + "有一条新的申请，请查收:" + message;
            sendToUser(UserConstant.ADMIN, notification);
        }
    }

    /**
     * 连接关闭调用的方法
     *
     * @param username
     */
    @OnClose
    public void onClose(@PathParam("username") String username) {
        System.out.println("连接断开:" + username);
        sessionMap.remove(username);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // 记录错误信息
        System.err.println("WebSocket error: " + throwable.getMessage());
        // 关闭会话或发送错误消息到客户端
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发
     *
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendToUser(String user, String message){
        Session session = sessionMap.get(user);
        if (session != null){
            try {
                session.getBasicRemote().sendText(message);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
