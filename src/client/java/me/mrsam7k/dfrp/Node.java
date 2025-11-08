package me.mrsam7k.dfrp;

import dev.dfonline.flint.Flint;

public enum Node {
    NODE1("node1", "Origins"),
    NODE2("node2", "Arcade"),
    NODE3("node3", "Fortress"),
    NODE4("node4", "Valley"),
    NODE5("node5", "Tropics"),
    NODE6("node6", "Canyon"),
    NODE7("node7", "Blossom"),
    PRIVATE("private", "Private"),
    DEV("devnode", "Parliament"),
    DEV2("devnode", "Palace"),
    DEV3("devnode", "Forest"),
    DEV4("devnode", "umm?"),
    ALPHA1("alpha", "Project"),
    ALPHA2("alpha", "Project"),
    BETA("beta", "Project"),
    LOCAL("local", "Bedroom"),
    EVENT("https://minecraft.wiki/images/Nether_Star.gif", "Event"),
    NONE("none", "None"),;

    final String imageKey;
    final String displayName;

    Node(String key, String displayName) {
        this.imageKey = key;
        this.displayName = displayName;
    }

    public static String getImageKey(dev.dfonline.flint.hypercube.Node node) {
        if(node.getId().equals("private"))
            return PrivateNode.getPrivateNodeHeadURL(Flint.getUser().getNodeId());

        for(Node key : Node.values()) {
            if(node.getId().equals(key.name().toLowerCase())) {
                return key.imageKey;
            }
        }

        return NONE.imageKey;
    }

    public static String getDisplayName(dev.dfonline.flint.hypercube.Node node) {
        if(node.getId().equals("private"))
            return PrivateNode.getDisplayName(Flint.getUser().getNodeId());

        for(Node key : Node.values()) {
            if(node.getId().equals(key.name().toLowerCase())) {
                return key.displayName;
            }
        }

        return NONE.displayName;
    }


}
