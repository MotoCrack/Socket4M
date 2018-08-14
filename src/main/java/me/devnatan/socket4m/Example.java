package me.devnatan.socket4m;

import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.enums.SocketCloseReason;
import me.devnatan.socket4m.client.message.Message;

import java.io.IOException;
import java.net.ConnectException;
import java.net.StandardSocketOptions;

import static me.devnatan.socket4m.util.DebugUtil.debug;
import static me.devnatan.socket4m.util.DebugUtil.err;

public class Example {

    private static void v2(String[] args) throws IOException {
        Client c = new Client();
        c.addOption(StandardSocketOptions.SO_KEEPALIVE, true);
        c.setCanDebug(true);
        c.setCanPrintErrors(false);
        c.connect("localhost", 8080, 5000);

        c.on("connect", arguments -> {
            long time = (long) arguments.get("time").getValue();
            System.out.println("Conected in " + time + "ms.");
        });

        c.on("disconnect", arguments -> {
            debug("Finished.");
        });

        c.on("message", arguments -> {
            Message message = (Message) arguments.get("data").getValue();
        });

        c.on("error", arguments -> {
            SocketCloseReason reason = (SocketCloseReason) arguments.get("reason").getValue();
            Throwable throwable = (Throwable) arguments.get("throwable").getValue();
            if(reason != SocketCloseReason.REFUSED)
                throwable.printStackTrace();
            err("Error [" + reason + "] " + throwable.getMessage() + " catched.");
        });
    }

    public static void main(String[] args) {
        try {
            v2(args);
        } catch (ConnectException e) {
            System.out.println("Connection refused server not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}