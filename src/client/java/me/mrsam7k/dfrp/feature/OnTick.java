package me.mrsam7k.dfrp.feature;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.hypercube.Mode;
import me.mrsam7k.dfrp.DFRPClient;
import me.mrsam7k.dfrp.ToastUtil;

public class OnTick implements TickedFeature {

    @Override
    public void tick() {
        //Workaround for first join because Flint doesn't run onSwitchMode on first join???
        if(Flint.getUser().getNode() != null && DFRPClient.currentMode == Mode.NONE && Flint.getUser().getMode() == Mode.SPAWN) {
            DFRPClient.initRP();

            DFRPClient.currentMode = Mode.SPAWN;
            DFRPClient.updateRP();
        }

        if(Flint.getUser().getNode() == null && DFRPClient.currentMode != Mode.NONE) {
            DFRPClient.currentMode = Mode.NONE;
            DFRPClient.updateRP();
        }

        if(!DFRPClient.ready && DFRPClient.gettingReady
                && System.currentTimeMillis() - DFRPClient.startedGettingReadyAt >= 6769) {
            DFRPClient.ready = false;
            DFRPClient.gettingReady = false;
            ToastUtil.toast("Could not connect to Discord", "Is it open?");
            DFRPClient.LOGGER.info("Discord connection timed out");
        }
    }
}
