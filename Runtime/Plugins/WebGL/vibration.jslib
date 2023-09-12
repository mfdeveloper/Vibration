mergeInto(LibraryManager.library, {
    Vibrate: function (milliseconds = 0, typeValue = -2) {
        const vibrationType = VibrationType.getByValue(typeValue);

        if (vibrationType) {

            if (vibrationType.composition) {
                try {
                    vibrationType.composition();
                    return;
                } catch (error) {}
            }
            
            milliseconds = vibrationType.duration || vibrationType.pattern || milliseconds;
        }

        if (navigator.isVibrateSupported()) {
            const result = navigator.vibrate(milliseconds);

            if (result) {
                const millisecondsResult = Array.isArray(milliseconds) ? `[${milliseconds}]` : milliseconds;
                console.log(`[VibrationPlugin] Vibration with result: '${millisecondsResult}'`);
            }
        }
    },
    
    VibrateWithPattern: function (pattern = [], patternSize = 0, typeValue = -2) {
        let intPattern = [];
        const vibrationType = VibrationType.getByValue(typeValue);

        if (vibrationType.composition) {
            try {
                vibrationType.composition();
                return;
            } catch (error) {}
        }

        if (vibrationType && vibrationType.pattern && vibrationType.pattern.length > 0) {
            intPattern = vibrationType.pattern;
        } else {

            for(var i = 0; i < patternSize; i++) {
                const value = HEAPU32[(pattern >> 2) + i];
                intPattern.push(value);
            }

            console.log(`[VibrationPlugin] Converted from: [${pattern}] to pattern: ['${intPattern}]'`);
        }

        if (navigator.isVibrateSupported()) {
            const result = navigator.vibrate(intPattern);

            if (result) {
                console.log(`[VibrationPlugin] Vibration with result: '[${intPattern}]'`);
            }
        }
    },

    VibrateCancel: function () {
        if (!navigator.isVibrateSupported()) {
            return;
        }

        const result = navigator.vibrate([]);

        if (result) {
            console.log('[VibrationPlugin] Cancelled last vibration');
        }
    },
});
