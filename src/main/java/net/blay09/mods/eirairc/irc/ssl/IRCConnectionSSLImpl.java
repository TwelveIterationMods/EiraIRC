// Copyright (c) 2014, Christopher "blay09" Baker

package net.blay09.mods.eirairc.irc.ssl;

import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.util.Utils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class IRCConnectionSSLImpl extends IRCConnectionImpl {

	public IRCConnectionSSLImpl(ServerConfig serverConfig, String nick) {
		super(serverConfig, nick);
	}

	@Override
	protected Socket connect() throws Exception {
		if (!SharedGlobalConfig.sslCustomTrustStore.isEmpty()) {
			System.setProperty("javax.net.ssl.trustStore", SharedGlobalConfig.sslCustomTrustStore);
		}
		SSLSocketFactory socketFactory;
		if (SharedGlobalConfig.sslTrustAllCerts) {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[]{new NaiveTrustManager()}, null);
			socketFactory = context.getSocketFactory();
		} else {
			socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		}
		Proxy proxy = createProxy();
		for (int i = 0; i < ports.length; i++) {
			try {
				SSLSocket sslSocket;
				if (proxy != null) {
					Socket underlying = new Socket(proxy);
					underlying.connect(new InetSocketAddress(host, ports[i]));
					sslSocket = (SSLSocket) socketFactory.createSocket(underlying, Utils.extractHost(SharedGlobalConfig.proxyHost), Utils.extractPorts(SharedGlobalConfig.proxyHost, DEFAULT_PROXY_PORT)[0], true);
				} else {
					sslSocket = (SSLSocket) socketFactory.createSocket(host, ports[i]);
				}
				if (!SharedGlobalConfig.bindIP.isEmpty()) {
					sslSocket.bind(new InetSocketAddress(SharedGlobalConfig.bindIP, ports[i]));
				}
				if (SharedGlobalConfig.sslDisableDiffieHellman) {
					disableDiffieHellman(sslSocket);
				}
				sslSocket.startHandshake();
				writer = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream(), serverConfig.getCharset()));
				reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream(), serverConfig.getCharset()));
				sender.setWriter(writer);
				return sslSocket;
			} catch (UnknownHostException e) {
				throw e;
			} catch (IOException e) {
				if (i == ports.length - 1) {
					throw e;
				}
			}
		}
		return null;
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
