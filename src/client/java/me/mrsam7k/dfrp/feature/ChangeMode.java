package me.mrsam7k.dfrp.feature;

import dev.dfonline.flint.feature.trait.ModeSwitchListeningFeature;
import dev.dfonline.flint.hypercube.Mode;
import me.mrsam7k.dfrp.DFRPClient;

public class ChangeMode implements ModeSwitchListeningFeature {

    @Override
    public void onSwitchMode(Mode oldMode, Mode newMode) {
        DFRPClient.currentMode = newMode;
        DFRPClient.updateRP();
    }
}
