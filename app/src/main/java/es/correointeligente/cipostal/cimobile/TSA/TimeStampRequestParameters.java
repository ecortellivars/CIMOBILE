/**
 * Copyright 2012 Agencia de Tecnolog�a y Certificaci�n Electr�nica
 */
package es.correointeligente.cipostal.cimobile.TSA;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Par�metros que se le pueden pasar a la llamada del sello de tiempos:
 * <ul>
 * 	<li>user: Usuario utilizado si la llamada requiere autenticaci�n.</li>
 * 	<li>password: Contrase�a utilizada si la llamada requiere autenticaci�n.</li>
 * 	<li>oid: OID demandado a la TSA para que se incluya en la respuesta.</li>
 * </ul>
 * 
 * @author <a href="mailto:jgutierrez@accv.es">Jos� Manuel Guti�rrez N��ez</a>
 *
 */
public class TimeStampRequestParameters {

	private String user;
	private String password;
	private String oid;
	private BigInteger nonce;
	private List<TimeStampRequestExtension> extensions = new ArrayList<TimeStampRequestExtension>();
	
	public TimeStampRequestParameters() {
	}
	public TimeStampRequestParameters(String user, String password, String oid, BigInteger nonce) {
		super();
		this.user = user;
		this.password = password;
		this.oid = oid;
		this.nonce = nonce;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public BigInteger getNonce() {
		return nonce;
	}
	public void setNonce(BigInteger nonce) {
		this.nonce = nonce;
	}
	
	public List<TimeStampRequestExtension> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<TimeStampRequestExtension> extensions) {
		this.extensions = extensions;
	}
	
	public void addExtension (TimeStampRequestExtension extension) {
		this.extensions.add(extension);
	}

	@Override
	public String toString() {
		return user + "," + password + "," + oid;
	}

	public class TimeStampRequestExtension {
		private String oid;
		private byte[] value;
		public TimeStampRequestExtension() {
			super();
		}
		public TimeStampRequestExtension(String oid, byte[] value) {
			super();
			this.oid = oid;
			this.value = value;
		}
		public String getOid() {
			return oid;
		}
		public void setOid(String oid) {
			this.oid = oid;
		}
		public byte[] getValue() {
			return value;
		}
		public void setValue(byte[] value) {
			this.value = value;
		}
		
	}
}
