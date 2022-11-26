const { app, BrowserWindow } = require('electron');


app.whenReady().then(() => {
    const win = new BrowserWindow({
        width: 800,
        height: 600,
        resizable: false,
        webPreferences: {
            nodeIntegration: true
        }
    })
    win.setMenu(null);
    win.webContents.on('before-input-event', (ev, input) => {
        if(input.key === 'F12') {
            win.webContents.isDevToolsOpened() ? win.webContents.closeDevTools() : win.webContents.openDevTools({ mode: 'undocked' });
        } else if(input.key === 'F5') {
            win.reload();
        }
    });
    win.loadFile('index.html');
});