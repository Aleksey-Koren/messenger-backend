var Ajax = {
    /**
     * @var object with key-value pairs with default ajax headers
     */
    headers: null
};

function addHeaders(xhr, headers) {
    if (headers) {
        for (var i in headers) {
            if (headers.hasOwnProperty(i)) {
                xhr.setRequestHeader(i, headers[i]);
            }
        }
    }
}

Ajax.ajax = function (data, resolve, reject) {
    var xhr = Ajax.getXhr();
    xhr.open(data.type, data.url, true);
    addHeaders(xhr, Ajax.headers);
    addHeaders(xhr, data.headers);
    xhr.onload = function () {
        if (xhr.status > 199 && xhr.status < 300) {
            resolve(Ajax.process(xhr, data.responseType), xhr);
        } else if (reject) {
            reject(xhr)
        }
    };
    xhr.send(data.data);
    return xhr;
};
Ajax.process = function (xhr, t) {
    var response = xhr.responseText;
    if (t === 'text' || !response) {
        return response;
    } else {
        return JSON.parse(xhr.responseText);
    }
};
/**
 * @returns XMLHttpRequest
 */
Ajax.getXhr = function () {
    var xmlhttp = null;
    try {
        xmlhttp = new XMLHttpRequest();
    } catch (e) {
        try {
            xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
            try {
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (E) {
                alert('Hey man, are you using browser?');
            }
        }
    }
    return xmlhttp;
};
var Rest = {
    host: null
};
Rest.doGet = function (url, responseType) {
    return Rest._onRequest(url, 'get', null, responseType)
};
Rest.doPost = function (url, data, responseType) {
    return Rest._onRequest(url, 'post', data, responseType)
};
Rest.doPut = function (url, data, responseType) {
    return Rest._onRequest(url, 'put', data, responseType)
};
Rest.doDelete = function (url, data, responseType) {
    return Rest._onRequest(url, 'delete', data, responseType)
};
Rest._onRequest = function (url, type, data, responseType) {
    if (Rest.host !== null) {
        url = Rest.host + url;
    }
    return new Promise(function (resolve, reject) {
        Ajax.ajax({
            responseType: responseType ? responseType : 'json',
            type: type,
            url: url,
            data: typeof data === 'string' || typeof data === 'number' ? data : JSON.stringify(data)
        }, resolve, reject)
    })
};

Ajax.headers = {
    'Content-Type': 'application/json'
}