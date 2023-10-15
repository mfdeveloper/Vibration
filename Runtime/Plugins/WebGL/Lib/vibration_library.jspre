/**
* TODO: Create a `VibrationResult` entity in order to return a unique data to console.log() outputs.
* TODO: Add a new method into {@link VibrationType} enum to allow add custom effect types from Unity `C#`
*
* A vanilla JS local library file, containing all reusable code to be called
* in the `.jslib` file, the methods bridge with the game engine
*
* **PS:** Pay attention that order of the `.jspre` files are included, depends of the name
* 
* ### Reference
* 
* - [Unity WebGL Secrets](https://medium.com/@depanther/unity-webgl-secrets-b6ddf214f1fd)
*/

/**
 * Strategy abstract class to create new effects compositions.
 * 
 * The method `{@link VibrationComposition#composition}` should be implemented
 * in a inherited class that should implement a custom effect from scratch
 */
class VibrationComposition {
    composition() {
        throw new Error('Not implemented!');
    }
}

class GrowsIntensityDiesOffComposition extends VibrationComposition {

    timeoutAmongTypes = 250;

    composition() {

        if (!navigator.isVibrateSupported()) {
            return;
        }
        
        // --- Simulate the composition --
        // (based in Android haptic `VibrationEffect.startComposition()`)

        let result = navigator.vibrate(VibrationType.SLOW_RISE.pattern);
        let fnResults = [];

        setTimeout(() => {
            
            result = navigator.vibrate(VibrationType.QUICK_FALL.pattern);
            setTimeout(() => {
                
                fnResults = setTimes(() => {
                    return navigator.vibrate(VibrationType.TICK.pattern);
                }, 2);
                
                result = fnResults.every((rs) => rs == true);

                if (result) {
                    
                    const patterns = 
                    `[${VibrationType.SLOW_RISE.pattern}], ` + 
                    `[${VibrationType.QUICK_FALL.pattern}], ` +
                    `[${VibrationType.TICK.pattern}]`;

                    console.log(`[VibrationPlugin] Vibration with result: '${patterns}'`)
                }

            }, this.timeoutAmongTypes); 
        }, this.timeoutAmongTypes); // Adjust the timings to match your composition
    }
}

/**
 * The Enum class pattern of Vibration effects built-in types
 * 
 * ### References
 * 
 * - [Creating Enums in Vanilla JavaScript](https://blog.bitsrc.io/explaining-enums-enumerations-in-plain-javascript-895a226622e3)
 * - [Unity WebGL Secrets](https://medium.com/@depanther/unity-webgl-secrets-b6ddf214f1fd)
 */
class VibrationType {
    
    //#region Enum values

    static NONE = new VibrationType('NONE', -2);

    static TICK = new VibrationType('TICK', 0, {
        pattern: [25, 50, 25, 50, 25] 
    });

    /**
     * The {@link VibrationType.pattern} pattern here consists of 
     * short vibrations of 25ms followed by pauses of 100ms between them.
     */
    static LOW_TICK = new VibrationType('LOW_TICK', 1, {
        pattern: [25, 100, 25, 100, 25]
    });

    static SHORT = new VibrationType('SHORT', 2, {
        duration: 20,
        pattern: this.TICK.pattern
    });

    static LIGHT = new VibrationType('LIGHT', 3, {
        duration: this.TICK.duration, 
        pattern: this.TICK.pattern
    });

    static NORMAL = new VibrationType('NORMAL', 4, {
        duration: 1000
    });

    static CLICK = new VibrationType('CLICK', 5, {
        pattern: [50]
    });

    static DOUBLE_TAP = new VibrationType('DOUBLE_TAP', 6, {
        pattern: [50, 100, 50]
    });

    static DOUBLE_CLICK = new VibrationType('DOUBLE_CLICK', 7, {
        duration: this.DOUBLE_TAP.duration, 
        pattern: this.DOUBLE_TAP.pattern
    });

    static HEAVY = new VibrationType('HEAVY', 8, {
        pattern: [200]
    });

    static THUD = new VibrationType('THUD', 9, {
        pattern: [100, 100, 100, 100]
    });

    static SPIN = new VibrationType('SPIN', 10, {
        pattern: [200, 100, 200, 100, 200, 100]
    });

