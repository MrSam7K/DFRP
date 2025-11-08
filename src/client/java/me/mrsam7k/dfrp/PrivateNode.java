package me.mrsam7k.dfrp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PrivateNode {
    private static final URI REMOTE_URI = URI.create("https://raw.githubusercontent.com/MrSam7K/data/main/DFRP/private_nodes.json");

    private static final Map<Integer, Entry> HEADS = new HashMap<>();

    public static final class Entry {
        public String uuid;
        public String displayName;
        public int id;
    }

    public static void init() {
        CompletableFuture.runAsync(PrivateNode::loadFromRemote)
                .exceptionally(ex -> {
                    DFRPClient.LOGGER.warn("Load failed", ex);
                    return null;
                });
    }

    private static void loadFromRemote() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(4))
                    .build();

            HttpRequest request = HttpRequest.newBuilder(REMOTE_URI)
                    .timeout(Duration.ofSeconds(5))
                    .header("User-Agent", "DFRP")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(resp.statusCode() != 200) {
                throw new IOException("HTTP " + resp.statusCode());
            }

            Entry[] array = DFRPClient.GSON.fromJson(resp.body(), Entry[].class);
            apply(array);

            DFRPClient.LOGGER.info("{} private nodes loaded.", HEADS.size());
        } catch (Exception e) {
            DFRPClient.LOGGER.warn("Failed to load private nodes", e);
        }
    }


    private static void apply(Entry[] array) {
        if(array == null || array.length == 0) return;

        Map<Integer, Entry> tmp = new HashMap<>();
        for(Entry e : array) {
            if(e != null)
                tmp.put(e.id, e);
        }

        if(!tmp.isEmpty()) {
            HEADS.clear();
            HEADS.putAll(tmp);
        }
    }

    public static String getPrivateNodeHeadURL(int id) {
        Entry head = HEADS.get(id);

        if(head != null && head.uuid != null && !head.uuid.isEmpty())
            return String.format("https://mc-heads.net/head/%s.png", head.uuid);

        return Node.PRIVATE.imageKey;
    }

    public static String getDisplayName(int id) {
        Entry head = HEADS.get(id);

        if(head != null && head.uuid != null && !head.uuid.isEmpty())
            return head.displayName;

        return Node.PRIVATE.displayName;
    }
}
