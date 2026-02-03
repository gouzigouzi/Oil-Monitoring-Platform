let websocket = null;
let messageCallback = null;
let errorCallback = null;

function websocketOpen (e) {
    console.log('ws连接成功');
}

function webSocketOnMessage (e) { 
    messageCallback(JSON.parse(e.data))
}

function webSocketSend (agentData) {
    if (websocket.readyState === websocket.OPEN) { // websock.OPEN = 1 
        // 发给后端的数据需要字符串化
        websocket.send(JSON.stringify(agentData))
    }
}

function webSocketClose (e) {  
    console.log("关闭webSocket");
}

function initWebSocket(url) {
    if (typeof (WebSocket) === 'undefined') {
        Message.error('您的浏览器不支持WebSocket,无法获取数据')
        return false
    }
    const webSocketUrl = 'ws:localhost:8999/ws/' + url;
    websocket = new WebSocket(webSocketUrl);
    websocket.onmessage = function (e) {
        webSocketOnMessage(e)
    } 
    websocket.onopen = function () {
        websocketOpen()
    }
    websocket.onerror = function () {
        console.log('webSocket连接异常,请稍候重试');
    }
    websocket.onclose = function (e) {
        webSocketClose(e)
    } 
}


export function sendWebsocket (url, agentData, successCallback, errCallback) { 
    initWebSocket(url)
    messageCallback = successCallback
    errorCallback = errCallback
    webSocketSend(agentData)
}

/**
 * 关闭websocket函数
 */
export function closeWebsocket () {
    if (websocket) {
        websocket.close() // 关闭websocket
        websocket.onclose() // 关闭websocket
    }
}
