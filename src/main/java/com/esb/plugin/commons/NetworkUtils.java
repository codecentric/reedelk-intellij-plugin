package com.esb.plugin.commons;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class NetworkUtils {

    private static final int MIN_PORT_NUMBER = 1100;
    private static final int MAX_PORT_NUMBER = 49151;

    public static boolean available(String bindAddress, int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(bindAddress, port);
            ss = new ServerSocket();
            ss.bind(socketAddress);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
        return false;
    }

}
