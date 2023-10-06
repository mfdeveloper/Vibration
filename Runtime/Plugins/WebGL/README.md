# WebGL: Unity Javascript browser interaction

Here should have at least one `.jslib` file that have functions
to be called from Unity `C#`.

Besides that, you can separate reusable classes/functions in one or more `.jspre`
files.

> **PS:** Pay attention in the order of `.jspre` files are loaded. It seems that is alphabetical order, I'm not sure :\

> Maybe you can change the order adding [emscripten](https://emscripten.org) arguments from a Editor script with: `UnityEditor.PlayerSettings.WebGL.emscriptenArgs`

## References

- [Unity Manual: Interaction with browser scripting](https://docs.unity3d.com/Manual/webgl-interactingwithbrowserscripting.html)
- [Unity WebGL Secrets](https://medium.com/@depanther/unity-webgl-secrets-b6ddf214f1fd)
