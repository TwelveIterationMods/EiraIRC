// Copyright (c) 2014, Christopher "blay09" Baker

package net.blay09.mods.eirairc.irc.ssl;

import net.blay09.mods.eirairc.config.done.NetworkConfig;
import net.blay09.mods.eirairc.irc.*;
import net.blay09.mods.eirairc.util.Utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class IRCConnectionSSLImpl extends IRCConnectionImpl {

	private SSLSocket sslSocket;

	public IRCConnectionSSLImpl(String host, String password, String nick, String ident, String description) {
		super(host, password, nick, ident, description);
	}

	@Override
	protected Socket connect() {
		try {
			if(!NetworkConfig.sslCustomTrustStore.isEmpty()) {
				System.setProperty("javax.net.ssl.trustStore", NetworkConfig.sslCustomTrustStore);
			}
			SSLSocketFactory socketFactory;
			if(NetworkConfig.sslTrustAllCerts) {
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, new TrustManager[] { new NaiveTrustManager() }, null);
				socketFactory = context.getSocketFactory();
			} else {
				socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			}
			Proxy proxy = createProxy();
			if(proxy != null) {
				Socket underlying = new Socket(proxy);
				underlying.connect(new InetSocketAddress(host, port));
				sslSocket = (SSLSocket) socketFactory.createSocket(underlying, Utils.extractHost(NetworkConfig.proxyHost), Utils.extractPort(NetworkConfig.proxyHost, DEFAULT_PROXY_PORT), true);
			} else {
				sslSocket = (SSLSocket) socketFactory.createSocket(host, port);
			}
			try {
				if(NetworkConfig.sslDisableDiffieHellman) {
					disableDiffieHellman(sslSocket);
				}
				sslSocket.startHandshake();
			} catch (SSLHandshakeException e) {
				System.out.println("Couldn't connect to " + host + " at port " + port + ": untrusted certificate");
				return null;
			}
			writer = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream(), charset));
			reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream(), charset));
			sender.setWriter(writer);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (KeyManagementException e) {
			e.printStackTrace();
			return null;
		}
		return sslSocket;
	}

	private void disableDiffieHellman(SSLSocket sslSocket) {
		List<String> limited = new LinkedList<String>();
		for(String suite : sslSocket.getEnabledCipherSuites()) {
			if(!suite.contains("_DHE_")) {
				limited.add(suite);
			}
		}
		sslSocket.setEnabledCipherSuites(limited.toArray(new String[limited.size()]));
	}

}
