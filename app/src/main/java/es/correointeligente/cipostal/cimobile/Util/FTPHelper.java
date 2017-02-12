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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
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

    public void inicializa(Context context) {
        this.context = context;
    }

    public Boolean connect() {
        try {
            JSch jsch = new JSch();
//            session = jsch.getSession("valencia","46.17.141.94", 1984);
//            session.setPassword("9ca174324c");
            session = jsch.getSession("jorge","192.168.0.105", 23);
            session.setPassword("abc123.");

            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect(10000);

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

    public Boolean cargarCarpetaSICER() {
        Boolean ok = Boolean.FALSE;
        try {

            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd("/SICERS");
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

    public List<FicheroViewHolder> obtenerFicherosDirectorio() {
        List<FicheroViewHolder> listaFicheros = new ArrayList<>();

        try {
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.txt");

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
