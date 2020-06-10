using UnityEngine;

/// <summary>
/// Inherit this to additional attributes to your specific ScriptableObject <br/><br/>
/// <b>Obs:</b> Add your custom ScriptableObject to VibrationComponent.options
/// </summary>
[System.Serializable, CreateAssetMenu(fileName = "ConfigOptions", menuName = "ScriptableObjects/ConfigOptions", order = 1)]
public class Options : ScriptableObject
{
    public bool vibration = true;
}