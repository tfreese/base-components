/*
 * Copyright 2012 The Netty Project The Netty Project licenses this file to you under the Apache
 * License, version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at: http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.freese.base.net.ssl.bogus;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

/**
 * Bogus {@link TrustManagerFactorySpi} which accepts any certificate even if it is invalid.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class BogusSSLTrustManagerFactory extends TrustManagerFactorySpi
{
	/**
	 * 
	 */
	private static final TrustManager DUMMY_TRUST_MANAGER = new X509TrustManager()
	{
		/**
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		@Override
		public void checkClientTrusted(final X509Certificate[] chain, final String authType)
			throws CertificateException
		{
			// Always trust - it is an example.
			// You should do something in the real world.
			// You will reach here only if you enabled client certificate auth,
			// as described in SecureChatSslContextFactory.
			System.err.println("UNKNOWN CLIENT CERTIFICATE: " + chain[0].getSubjectDN());
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		@Override
		public void checkServerTrusted(final X509Certificate[] chain, final String authType)
			throws CertificateException
		{
			// Always trust - it is an example.
			// You should do something in the real world.
			System.err.println("UNKNOWN SERVER CERTIFICATE: " + chain[0].getSubjectDN());
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[0];
		}
	};

	/**
	 * @return {@link TrustManager}[]
	 */
	public static TrustManager[] getTrustManagers()
	{
		return new TrustManager[]
		{
			DUMMY_TRUST_MANAGER
		};
	}

	/**
	 * Erstellt ein neues {@link BogusSSLTrustManagerFactory} Object.
	 */
	public BogusSSLTrustManagerFactory()
	{
		super();
	}

	/**
	 * @see javax.net.ssl.TrustManagerFactorySpi#engineGetTrustManagers()
	 */
	@Override
	protected TrustManager[] engineGetTrustManagers()
	{
		return getTrustManagers();
	}

	/**
	 * @see javax.net.ssl.TrustManagerFactorySpi#engineInit(java.security.KeyStore)
	 */
	@Override
	protected void engineInit(final KeyStore keystore) throws KeyStoreException
	{
		// Unused
	}

	/**
	 * @see javax.net.ssl.TrustManagerFactorySpi#engineInit(javax.net.ssl.ManagerFactoryParameters)
	 */
	@Override
	protected void engineInit(final ManagerFactoryParameters managerFactoryParameters)
		throws InvalidAlgorithmParameterException
	{
		// Unused
	}
}