    static QUICK_RISE = new VibrationType('QUICK_RISE', 11, {
        pattern: [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
    });

    static SLOW_RISE = new VibrationType('SLOW_RISE', 12, {
        pattern: [50, 40, 30, 20, 10]
    });

    static QUICK_FALL = new VibrationType('QUICK_FALL', 13, {
        pattern: [100, 90, 80, 70, 60, 50, 40, 30, 20, 10]
    });

    static GROWS_INTENSITY_DIES_OFF = new VibrationType('GROWS_INTENSITY_DIES_OFF', 14, {
        composition: new GrowsIntensityDiesOffComposition()
    });

    //#endregion
    
    //#region Fields

    #compositionInstance = new VibrationComposition();

    name = "";
    value = -1;
    duration = 0;
    /**
     * @type {number[]}
     */
    pattern = [];
    composition = () => {};


    //#endregion

    constructor(
        name = "", 
        value = -1, 
        settings = {
            duration: null, 
            pattern : new Array(), 
            composition: null
        }
    ) {
        this.name = name;
        this.value = value;
        
        this.duration = settings.duration || this.duration;
        this.pattern = settings.pattern || this.pattern;
        this.#compositionInstance = settings.composition;

        if (this.#compositionInstance instanceof VibrationComposition) {
            this.composition = this.#compositionInstance.composition.bind(this);
        } else {
            this.composition = null;
        }

        Object.freeze(this);
    }

    //#region Methods

    toString() {
        let compositionValue = this.composition ? 'function(){}' : 'null';

        return `[${this.name}](
            value=${this.value},
            duration=${this.duration},
            pattern=[${this.pattern}],
            composition=${compositionValue}
        )`;
    }

    static getByValue(value = -1) {
        let type = VibrationType.prototype;

        for (const typeName in VibrationType) {
            const vibrationType = VibrationType[typeName];
            if (vibrationType.value == value) {
                // TODO: Remove this log from here, and use a new "VibrationResult" data instance
                //       to show output results on `.jslib` only 
                console.log(`[VibrationPlugin] Using VibrationType: ${vibrationType}`);
                type = vibrationType;
                break;
            }
        }
        
        return (type.name.length > 0 && type.value >= 0) ? type : null;
    }

    //#endregion
}

/**
 * A vanilla JS vibration **Singleton** class that can be reused on several game engines
 * @see [Singleton Design Pattern: JavaScript](https://www.freecodecamp.org/news/singleton-design-pattern-with-javascript)
 * @see [Object.freeze()](https://developer.mozilla.org/pt-BR/docs/Web/JavaScript/Reference/Global_Objects/Object/freeze)
 */
class Vibration {

    /**
     * @type {(Vibration|null)}
     */
    static instance = null;

    constructor() {
        // Make sure that any attempts of change on that Singleton class
        // will throws a "TypeError" exception
        "use strict";

        if (Vibration.instance instanceof Vibration) {
          throw new Error("New instance cannot be created!!");
        }
    
        Vibration.instance = this;

        Object.freeze(Vibration);
    }

    /**
     * Vibrate for a duration of {@link milliseconds} or from a pre-defined {@link VibrationType} effect
     * @param {number} milliseconds An integer value duration, in milliseconds
     * @param {number} [typeValue] The vibration effect integer value defined in {@link VibrationType} enum class
     * @returns {void}
     */
    vibrate(milliseconds = 0, typeValue = -2) {
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

        if (milliseconds == null || milliseconds <= 0) {
            throw new Error("The 'milliseconds' duration should be greater than 0");
        }

        if (navigator.isVibrateSupported()) {
            const result = navigator.vibrate(milliseconds);

            if (result) {
                const millisecondsResult = Array.isArray(milliseconds) ? `[${milliseconds}]` : milliseconds;
                console.log(`[VibrationPlugin] Vibration with result: '${millisecondsResult}'`);
            }
        }
    }

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
    vibrateWithPattern(pattern = [], typeValue = -2, repeat = -1) {
        const vibrationType = VibrationType.getByValue(typeValue);

        if (vibrationType && vibrationType.composition) {
            try {
                vibrationType.composition();
                return;
            } catch (error) {}
        }

        if (vibrationType && vibrationType.pattern && vibrationType.pattern.length > 0) {
            pattern = vibrationType.pattern;
        }

        if (navigator.isVibrateSupported()) {
            let result = false;

            if (repeat > 0) {
                const repeatMsg = `[VibrationPlugin] Repeat by "${repeat}" times isn't supported yet.\n` + 
                `Prefer use repetition from pattern values (e.g "[500, 300, 500, 300, 500]" => vibrate 3 times)`
                console.warn(repeatMsg);
            }
            
            result = navigator.vibrate(pattern);

            if (result) {
                console.log(`[VibrationPlugin] Vibration with result: '[${pattern}]'`);
            }
        }
    }

    /**
     * Cancel the last running vibration
     * @returns {void}
     */
    cancel() {
        if (!navigator.isVibrateSupported()) {
            return;
        }

        const result = navigator.vibrate([]);

        if (result) {
            console.log('[VibrationPlugin] Cancelled last vibration');
        }
    }

    /**
     * Check if your browser/device supports vibration
     * @returns {number} 1 if vibration is supported in your browser, 0 otherwise
     */
    isSupported() {
        return !navigator.isVibrateSupported() == true ? 1 : 0;
    }
}

/**
 * @type {Vibration}
 */
window.vibration = Object.freeze(new Vibration());

/**
 * @type {Vibration}
 */
Module.vibration = window.vibration;

/**
 * @type {VibrationType}
 */
Module.VibrationType = VibrationType;
