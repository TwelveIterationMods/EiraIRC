// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.irc.ssl;

import com.google.common.collect.Lists;
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
import java.util.List;

public class IRCConnectionSSLImpl extends IRCConnectionImpl {

	public IRCConnectionSSLImpl(ServerConfig serverConfig, String nick) {
		super(serverConfig, nick);
	}

	@Override
	protected Socket connect() throws Exception {
		if (!SharedGlobalConfig.sslCustomTrustStore.get().isEmpty()) {
			System.setProperty("javax.net.ssl.trustStore", SharedGlobalConfig.sslCustomTrustStore.get());
		}
		SSLSocketFactory socketFactory;
		if (SharedGlobalConfig.sslTrustAllCerts.get()) {
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
					sslSocket = (SSLSocket) socketFactory.createSocket(underlying, Utils.extractHost(SharedGlobalConfig.proxyHost.get()), Utils.extractPorts(SharedGlobalConfig.proxyHost.get(), DEFAULT_PROXY_PORT)[0], true);
				} else {
					sslSocket = (SSLSocket) socketFactory.createSocket(host, ports[i]);
				}
				if (!SharedGlobalConfig.bindIP.get().isEmpty()) {
					sslSocket.bind(new InetSocketAddress(SharedGlobalConfig.bindIP.get(), ports[i]));
				}
				if (SharedGlobalConfig.sslDisableDiffieHellman.get()) {
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
		List<String> limited = Lists.newLinkedList();
		for(String suite : sslSocket.getEnabledCipherSuites()) {
			if(!suite.contains("_DHE_")) {
				limited.add(suite);
			}
		}
		sslSocket.setEnabledCipherSuites(limited.toArray(new String[limited.size()]));
	}

}
