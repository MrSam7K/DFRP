package me.mrsam7k.dfrp;

import com.google.gson.Gson;
import dev.dfonline.flint.Flint;
import dev.dfonline.flint.FlintAPI;
import dev.dfonline.flint.hypercube.Mode;
import dev.dfonline.flint.hypercube.Plot;
import dev.firstdark.rpc.DiscordRpc;
import dev.firstdark.rpc.enums.ActivityType;
import dev.firstdark.rpc.enums.ErrorCode;
import dev.firstdark.rpc.exceptions.UnsupportedOsType;
import dev.firstdark.rpc.handlers.RPCEventHandler;
import dev.firstdark.rpc.models.DiscordRichPresence;
import dev.firstdark.rpc.models.User;
import me.mrsam7k.dfrp.feature.ChangeMode;
import me.mrsam7k.dfrp.feature.OnTick;
import me.mrsam7k.dfrp.feature.PlotSwitch;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DFRPClient implements ClientModInitializer {
    public static final String MOD_ID = "DFRP";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Gson GSON = new Gson();

    public static MinecraftClient MC = MinecraftClient.getInstance();

    public static DiscordRpc rpc = null;

    public static Plot currentPlot;
    public static Mode currentMode = Mode.NONE;
    public static dev.dfonline.flint.hypercube.Node currentNode = null;

    public static boolean ready = false;
    public static boolean gettingReady = false;

    public static long plotChangeTimestamp = System.currentTimeMillis();
    public static long startedGettingReadyAt = System.currentTimeMillis();

    @Override
    public void onInitializeClient() {
        MC = MinecraftClient.getInstance();
        PrivateNode.init();

        FlintAPI.confirmLocationWithLocate();

        FlintAPI.registerFeatures(
                new ChangeMode(),
                new PlotSwitch(),
                new OnTick()
        );
    }

    public static void initRP() {
        rpc = new DiscordRpc();
        gettingReady = true;
        startedGettingReadyAt = System.currentTimeMillis();

        RPCEventHandler handler = new RPCEventHandler() {
            @Override
            public void ready(User user) {
                ready = true;
                LOGGER.info("{} is ready", user.getUsername());
                if(Flint.getClient().textRenderer != null)
                    ToastUtil.toast("Connected to Discord", String.format("Hello, %s!", user.getUsername()));
            }

            @Override
            public void disconnected(ErrorCode errorCode, String message) {
                ToastUtil.toast("Disconnected from Discord", message == null ? "" : message);
                LOGGER.info("DFRP disconnected from Discord ({}): {}", errorCode, message);
                ready = false;
                gettingReady = false;
            }

            @Override
            public void errored(ErrorCode errorCode, String message) {
                ToastUtil.toast("DFRP error", message == null ? "" : message);
                LOGGER.info("DFRP error ({}): {}", errorCode, message);
                ready = false;
                gettingReady = false;
            }
        };

        try {
            rpc.init("1436363273233502259", handler, false);
        } catch (UnsupportedOsType e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateRP() {
        if(currentMode == Mode.NONE) {
            rpc.shutdown();
            rpc = null;
            ready = false;
            gettingReady = false;
            return;
        }

        if(rpc == null) {
            initRP();
        }

        dev.dfonline.flint.hypercube.Node node = Flint.getUser().getNode();
        String nodeDisplay = node.getName() + (node.equals(dev.dfonline.flint.hypercube.Node.PRIVATE) ? " " + Flint.getUser().getNodeId() : "");
        if(node != currentNode) {
            currentNode = node;
            plotChangeTimestamp = System.currentTimeMillis();
        }

        DiscordRichPresence.DiscordRichPresenceBuilder builder = DiscordRichPresence.builder()
                .name("DiamondFire")
                .startTimestamp(plotChangeTimestamp)
                .activityType(ActivityType.PLAYING)
                .largeImageKey(Node.getImageKey(node))
                .largeImageText(nodeDisplay + " - " + Node.getDisplayName(node))
                .button(DiscordRichPresence.RPCButton.of("DiamondFire Discord Server", "https://discord.gg/sNJzkGCHNx"))
                .button(DiscordRichPresence.RPCButton.of("Download DFRP", "https://modrinth.com/mod/dfrp"));

        if(currentMode.equals(Mode.SPAWN)) {
            builder.details("At spawn on " + nodeDisplay);
        } else {
            builder
                    .details(String.format("%s on %s [%d]" + (currentPlot.getHandle() == null ? "" : " [" + currentPlot.getHandle() + "]"),
                            State.valueOf(currentMode.name()).getAction(),
                            currentPlot.getName().getLiteralString(),
                            currentPlot.getId()))
                    .state(String.format("Owner: %s", currentPlot.getOwner()) + (currentPlot.isWhitelisted() ? " [Whitelisted]" : ""))
                    .smallImageKey(currentMode.name().toLowerCase())
                    .smallImageText(State.valueOf(currentMode.name()).getAction());
        }

        rpc.updatePresence(builder.build());
    }

}
