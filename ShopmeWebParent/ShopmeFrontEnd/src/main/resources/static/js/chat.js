var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    var sender = document.getElementById("sender").value;
    var content = document.getElementById("content").value;
    stompClient.send("/app/chat", {}, JSON.stringify({
        sender: sender,
        content: content
    }));
    document.getElementById("content").value = "";
}

function showMessage(message) {
    var messagesArea = document.getElementById("messages");
    var messageElement = document.createElement("div");
    messageElement.innerHTML = "<strong>" + message.sender + "</strong>: " + message.content;
    messagesArea.appendChild(messageElement);
}

connect();
