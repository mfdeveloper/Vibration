// --- Global functions ---

function setTimes(func, times, ...args) {
    const returnValues = [];

    for (let i = 1; i <= times; i++) {
        const returned = func(...args)
        returnValues.push(returned)
    }

    return returnValues;
}

Module.isVibrateSupported = function() {
    if (window.navigator && window.navigator.vibrate) {
        return true;
    }

    return false;
};

Module.strJSToStrCS = function(str) {
    const bufferSize = lengthBytesUTF8(str) + 1;
    const buffer = _malloc(bufferSize);
    stringToUTF8(str, buffer, bufferSize);
    return buffer;
}

Module.strCSToStrJS = function(str) {
    return UTF8ToString(str);
}

// --- Polyfills ---

Navigator.prototype.isVibrateSupported = Module.isVibrateSupported;