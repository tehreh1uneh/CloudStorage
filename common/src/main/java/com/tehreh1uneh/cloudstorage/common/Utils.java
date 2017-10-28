package com.tehreh1uneh.cloudstorage.common;

import org.apache.log4j.Logger;

public final class Utils {

    private static Logger logger = Logger.getLogger(Utils.class);

    private Utils() {
    }

    public static void disconnectClient(SocketThread clientThread, SocketThreadListener listener) {
        new Thread(() -> {
            while (true) {
                if (clientThread == null || !clientThread.isAlive()) break;
                if (!clientThread.isBusy()) {
                    clientThread.interrupt();
                    logger.info("Thread [" + clientThread.getName() + "] успешно остановлен");
                    listener.onStopSocketThread(clientThread);
                    break;
                }
            }
        }).start();
    }
}
