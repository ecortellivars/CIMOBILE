package es.correointeligente.cipostal.cimobile.Util;

import android.content.Context;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import es.correointeligente.cipostal.cimobile.Holders.FicheroViewHolder;

public class FTPHelper {

    private static FTPHelper INSTANCIA = null;
    private Context context;
    private Session session;
    private ChannelSftp channelSftp;
    private Boolean conectado;

    private FTPHelper() {

    }

    public static FTPHelper getInstancia() {
        if(INSTANCIA == null) {
            synchronized (FTPHelper.class) {
                if(INSTANCIA == null) {
                    INSTANCIA = new FTPHelper();
                    INSTANCIA.conectado = Boolean.FALSE;
                }
            }
        }
        return INSTANCIA;
    }

    public Boolean connect(Context context) {
        try {
            this.context = context;
            JSch jsch = new JSch();
            // Carga los datos de conexion al FTP desde las preferencias.xml que esta en la carpeta /res/xml
            String usuario = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_USER, context, String.class.getSimpleName());
            String ipFTP = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_IP, context, String.class.getSimpleName());
            Integer puertoFTP = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_PUERTO, context, Integer.class.getSimpleName());
            String passFTP = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_PASSWORD, context, String.class.getSimpleName());
            Integer timeoutFTP = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_TIMEOUT, context, Integer.class.getSimpleName());

            // Carga los datos de conexion al FTP de CIPOSTAL
            //String usuario = "u79475687";
            //String ipFTP = "home557660407.1and1-data.host";
            //Integer puertoFTP = 22;
            //String passFTP = "abc123.";

            // Preparamos la sesion para conectar con los valores obtenidos en las preferencia
            session = jsch.getSession(usuario,ipFTP, puertoFTP);
            session.setPassword(passFTP);
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            //Conectamos
            session.connect(timeoutFTP);
            conectado = Boolean.TRUE;

        } catch (JSchException e) {
            e.printStackTrace();
        }

        return conectado;
    }

    public Boolean disconnect() {
        Boolean desconectado = Boolean.FALSE;

        try {
            session.disconnect();
            desconectado = Boolean.TRUE;
            this.conectado = Boolean.FALSE;
        } catch (Exception e) {
          e.printStackTrace();
        }

        return  desconectado;
    }

    public Boolean cargarCarpeta(String ruta) {
        Boolean ok = Boolean.FALSE;
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(ruta);
            ok = Boolean.TRUE;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ok;
    }

    public Boolean cargarCarpetaNotificador(String path) {
        Boolean ok = Boolean.FALSE;
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            try {
                channelSftp.mkdir(path);
            } catch (Exception e) {
            }

            ok = Boolean.TRUE;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ok;
    }

    public Boolean subirFichero(File file, String Path) {
        Boolean ok = Boolean.TRUE;
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.cd(Path);
            InputStream stream = new ByteArrayInputStream( FileUtils.readFileToByteArray(file));
            channelSftp.put(stream, file.getName());
        } catch (Exception e) {
            ok = Boolean.FALSE;
        }

        return ok;
    }

    /**
     * Conectamos con el servidor FTp via WIFI y si existe un SICER nos lo bajamos
     * @return
     */
    public List<FicheroViewHolder> obtenerFicherosDirectorio() {
        List<FicheroViewHolder> listaFicheros = new ArrayList<>();

        try {
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.txt");
            list.addAll(channelSftp.ls("*.TXT"));

            for(ChannelSftp.LsEntry entry : list) {
                if(entry.getAttrs().isReg() && FilenameUtils.getExtension(entry.getFilename()).equalsIgnoreCase("txt")) {
                    String nombreFichero = entry.getFilename();
                    String tamanyo = Util.obtenerTamanyoFicheroString(entry.getAttrs().getSize());
                    Date date= new Date(((long)entry.getAttrs().getMTime())*1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String fecha = sdf.format(date);

                    listaFicheros.add(new FicheroViewHolder(nombreFichero, tamanyo, fecha, null));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaFicheros;
    }

    public Boolean isConnected() {
        return conectado;
    }

    public InputStream leerFichero(String nombreFichero) throws SftpException {
        return channelSftp.get(nombreFichero);
    }

    public void descargarFichero(String nombreFichero, String rutaCapeta) throws SftpException {
        // storage/emulated/0/CIMobile/UPDATES_APP
        File file = new File(rutaCapeta);
        if(!file.exists()) {
            file.mkdirs();
        }
        File outputFile = new File(file, nombreFichero);
        if(outputFile.exists()){
            outputFile.delete();
        }
        try (BufferedInputStream  is = new BufferedInputStream(channelSftp.get(nombreFichero));
             OutputStream fos = new FileOutputStream(outputFile);){

            // copia el fichero desde el ftp a la carpeta indicada
            IOUtils.copy(is, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


  /* TODO: PARA PODER HACER CONEXION POR FTP, FTPS IMPLICITO, FTPS EXPLICITO O SFTP
  ftpClient = new FTPClient();
    ftpClient.setConnectTimeout(5000);
//                ftpClient.connect("46.17.141.94",1984);
    ftpClient.connect("192.168.0.105", 23);
    ftpClient.login("jorge", "abc123.");
//                ftpClient.login("valencia", "abc123.");
    ftpClient.changeWorkingDirectory("/SICERS");



//                ftpsClient = new FTPSClient(false);
//                ftpsClient.setConnectTimeout(10000);
//                ftpsClient.connect("192.168.0.105");
//                ftpsClient.login("jorge", "abc123.");
//
//               int  reply = ftpsClient.getReplyCode();
//                FTPReply.isPositiveCompletion(reply);
//                ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
//                ftpsClient.execPROT("P");
//                ftpsClient.changeWorkingDirectory("/SICERS");
//                reply = ftpsClient.getReplyCode();
//                FTPReply.isPositiveCompletion(reply);
//                ftpsClient.enterLocalPassiveMode();

 FTPFile[] arrayFicheros = ftpClient.listFiles();
                List<FicheroViewHolder> listaFicheros = new ArrayList<>();
                for (FTPFile ftpFile : arrayFicheros) {
                    if (ftpFile.isFile() && FilenameUtils.getExtension(ftpFile.getName()).equalsIgnoreCase("txt")) {
                        String nombreFichero = ftpFile.getName();
                        String tamanyo = Util.obtenerTamanyoFicheroString(ftpFile.getSize());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        String fecha = sdf.format(ftpFile.getTimestamp().getTime());

                        listaFicheros.add(new FicheroViewHolder(nombreFichero, tamanyo, fecha, null));
                    }
                }
*/

}
