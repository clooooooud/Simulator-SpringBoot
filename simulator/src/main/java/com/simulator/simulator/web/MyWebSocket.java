package com.simulator.simulator.web;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * socket连接
 */
@Component
@ServerEndpoint(value = "/Jqcase")
public class MyWebSocket {

    private Session session = null;

    private Integer linkCount=0;

    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet=new CopyOnWriteArraySet<MyWebSocket>();

    /**
     * 新的客户端连接调用的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("-------------有新的客户端连接----------");
        linkCount++;
        this.session = session;
        webSocketSet.add(this);
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     */

    @OnMessage

    public void onMessage(String message){
        System.out.println("发生变化"+message);
        try{
            sendMessage("发生变化"+message);
        }catch (Exception e){

            e.printStackTrace();
        }
    }

    /**
     * 发送信息调用的方法
     * @param message
     * @throws IOException
     */

    public static void sendMessage(String message) throws IOException {
        for (MyWebSocket item : webSocketSet) {
            item.session.getBasicRemote().sendText(message);
        }
    }

    @OnClose
    public void onClose(){
        //thread1.stopMe();
        linkCount--;
        webSocketSet.remove(this);
    }

    @OnError
    public void onError(Session session,Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }
}
