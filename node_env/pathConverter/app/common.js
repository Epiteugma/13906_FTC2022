const fs = require('fs');
const path = require('path');

module.exports = {
    config: {
        __cache: {},
        __cacheTime: 0,

        __refreshCache: function () {
            if(!fs.existsSync(path.join(__dirname, 'config.json'))) fs.writeFileSync(path.join(__dirname, 'config.json'), fs.readFileSync(path.join(__dirname, 'config.default.json')));
            if(fs.statSync(path.join(__dirname, 'config.json')).mtimeMs > this.__cacheTime) {
                this.__cache = JSON.parse(fs.readFileSync(path.join(__dirname, 'config.json')));
                this.__cacheTime = fs.statSync(path.join(__dirname, 'config.json')).mtimeMs;
            }
        },

        get: function (key) {
            this.__refreshCache();
            return getter(this.__cache, key);
        },

        set: function (key, value) {
            this.__refreshCache();
            setter(this.__cache, key, value);
            this.__cacheTime = Date.now();
            fs.writeFileSync(path.join(__dirname, 'config.json'), JSON.stringify(this.__cache, null, 4));
        },

        delete: function (key) {
            this.__refreshCache();
            deleter(this.__cache, key);
            this.__cacheTime = Date.now();
            fs.writeFileSync(path.join(__dirname, 'config.json'), JSON.stringify(this.__cache, null, 4));
        }
    }
}

let getter = function (obj, key) {
    key = key.split('.');
    if(key.length > 1) {
        if(!obj[key[0]]) return undefined;
        return getter(obj[key[0]], key.slice(1).join('.'));
    } else {
        return obj[key[0]];
    }
}

let setter = function (obj, key, value) {
    key = key.split('.');
    if(key.length > 1) {
        if(!obj[key[0]]) obj[key[0]] = {};
        setter(obj[key[0]], key.slice(1).join('.'), value);
    } else {
        obj[key[0]] = value;
    }
}

let deleter = function (obj, key) {
    key = key.split('.');
    if(key.length > 1) {
        if(!obj[key[0]]) return;
        deleter(obj[key[0]], key.slice(1).join('.'));
    } else {
        delete obj[key[0]];
    }
}