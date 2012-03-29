package com.example.android.btpan;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    String displayName;
    String hostAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);

        try {
            // Needs android.permission.INTERNET.
            if (ensureBnepAddress()) {
                tv.setText(displayName + ": inet " + hostAddress);
            } else {
                tv.setText("not found");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            tv.setText(e.toString());
        }

        setContentView(tv);
    }

    /**
     * Ensure IPv4 BNEP intarface name and address if available.
     */
    boolean ensureBnepAddress() throws SocketException {

        for (NetworkInterface i : Collections.list(NetworkInterface
                .getNetworkInterfaces())) {

            if (!isBnepInterface(i))
                continue;

            InetAddress addr = extractBnepAddress(i.getInterfaceAddresses());

            if (addr != null) {

                displayName = i.getDisplayName();
                hostAddress = addr.getHostAddress();

                return true;
            }
        }

        return false;
    }

    /**
     * Returns IPv4 BNEP address if available.
     */
    InetAddress extractBnepAddress(List<InterfaceAddress> addresses) {
        for (InterfaceAddress address : addresses) {

            if (!isIPv4Address(address))
                continue;

            return address.getAddress();
        }
        return null;
    }

    /**
     * Returns true if the network interface is a BNEP interface.
     */
    static boolean isBnepInterface(NetworkInterface itf) {
        String displayName = itf.getDisplayName();
        return displayName.startsWith("bnep");
    }

    /**
     * Returns true if the network interface is a IPv4 interface.
     */
    static boolean isIPv4Address(InterfaceAddress ifAddr) {
        return ifAddr.getAddress() instanceof Inet4Address;
    }
}