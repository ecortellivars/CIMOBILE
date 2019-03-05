package es.correointeligente.cipostal.cimobile.TSA;

import android.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cmp.PKIFailureInfo;
import org.spongycastle.tsp.TSPException;
import org.spongycastle.tsp.TimeStampRequest;
import org.spongycastle.tsp.TimeStampRequestGenerator;
import org.spongycastle.tsp.TimeStampResponse;
import org.spongycastle.tsp.TimeStampToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import es.correointeligente.cipostal.cimobile.Util.CiMobileException;

public class TimeStamp {

    private static final Provider CRYPTOGRAPHIC_PROVIDER = new org.spongycastle.jce.provider.BouncyCastleProvider();
    private TimeStampToken timeStamp;

    /**
     * Constructor de un TimeStamp a partir de un TimeStampToken
     * @param timeStamp
     */
    public TimeStamp (TimeStampToken timeStamp) {

        this.timeStamp = timeStamp;
    }

    /**
     * Devuelve la fecha y la hora en la que se estampó el sello de tiempo.
     */
    public Date getTime() {
        return timeStamp.getTimeStampInfo().getGenTime();
    }

    /**
     * Obtiene el hash que fue sellado por la TSA
     * @return Hash sellado por la TSA
     */
    public byte[] getHash () {
        return timeStamp.getTimeStampInfo().getMessageImprintDigest();
    }

    /**
     * Obtiene el OID del algoritmo de hashing con el que fue obtenido el hash que
     * selló la TSA
     * @return OID del algoritmo de hashing con el que se creó el hash sellado por la TSA
     */
    public String getHashAlgorithm () {
        return timeStamp.getTimeStampInfo().getMessageImprintAlgOID().getId();
    }

    /**
     * Obtiene el algoritmo de hashing con el que fue obtenido el hash que
     * selló la TSA
     * @return Nombre del algoritmo de hashing con el que se creó el hash sellado por la TSA
     */
    public String getHashAlgorithmName () throws NoSuchAlgorithmException {
        return HashingAlgorithm.getAlgorithmName(timeStamp.getTimeStampInfo().getMessageImprintAlgOID().getId());
    }

    /**
     * Obtiene el OID de la política con la que se realizó el firmado
     * @return OID de la política
     */
    public String getPolicyOID () {
        return timeStamp.getTimeStampInfo().getPolicy().getId();
    }

    /**
     * Obtiene el campo TSA
     * @return TSA
     */
    public String getTSA () {
        String tsa = null;
        if (timeStamp.getTimeStampInfo() != null && timeStamp.getTimeStampInfo().getTsa() != null) {
            tsa = timeStamp.getTimeStampInfo().getTsa().toString();
        }

        return tsa;
    }

    /**
     * Obtiene el sello en formato DER
     *
     * @return Array de bytes con el sello de tiempos
     */
    public byte[] toDER () {
        byte[] der = null; //array de bytes con el sello de tiempo
        try {
            der = timeStamp.getEncoded();
        } catch (IOException e) {
        }
        return  der;
    }

    /**
     * Implementacion del comparable
     * @param ts
     * @return
     */
    public int compareTo(TimeStamp ts) {
        return getTime().compareTo(ts.getTime());
    }

    /**
     *
     * @param bDocument Contenido del documento que se desea sellar
     * @param serverTimeStampURL URL del servidor de sello de tiempos
     * @param parameters Parámetros de la llamada al sello de tiempos
     * @return TimeStamp
     * @throws Exception
     */
    public static TimeStamp stampDocument (byte[] bDocument, URL serverTimeStampURL, TimeStampRequestParameters parameters, String hashAlgorithm) throws CiMobileException {
        //String userPassword = "cipostaluser:8ttErr32";
        // Si no se le pasa ningun algoritmo, se coge el de por defecto
        if(hashAlgorithm == null) {
            hashAlgorithm = HashingAlgorithm.getDefault();
        }

        // Se genera el hash correspondiente al documento el cual queremos sellar
        InputStream bais = new ByteArrayInputStream(bDocument);

        // Hacer hash por partes mediante el inputStream
        byte[] hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm, CRYPTOGRAPHIC_PROVIDER);
            DigestInputStream dis = new DigestInputStream(bais, md);
            byte[] buffer = new byte[512];
            while (true) {
                int n;
                n = dis.read(buffer);
                if (n < 0)
                    break;
            }
            dis.on(false);

