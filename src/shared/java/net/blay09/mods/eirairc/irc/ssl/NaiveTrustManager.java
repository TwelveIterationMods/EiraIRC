// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.irc.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class NaiveTrustManager implements X509TrustManager {

	private X509Certificate[] chain;

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		this.chain = chain;
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return chain;
	}
}
