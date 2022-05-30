package net.earthcomputer.multiconnect.protocols.v1_12_2;

import com.mojang.brigadier.CommandDispatcher;
import net.earthcomputer.multiconnect.protocols.v1_12_2.command.Commands_1_12_2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TabCompletionManager {

    private static final Queue<Entry> entries = new ArrayDeque<>();
    private static final Queue<CompletableFuture<List<String>>> customCompletions = new ArrayDeque<>();

    public static void reset() {
        entries.clear();
        customCompletions.clear();
    }

    public static void addTabCompletionRequest(int id, String message) {
        synchronized (entries) {
            entries.add(new Entry(id, message));
        }
    }

    public static Entry nextEntry() {
        synchronized (entries) {
            return entries.poll();
        }
    }

    public static void requestCommandList() {
        assert MinecraftClient.getInstance().getNetworkHandler() != null;
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(-1, "/"));
    }

    public static CompletableFuture<List<String>> requestCustomCompletion(String command) {
        assert MinecraftClient.getInstance().getNetworkHandler() != null;
        var future = new CompletableFuture<List<String>>();
        customCompletions.add(future);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(-2,
                command));
        return future;
    }

    public static boolean handleCustomCompletions(Entry entry, List<String> suggestions) {
        if (entry.id() == -1) {
            var dispatcher = new CommandDispatcher<CommandSource>();
            Commands_1_12_2.registerAll(dispatcher, suggestions.stream()
                    .filter(str -> !str.isEmpty())
                    .map(str -> str.substring(1))
                    .collect(Collectors.toSet()));
            assert MinecraftClient.getInstance().getNetworkHandler() != null;
            MinecraftClient.getInstance().getNetworkHandler().onCommandTree(new CommandTreeS2CPacket(dispatcher.getRoot()));
            return true;
        } else if (entry.id() == -2) {
            if (customCompletions.isEmpty())
                return false;
            customCompletions.remove().complete(suggestions);
            return true;
        } else {
            return false;
        }
    }

    public record Entry(int id, String message) {
    }

}
