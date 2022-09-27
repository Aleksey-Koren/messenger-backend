/**
 * @returns {{popup: HTMLDivElement, close: () => void}}
 */
function createPopup() {
    var popup = document.createElement('div');
    popup.className = "component-popup";
    document.body.appendChild(popup);

    if (!createPopup.activeOverlay) {
        createPopup.activeOverlay = document.createElement('div');
        createPopup.activeOverlay.className = 'component-overlay';
    }
    if (!createPopup.activePopups) {
        document.body.appendChild(createPopup.activeOverlay);
    }
    createPopup.activePopups++;
    return {
        popup: popup,
        close: function () {
            popup.remove();
            createPopup.activePopups--;
            if (!createPopup.activePopups) {
                createPopup.activeOverlay.remove();
            }
        }
    }
}

createPopup.activeOverlay = null;
createPopup.activePopups = 0;

/**
 * @param params {{
 *     title: string,
 *     yes: {title: string, onclick: () => void},
 *     no: {title: string, onclick: () => void},
 * }}
 * @return {{popup: HTMLDivElement, close: (function(): void)}}
 */
function createConfirm(params) {
    var popup = createPopup();
    var title = document.createElement('h2');
    title.innerText = params.title;
    popup.popup.appendChild(title);

    if (params.no) {
        var no = document.createElement('button');
        no.innerText = params.no.title;
        no.onclick = params.no.onclick
        popup.popup.appendChild(no);
    }
    if (params.yes) {
        var yes = document.createElement('button');
        yes.innerText = params.yes.title;
        yes.onclick = params.yes.onclick
        popup.popup.appendChild(yes);
    }


    return popup;
}