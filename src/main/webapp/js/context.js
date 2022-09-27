/**
 * @returns {Promise<{user:User, chats:{[key:string]: {id:string, users:string[], messages:{sender:string,data:string}[]}}, users:{[key:string]:User}, activeUsers: string[], activeChat:string|null}>}
 */
function initContext() {
    var context = {
        user: null,
        users: {}
    }

    function loadChats() {
        return Rest.doGet("chats?receiver=" + context.user.id).then(function (chats) {
            context.chats = {};
            chats.forEach(function (chat, index) {
                if (index === 0) {
                    context.activeChat = chat
                }
                context.chats[chat] = {id: chat, users: null};
            });

            persistContext(context);
            return chats;
        })
    }

    var storage = localStorage.getItem("whisper")
    if (storage) {
        try {
            var json = JSON.parse(storage);
            context.user = json.user;
            context.user.publicKey = Uint8Array.from(json.user.publicKey);
            if (context.user.publicKey.length !== 32) {
                throw new Error("public key not valid length")
            }
            context.user.secretKey = Uint8Array.from(json.user.secretKey);
            if (context.user.secretKey.length !== 32) {
                throw new Error("private key not valid length")
            }
            context.chats = {};
            context.activeChat = json.activeChat;
            context.users = {};
            context.users[context.user.id] = context.user;
            json.chats.forEach(function (id) {
                context.chats[id] = {
                    id: id,
                    users: null
                };
            })
        } catch (e) {
            context.user = null;
            alert("bullshit in localstorage")
        }
    }
    return new Promise(resolve => {

        if (!context.user) {
            var popupContainer = createPopup();
            var popup = popupContainer.popup;

            var header = document.createElement('h2');
            header.innerText = "Hello, who are you?";
            popup.appendChild(header);

            var errorMessage = document.createElement('div');
            errorMessage.style.color = 'red';
            popup.appendChild(errorMessage);

            var newUser = document.createElement("button");
            newUser.innerText = "I'm new user";
            newUser.onclick = function () {
                var keyPair = nacl.box.keyPair();
                context.user = {
                    id: null,
                    publicKey: keyPair.publicKey,
                    secretKey: keyPair.secretKey
                }
                console.log(context.user.publicKey);
                Rest.doPost("/customers", {pk: context.user.publicKey.join(",")}).then(function (response) {
                    context.user.id = response.id;
                    context.users[context.user.id] = context.user;
                    persistContext(context);
                    popupContainer.close();

                    var userData = new UserData({
                        title: "Please save your id and private key. They are used as login credentials",
                        id: context.user.id,
                        publicKey: context.user.publicKey.join(","),
                        secretKey: context.user.secretKey.join(','),
                        buttons: [
                            {
                                title: "Close window, I save it.",
                                onclick: function () {
                                    userData.close();
                                }
                            }
                        ]
                    });

                    resolve(context);
                }).catch(function (e) {
                    var status = document.createElement('span');
                    status.innerText = e.status;
                    var message = document.createElement('span');
                    message.innerText = e.responseText;
                    var s = document.createElement('span');
                    s.innerText = "Status code: "
                    errorMessage.appendChild(s);
                    errorMessage.appendChild(status);
                    errorMessage.appendChild(document.createElement('br'));

                    var m = document.createElement('span');
                    m.innerText = "Message: "
                    errorMessage.appendChild(m);
                    errorMessage.appendChild(message);
                })
            }
            popup.appendChild(newUser);

            var existingUser = document.createElement('button');
            existingUser.innerText = "I'm already registered";
            existingUser.onclick = function () {
                var loginPopup = new UserData({
                    id: '',
                    secretKey: '',
                    publicKey: null,
                    title: "If so, paste your id and private key",
                    buttons: [
                        {
                            title: "Back",
                            onclick: function () {
                                loginPopup.close();
                            }
                        },
                        {
                            title: 'Next',
                            onclick: function () {
                                if (!loginPopup.id.value || !loginPopup.secretKey.value) {
                                    return;
                                }
                                loginPopup.error('')
                                Rest.doGet('customers/' + loginPopup.id.value).then(function (user) {
                                    try {
                                        var publicKey = Uint8Array.from(user.pk
                                            .split(",")
                                            .map(function (str) {
                                                return parseInt(str);
                                            }));
                                        var privateKey = Uint8Array.from(loginPopup.secretKey.value
                                            .split(",")
                                            .map(function (str) {
                                                return parseInt(str);
                                            }));

                                        var test = stringToUint8("test");
                                        var nonce = new Uint8Array(24);
                                        self.crypto.getRandomValues(nonce)
                                        var encrypted = nacl.box(test, nonce, publicKey, privateKey);
                                        var decrypted = nacl.box.open(encrypted, nonce, publicKey, privateKey);
                                        if ("test" === uint8ToString(decrypted)) {
                                            context.user = {
                                                id: user.id,
                                                publicKey: publicKey,
                                                secretKey: privateKey
                                            };
                                            context.users = {};
                                            context.users[context.user.id] = context.user;

                                            loadChats().then(function () {
                                                loginPopup.close();
                                                popupContainer.close();

                                                resolve(context);
                                            }).catch(function (e) {
                                                loginPopup.error(e + '')
                                            })
                                        } else {
                                            loginPopup.error('Your private key invalid');
                                        }
                                    } catch (e) {
                                        loginPopup.error('Your private key invalid');
                                    }
                                }).catch(function (e) {
                                    loginPopup.error("Something wrong, error status is: " + e.status);
                                });
                            }
                        }
                    ]
                });

            }
            popup.appendChild(existingUser);
        } else {
            loadChats().then(function () {
                resolve(context)
            }).catch(function (e) {
                var popup = createConfirm({
                    title: "Fail to load chats",
                    yes: {
                        title: "Reload",
                        onclick: function () {
                            loadChats().then(function () {
                                resolve(context);
                                popup.close();
                            }).catch(function (e) {
                                console.log(e)
                            })
                        }
                    }
                });
            })
        }
    });
}


function persistContext(context) {
    var value = {
        user: {
            id: context.user.id,
            publicKey: Array.from(context.user.publicKey),
            secretKey: Array.from(context.user.secretKey),
        },
        users: {},
        chats: [],
        activeChat: context.activeChat
    };
    for (var chatId in context.chats) {
        value.chats.push(chatId);
    }
    localStorage.setItem('whisper', JSON.stringify(value));
}