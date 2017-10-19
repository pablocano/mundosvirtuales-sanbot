package com.mundos_virtuales.sanbotmv.utils;

import android.os.Environment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;


public class RequestWatson implements Runnable {

    byte[] response;
    ConcurrentLinkedQueue<String> m_queueVoice;
    private static int m_nWav = 0;

    public RequestWatson(byte[] response, ConcurrentLinkedQueue<String> queueVoice){
        this.response = response;
        this.m_queueVoice = queueVoice;
    }

    private void saveWAV() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) &&
                !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            File pathToExternalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String filename = pathToExternalStorage.getAbsolutePath().toString() + "/test-" + (++m_nWav) + ".wav";

            try {
                FileOutputStream fis = new FileOutputStream (new File(filename));
                fis.write(this.response);
                fis.flush();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        //saveWAV();

        String result = getResponseWatson(this.response);
        final Gson gson = new Gson();
        ResponseWatson response = gson.fromJson(result, ResponseWatson.class);

        synchronized (m_queueVoice) {
            if(response.getConfidence() > 0.5) {
                m_queueVoice.add(response.getOutput());
                m_queueVoice.notifyAll();
            }
        }
    }

    public String getResponseWatson(byte[] bytes) {
        HttpsURLConnection client = null;
        String ret = "";
        String attachmentName = "test";
        String attachmentFileName = "wav";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        try {

            URL url = new URL("https://sanbotmv1.mybluemix.net/sanbot");
            client = NetCipher.getHttpsURLConnection(url);

            client.setRequestMethod("POST");
            client.setUseCaches(false);
            client.setDoOutput(true);
            client.setDoInput(true);

            // Send WAV to Watson
            client.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream request = new DataOutputStream(
                    client.getOutputStream());
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);
            request.write(bytes);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);
            request.flush();
            request.close();

            // Receive Watson response
            client.connect();

            InputStream is = client.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            ret = response.toString();

        } catch(MalformedURLException error) {
            ret = "Error: Handles an incorrectly entered URL, " + error.toString();
        }
        catch(SocketTimeoutException error) {
            ret = "Error: Handles URL access timeout, " + error.toString();
        }
        catch (IOException error) {
            ret = "Error: Handles input and output errors, " + error.toString();
        }
        catch (Exception error){
            ret = "Error: " + error.toString();
        }
        finally {
            if(client != null)
                client.disconnect();
        }

        return ret;
    }

}
