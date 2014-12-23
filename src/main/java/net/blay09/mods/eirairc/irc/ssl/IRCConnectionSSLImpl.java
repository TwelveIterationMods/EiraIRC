// Copyright (c) 2014, Christopher "blay09" Baker

package net.blay09.mods.eirairc.irc.ssl;

import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
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

	public IRCConnectionSSLImpl(ServerConfig serverConfig, String nick) {
		super(serverConfig, nick);
	}

	@Override
	protected Socket connect() {
		try {
			if(!SharedGlobalConfig.sslCustomTrustStore.isEmpty()) {
				System.setProperty("javax.net.ssl.trustStore", SharedGlobalConfig.sslCustomTrustStore);
			}
			SSLSocketFactory socketFactory;
			if(SharedGlobalConfig.sslTrustAllCerts) {
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
				sslSocket = (SSLSocket) socketFactory.createSocket(underlying, Utils.extractHost(SharedGlobalConfig.proxyHost), Utils.extractPort(SharedGlobalConfig.proxyHost, DEFAULT_PROXY_PORT), true);
			} else {
				sslSocket = (SSLSocket) socketFactory.createSocket(host, port);
			}
			if(!SharedGlobalConfig.bindIP.isEmpty()) {
				sslSocket.bind(new InetSocketAddress(SharedGlobalConfig.bindIP, port));
			}
			try {
				if(SharedGlobalConfig.sslDisableDiffieHellman) {
					disableDiffieHellman(sslSocket);
				}
				sslSocket.startHandshake();
			} catch (SSLHandshakeException e) {
				System.out.println("Couldn't connect to " + host + " at port " + port + ": untrusted certificate");
				return null;
			}
			writer = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream(), serverConfig.getCharset()));
			reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream(), serverConfig.getCharset()));
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
