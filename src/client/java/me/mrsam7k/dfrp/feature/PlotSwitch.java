package me.mrsam7k.dfrp.feature;

import dev.dfonline.flint.feature.trait.PlotSwitchListeningFeature;
import dev.dfonline.flint.hypercube.Plot;
import me.mrsam7k.dfrp.DFRPClient;

public class PlotSwitch implements PlotSwitchListeningFeature {
    @Override
    public void onSwitchPlot(Plot oldPlot, Plot newPlot) {
        DFRPClient.currentPlot = newPlot;
        DFRPClient.plotChangeTimestamp = System.currentTimeMillis();
    }
}
