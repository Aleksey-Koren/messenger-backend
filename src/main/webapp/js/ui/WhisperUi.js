/**
 * @param context {{logout: () => Promise<void>, newUser: (User) => void, selectUser: (User) => void, sendMessage: (string) => Promise}}
 * @constructor
 */
function WhisperUi(context) {
    this.context = context;
    this.container = document.createElement('div');

    this.messagesContainer = document.createElement('div');
    this.messagesContainer.className = 'ui-messages';
    this.messagesHistory = {};
    this.container.appendChild(this.messagesContainer);

    this.textarea = document.createElement('textarea');
    this.textarea.className = "ui-input";
    this.container.appendChild(this.textarea);

    this.sendButton =  document.createElement('button');
    this.sendButton.className = "ui-send-message"
    this.sendButton.innerText = "Send"
    this.sendButton.onclick = function() {
        if(me.textarea.value) {
            me.context.sendMessage(me.textarea.value).then(function(){
                me.textarea.value = ''
            });
        }
    }
    this.container.appendChild(this.sendButton);

    this.sidebar = document.createElement('div');
    this.sidebar.className = 'ui-sidebar';
    this.container.appendChild(this.sidebar);

    this.addUser = document.createElement('button');
    this.chats = {};
    var me = this;
    this.addUser.onclick = function(e) {
        e.preventDefault();
        var popup = createPopup();
        var header = document.createElement('h2');
        header.innerText = 'Paste user id:';
        popup.popup.appendChild(header);

        var error = document.createElement('div');
        popup.popup.appendChild(error);
        error.style.color = 'red';

        var input = document.createElement('input');
        popup.popup.appendChild(input);

        var search = document.createElement('button');
        search.innerText = 'Search';
        popup.popup.appendChild(search);
        search.onclick = function(e) {
            if(input.value) {
                Rest.doGet("customers/" + input.value).then(function(user) {
                    me.context.newUser({
                        id: user.id,
                        publicKey: Uint8Array.from(user.pk.split(","))
                    });
                    me.messagesContainer.innerHTML = '';
                    me.textarea.value = '';
                    popup.close();
                }).catch(function(e) {
                    console.log(e);
                    switch (e.status) {
                        default:
                            error.innerText = "Something wrong, error status is: " + e.status;
                    }
                });
            }
        }

        var close = document.createElement('button');
        close.innerText = 'Close';
        popup.popup.appendChild(close);
    }
    this.addUser.innerText = "Find user"
    this.sidebar.appendChild(this.addUser);

    this.lastMessage = {
        from: null,
        container: null
    }

    this.logout = document.createElement('button');
    this.logout.onclick = function() {
        var logoutPopup = createPopup();
        var header = document.createElement('h2');
        header.innerText = "Are you sure to logout?";
        logoutPopup.popup.appendChild(header);

        var no = document.createElement('button');
        no.innerText = 'Cancel';
        no.onclick = function() {
            logoutPopup.close();
        }
        logoutPopup.popup.appendChild(no);

        var yes = document.createElement('button');
        yes.innerText = 'Yes, logout';
        yes.onclick = function() {
            logoutPopup.close();
            me.context.logout().then(function() {
                me.chats = {};
                me.chatsContainer.innerHTML = '';
                me.messagesContainer.innerHTML = '';
                me.textarea.value = '';
            });
        }
        logoutPopup.popup.appendChild(yes);


    }
    this.logout.innerText = "Logout";
    this.sidebar.appendChild(this.logout);

    this.chatsContainer = document.createElement('div');
    this.sidebar.appendChild(this.chatsContainer);
}

WhisperUi.prototype.addChat = function(chat) {
    var container = document.createElement('div');
    container.className = 'ui-chat';
    container.innerText = chat.id;
    var me = this;
    container.onclick = function() {
        me.context.selectUser(chat);
        me.selectChat(chat);
    }
    this.chats[chat.id] = {
        id: chat.id,
        container: container
    };
    this.chatsContainer.appendChild(container)
}
/**
 *
 * @param chat{{id: string}}
 */
WhisperUi.prototype.selectChat = function(chat) {
    this.messagesContainer.innerHTML = '';
    this.lastMessage.from = null;
    this.lastMessage.container = null;
    for(var chatId in this.chats) {
        if(this.chats[chatId].id === chat.id) {
            this.chats[chatId].container.className = 'ui-chat-selected ui-chat';
            this.context.selectChat(chat);
        } else {
            this.chats[chatId].container.className = 'ui-chat';
        }
    }
}
/**
 *
 * @param whisper {{sender: string, id: string, message: string, created: string, type: string}}
 * @param you string
 */
WhisperUi.prototype.addMessage = function(whisper, you) {
    if(whisper.type !== 'whisper' || this.messagesHistory[whisper.id]) {
        return;
    }
    this.messagesHistory[whisper.id] = 1;
    
    var usePrevious = this.lastMessage.from === whisper.sender;
    var myMessage = whisper.sender === you;
    var messageContainer = usePrevious ? this.lastMessage.container : document.createElement('div');
    if(!usePrevious) {
        var author = document.createElement('div');
        author.innerText = (myMessage ? "you" : whisper.sender) + " (" + whisper.created + "):";
        author.style.fontWeight = 'bold';
        messageContainer.appendChild(author);
        messageContainer.className = myMessage ? 'ui-my-message ui-message' : 'ui-message';
    }

    var text = document.createElement('p');
    text.innerText = whisper.message;
    messageContainer.appendChild(text);
    if(!usePrevious) {
        this.messagesContainer.appendChild(messageContainer);
        this.lastMessage.from = whisper.sender;
        this.lastMessage.container = messageContainer;
    }
}