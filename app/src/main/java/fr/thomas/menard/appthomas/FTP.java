package fr.thomas.menard.appthomas;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;

public class FTP {
    public FTPClient mFTPClient = null;

    public boolean connect(String host, String username, String password, int port, Uri videoURI, Context context, String patientID, String preset)
    {
        try
        {
            return new asyncConnexion(host, username, password, port, videoURI, context, patientID, preset).execute().get();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public class asyncConnexion extends AsyncTask<Void, Void, Boolean> {
        private final String host;
        private final String username;
        private final String password;
        private final int port;
        private final String patientID;
        private final Uri videoURI;

        private final Context context;

        private final String preset;

        asyncConnexion(String host, String username, String password, int port, Uri videoURI, Context context, String patientID, String preset) {
            this.host = host;
            this.password = password;
            this.port = port;
            this.username = username;
            this.videoURI = videoURI;
            this.context = context;
            this.patientID = patientID;
            this.preset = preset;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                mFTPClient = new FTPClient();
                // connecting to the host
                mFTPClient.connect(host, port);
                showServerReply(mFTPClient);
                int replyCode = mFTPClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    System.out.println("Connect failed");
                    return false;
                }

                boolean status = mFTPClient.login(username, password);

                mFTPClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

                if(status){
                    // Emplacement de la vidéo enregistrée
                    String videoPath = getAbsolutePathFromUri(context, videoURI);

                    Log.d("TEST", "local file path" + videoPath);

                    // Nom de fichier sur le serveur FTP
                    //String remoteFilePath = "./DB/"+ patientID+"/iGAIT/raw/"+preset+"_"+patientID+".mp4";
                    String remoteFilePath = "./DB/"+ patientID+"/iGAIT/raw/";

                    String currentPath = "";
                    for (String segment : remoteFilePath.split("/")) {
                        currentPath += "/" + segment;

                        if (!mFTPClient.changeWorkingDirectory(currentPath)) {
                            // Le répertoire n'existe pas, nous allons le créer
                            boolean success = mFTPClient.makeDirectory(currentPath);
                            if (success) {
                                Log.d("TEST","Created directory: " + currentPath);
                                mFTPClient.changeWorkingDirectory(currentPath);
                            } else {
                                Log.d("TEST","Failed to create directory: " + currentPath);
                                break;
                            }
                        }
                    }

                    mFTPClient.changeWorkingDirectory("./DB/"+ patientID+"/iGAIT/raw/");
                    Log.d("TEST", "wdr"+mFTPClient.printWorkingDirectory());
                    // Charger le fichier sur le serveur
                    FileInputStream localFileStream = new FileInputStream(videoPath);
                    boolean uploaded = mFTPClient.storeFile(patientID+"_"+preset+".mp4", localFileStream);
                    Log.d("TEST", ""+uploaded+ remoteFilePath);
                    // Fermer le flux et déconnecter
                    localFileStream.close();
                    mFTPClient.logout();
                    mFTPClient.disconnect();
                }else{
                    Log.d("TEST","not connected");
                }




                return status;

            } catch (Exception e) {
                Log.d("TEST", "error" + host);
                Log.i("testConnection", "Error: could not connect to host " + host);
                e.printStackTrace();

            }
            return false;

        }

        private void showServerReply(FTPClient ftpClient) {
            String[] replies = ftpClient.getReplyStrings();
            if (replies != null && replies.length > 0) {
                for (String aReply : replies) {
                    System.out.println("SERVER: " + aReply);
                }
            }
        }

        private String getAbsolutePathFromUri(Context context, Uri uri) {
            String absolutePath = null;
            if (uri.getScheme().equals("content")) {
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    absolutePath = cursor.getString(column_index);
                    cursor.close();
                }
            } else if (uri.getScheme().equals("file")) {
                absolutePath = uri.getPath();
            }
            return absolutePath;
        }

    }
}
