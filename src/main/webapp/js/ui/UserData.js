/**
 * @param params {{
 * buttons: {title:string, onclick: () => void}[],
 * title: string,
 * id: string|null,
 * publicKey: string|null,
 * secretKey: string|null
 * }}
 * @constructor
 */
function UserData(params) {
    this.popup = createPopup();
    var title = document.createElement('h2');
    title.innerText = params.title;
    this.popup.popup.appendChild(title);

    this.errorContainer = document.createElement('div');
    this.errorContainer.style.color = 'red';
    this.popup.popup.appendChild(this.errorContainer);

    if(params.id !== null) {
        var id = document.createElement('input');
        this.id = id;
        id.id = 'acc_id';
        var idSpan = document.createElement('label');
        idSpan.htmlFor = 'acc_id';
        idSpan.innerText = 'Your id is:';
        id.value = params.id;
        this.popup.popup.appendChild(idSpan);
        this.popup.popup.appendChild(id);
        this.popup.popup.appendChild(document.createElement('br'));
    }
    if(params.secretKey !== null) {
        var secretKey = document.createElement('textarea')
        this.secretKey = secretKey;
        secretKey.id = 'secret_id';
        secretKey.value = params.secretKey;
        var secretKeyLabel = document.createElement('label');
        secretKeyLabel.htmlFor = 'secret_id';
        secretKeyLabel.innerText = 'Your private key is:';
        this.popup.popup.appendChild(secretKeyLabel);
        this.popup.popup.appendChild(document.createElement('br'));
        this.popup.popup.appendChild(secretKey);
        this.popup.popup.appendChild(document.createElement('br'));
    }
    if(params.publicKey !== null) {
        var publicKey = document.createElement('textarea')
        publicKey.id = 'public_id';
        publicKey.value = params.publicKey;
        var publicKeyLabel = document.createElement('label');
        publicKeyLabel.htmlFor = 'public_id';
        publicKeyLabel.innerText = 'Your public key is:';
        this.popup.popup.appendChild(publicKeyLabel);
        this.popup.popup.appendChild(document.createElement('br'));
        this.popup.popup.appendChild(publicKey);
        this.popup.popup.appendChild(document.createElement('br'));
    }

    var me = this;
    params.buttons.forEach(function(button) {
        var buttonHtml = document.createElement('button');
        buttonHtml.innerText = button.title;
        me.popup.popup.appendChild(buttonHtml);
        buttonHtml.onclick = button.onclick
    })
}

UserData.prototype.close = function() {
    this.popup.close();
}

UserData.prototype.error = function(error) {
    this.errorContainer.innerText = error;
}