            hash = dis.getMessageDigest().digest();

        } catch (NoSuchAlgorithmException e) {
            throw new CiMobileException("No existe en el sistema el algoritmo de hashing '" + hashAlgorithm + "'");
        } catch (IOException e) {
            throw new CiMobileException ("Excepción leyendo el stream de lectura");
        }

        // Obtener el generador de peticiones a la TSA
        TimeStampRequestGenerator tsrGenerator = new TimeStampRequestGenerator();
        tsrGenerator.setCertReq(true);

        // Añadir parámetros
        if (parameters != null) {
            if (parameters.getOid() != null) {
                tsrGenerator.setReqPolicy(new ASN1ObjectIdentifier(parameters.getOid()));
            }

            // Obtener las extensiones
            if (parameters.getExtensions() != null && !parameters.getExtensions().isEmpty()) {
                for(TimeStampRequestParameters.TimeStampRequestExtension extension : parameters.getExtensions()) {
                    tsrGenerator.addExtension(new ASN1ObjectIdentifier(extension.getOid()), false, extension.getValue());
                }
            }
        }

        // Obtener el algoritmo por el tamaño del hash
        String hashingAlgorithmOID = null;
        try {
            hashingAlgorithmOID = HashingAlgorithm.getOID(hashAlgorithm);

            switch (hash.length) {
                case 20:
                    hashingAlgorithmOID = HashingAlgorithm.getOID(HashingAlgorithm.SHA1);
                    break;
                case 32:
                    hashingAlgorithmOID = HashingAlgorithm.getOID(HashingAlgorithm.SHA256);
                    break;
                case 48:
                    hashingAlgorithmOID = HashingAlgorithm.getOID(HashingAlgorithm.SHA384);
                    break;
                case 64:
                    hashingAlgorithmOID = HashingAlgorithm.getOID(HashingAlgorithm.SHA512);
            }
        } catch (NoSuchAlgorithmException e) {
        }

        //-- Obtener la petición
        BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
        if (parameters != null && parameters.getNonce() != null) {
            nonce = parameters.getNonce();
        }

        // Se obtiene la peticion de sellado
        TimeStampRequest timeStampRequest = tsrGenerator.generate(new ASN1ObjectIdentifier(hashingAlgorithmOID), hash, nonce);

        byte[] requestBytes = null;
        try {
            requestBytes = timeStampRequest.getEncoded();
        } catch (IOException e) {

        }

        //-- Conectar a la TSA y obtener la respuesta
        Hashtable reqProperties = new Hashtable();

        if (parameters != null && StringUtils.isNotBlank(parameters.getUser()) && StringUtils.isNotBlank(parameters.getPassword())) {
            String userPassword = parameters.getUser() + ":" + parameters.getPassword();
            String reqString = "Basic " + new String(Base64.encode(userPassword.getBytes(), Base64.NO_WRAP));
            reqProperties.put("Authorization", reqString);
//            reqProperties.put("Content-Length", String.valueOf(reqString.length()));
        }

        reqProperties.put("Content-Type", "application/timestamp-query");
        reqProperties.put("Content-Transfer-Encoding", "binary");

        TimeStampResponse response = connect(serverTimeStampURL, reqProperties, requestBytes);

        // Validar la respuesta
        if (response.getTimeStampToken()== null) {
            if (response.getFailInfo() == null) {
                throw new CiMobileException("Por algún motivo desconocido la TSA no ha podido devolver un sello de tiempos");
            } else {
                switch (response.getFailInfo().intValue()) {
                    case PKIFailureInfo.badAlg:
                        throw new CiMobileException("El algoritmo del hashing no está reconocido por la TSA");
                    case PKIFailureInfo.badRequest:
                        throw new CiMobileException("La TSA considera que la petición no es correcta");
                    case PKIFailureInfo.badDataFormat:
                        throw new CiMobileException("La información a sellar tiene un formato incorrecto");
                    case PKIFailureInfo.timeNotAvailable:
                        throw new CiMobileException("En estos momentos la fuente de tiempos de la TSA no se encuentra disponible");
                    case PKIFailureInfo.unacceptedPolicy:
                        throw new CiMobileException("La polótica pedida a la TSA no es aceptada por ésta");
                    case PKIFailureInfo.unacceptedExtension:
                        throw new CiMobileException("La extensión pedida no es aceptada por la TSA");
                    case PKIFailureInfo.addInfoNotAvailable:
                        throw new CiMobileException("La información adicional pedida no es entendida o no está disponible en la TSA");
                    case PKIFailureInfo.systemFailure:
                        throw new CiMobileException("La respuesta no puede ser atendida debido a un error interno del servidor");
                    default:
                        throw new CiMobileException("Por algún motivo desconocido la TSA no ha podido devolver un sello de tiempos");
                }
            }
        }

        return new TimeStamp(response.getTimeStampToken());
    }

    /*
	 * Se conecta a una TSA, le pasa la petición con las propiedades reqProperties y
	 * obtiene una respuesta
	 */
    private static TimeStampResponse connect(URL serverTimeStampURL, Hashtable reqProperties, byte[] requestBytes) throws CiMobileException {

        HttpURLConnection urlConn = null;
        TimeStampResponse timeStampResponse = null;

        // Abrir la conexión a la URL
        try {
            urlConn = (HttpURLConnection) serverTimeStampURL.openConnection();
        } catch(IOException e) {
            throw new CiMobileException("Fallo al abrir la conexión a la URL");
        }
            // La conexión será en las dos direcciones
        try {
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");

            // No usar caché
            urlConn.setUseCaches(false);
        } catch (ProtocolException e){
            throw new CiMobileException("Fallo de protocolo de petición");
        }

        // Propiedades de la petición
        Iterator iter = reqProperties.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if(entry.getKey().equals("Authorization")) {
                urlConn.setChunkedStreamingMode(entry.getValue().toString().getBytes().length);
                urlConn.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            } else {
                urlConn.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }

        }

        try (OutputStream printout = urlConn.getOutputStream();){
            // Escribir la petición
            printout.write(requestBytes);
            printout.flush();
        } catch(IOException e) {
            throw new CiMobileException("Fallo al escribir la petición sobre la conexión");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataInputStream input = new DataInputStream(urlConn.getInputStream());) {
            // Obtener la respuesta
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                baos.write(buffer, 0, bytesRead);
            }
            timeStampResponse = new TimeStampResponse (baos.toByteArray());

        } catch(IOException e) {
            throw new CiMobileException("Fallo de lectura en la respuesta");
        }catch (TSPException e) {
            throw new CiMobileException("La respuesta del servidor no parece una respuesta de sello de tiempos");
        }

        return timeStampResponse;
    }

}
