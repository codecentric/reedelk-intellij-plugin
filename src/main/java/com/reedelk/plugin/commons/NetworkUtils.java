package com.reedelk.plugin.commons;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class NetworkUtils {

    private static final int MIN_PORT_NUMBER = 1100;
    private static final int MAX_PORT_NUMBER = 49151;

    private NetworkUtils() {
    }

    public static boolean available(String bindAddress, int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        InetSocketAddress socketAddress = new InetSocketAddress(bindAddress, port);
        try (ServerSocket serverSocket = new ServerSocket();) {
            serverSocket.bind(socketAddress);
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
            // ignored
        }
        return false;
    }

}
