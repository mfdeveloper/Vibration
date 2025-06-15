mergeInto(LibraryManager.library, {
    /**
     * Vibrate for a duration of {@link milliseconds} or from a pre-defined {@link VibrationType} effect
     * @param {number} milliseconds An integer value duration, in milliseconds
     * @param {number} [typeValue] The vibration effect integer value defined in {@link VibrationType} enum class
     * @returns {void}
     */
    Vibrate: function(milliseconds = 0, typeValue = -2) {
        Module.vibration.vibrate(milliseconds, typeValue);
    },
    
    /**
     * Vibrate with a {@link pattern} array, or which one from {@link VibrationType} defined in {@link typeValue} param
     * @param {number[]} pattern An array of pattern milliseconds durations and pause values to vibrate
     * @param {number} patternSize The lenght of {@link pattern} array
     * @param {number} [typeValue] The vibration effect integer value defined in {@link VibrationType} enum class
     * @param {number} [repeat] A positive integer value to repeat the {@link pattern}, or -1 otherwise (not supported on HTML5/WebGL yet)
     * @returns {void}
     * @see {@link VibrationType}
     * @see {@link navigator.vibrate}
     * @see [MDN: Vibration API](https://developer.mozilla.org/en-US/docs/Web/API/Vibration_API)
     * @see [Adding Haptic Feedback to HTML](http://diagramcenter.org/adding-haptic-feedback-to-html.html)
     * @see [navigator.vibrate more than 1 time](https://stackoverflow.com/a/44618810)
     */
    VibrateWithPattern: function(pattern = [], patternSize = 0, typeValue = -2, repeat = -1) {

        // Emscripten array conversion only on .jslib file bridge
        if (typeof pattern == 'number' && pattern > 0 && patternSize > 0 && typeValue < 0) {
            let intPattern = [];
            for(var i = 0; i < patternSize; i++) {
                const value = HEAPU32[(pattern >> 2) + i];
                intPattern.push(value);
            }

            if (intPattern && intPattern.length > 0) {
                
                console.log(`[VibrationPlugin] Converted from: [${pattern}] to pattern: ['${intPattern}]'`);
                pattern = intPattern;
            }
        }

        Module.vibration.vibrateWithPattern(pattern, typeValue, repeat);
    },

    /**
     * Cancel the last running vibration
     * @returns {void}
     */
    VibrateCancel: function() {
        Module.vibration.cancel();
    },

    /**
     * Check if your browser/device supports vibration
     * @returns {number} 1 if vibration is supported in your browser, 0 otherwise
     */
    IsVibrateSupported: function() {
       return Module.vibration.isSupported() == true ? 1 : 0;
    },
});
