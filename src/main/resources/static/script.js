let token = null;

function start(){
    let nickname = document.getElementById("nickname-input").value;
    let url = "/start";
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            setToken(xhr.responseText)
            console.log(xhr.responseText);
        }
    }
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.send(nickname);

    if(token != null){
        connectToWebSocket(token)
    }else{
        console.log("No token to connect to ws")
    }
}

function setToken(newToken){
    if(newToken != ""){
        token = newToken
    }
}

function connectToWebSocket(authToken) {
    let socket = new SockJS('/ws-register');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/game-socket/'+authToken, function (message){
            receivedMessage(message)
        })
    });
}

function receivedMessage(message){
    console.log(message)
}

function send(){
    let url = "/test/"+document.getElementById("send-to").value;
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            setToken(xhr.responseText)
            console.log(xhr.responseText);
        }
    }
    xhr.open("GET", url, false);
    xhr.send();
}