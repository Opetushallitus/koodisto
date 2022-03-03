const element = document.getElementById('devraamit');
if (!element) {
    const container = document.createElement('div');
    container.id = 'devraamit';
    container.style.cssText = `
            font-size: 14px;
            height: 100px;
            color: white;
            box-sizing: border-box;
            z-index: 100;
            display: flex;
            flex-direction: column;
            background-color: #040066;
            `;
    const headingElement = document.createElement('h4');
    headingElement.append('KoodistoUI DEV!');
    const magentaStyleText = 'color: white';
    headingElement.style.cssText = magentaStyleText;
    container.appendChild(headingElement);
    document.body.prepend(container);
}
