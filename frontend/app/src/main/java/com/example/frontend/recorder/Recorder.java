package com.example.frontend.recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.example.frontend.info.UserInfo;
import com.example.frontend.utils.FileManager;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class Recorder {
    private Context context;
    private String chatid;

    private static volatile Recorder mInstance;//单利引用
    private int SamplingRate = 48000;
    //格式：双声道
    private int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
    //16Bit
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //是否在录制
    private boolean isRecording = false;
    //每次从audiorecord输入流中获取到的buffer的大小
    private int bufferSize = 0;
    private boolean lock = false;

    public static final int FILE_RESULT_CODE = 1;
    private volatile String musicFileName = "";
    private MediaPlayer player = null;

    private Recorder(Context context){
        this.context = context;
        this.chatid = "";
    }

    public void setChatid(String chatid){
        this.chatid = chatid;
    }

    public synchronized void setMusicFileName(String name){
        this.musicFileName=name;
    }
    public synchronized String getMusicFileName(){
        return this.musicFileName;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public static Recorder getInstance(Context context) {
        Recorder inst = mInstance;
        if (inst == null) {
            synchronized (Recorder.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new Recorder(context);
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    public void record() {
//        try {
//            SamplingRate = Integer.parseInt(SamplingRateText.getText().toString());
//        } catch (NumberFormatException e) {
//            Toast.makeText(getApplicationContext(),
//                    "错误的采样频率格式！请输入正整数。",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
        //恢复停止录音按钮，并禁用开始录音按钮

        if(chatid.equals("")){
            return;
        }
        if (player != null) {
            player.release();
            player = null;
        }
        Toast.makeText(context,
                "开始录音",
                Toast.LENGTH_SHORT).show();
        lock = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                    //获取此刻的时间
                    Date now = Calendar.getInstance().getTime();
                    //设置用于临时保存录音原始数据的文件的名字
                    String name = context.getExternalFilesDir(UserInfo.getInstance().getUsername()).getAbsolutePath() + "/raw.wav";
                    //调用开始录音函数，并把原始数据保存在指定的文件中
                    StartRecordWav(name);

                    //用此刻时间为最终的录音wav文件命名
                    String filepath = FileManager.getInstance()
                            .getUserFileAbsolutePath(context, chatid + "/" + now.toString() + ",wav");
                    //把录到的原始数据写入到wav文件中。
                    copyWaveFile(name, filepath);
                    setMusicFileName(filepath);
                    Log.e("musicFileName", getMusicFileName());
                    lock = false;
            }
        });
        //开启线程
        thread.start();
    }

    public String stopRecord() throws InterruptedException {
        //停止录音
        isRecording = false;

        Date now = Calendar.getInstance().getTime();
        Toast.makeText(context,
                String.format("录音完成，正在发送", now.toString()),
                Toast.LENGTH_SHORT).show();
        Log.e("wenjianming",now.toString());
        while(lock){
            Thread.sleep(100);
        }
        return getMusicFileName();
    }

    public void StartRecordWav(String name) {
        //生成原始数据文件
        File dir = new File(context.getExternalFilesDir(UserInfo.getInstance().getUsername()).getAbsolutePath());
        if (!dir.exists()) System.out.println(dir.mkdir());
        else System.out.println("Directory already exists");
        File file = new File(name);
        //如果文件已经存在，就先删除再创建
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("未能创建" + file.toString());
        }
        try {
            //文件输出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            //获取在当前采样和信道参数下，每次读取到的数据buffer的大小
            bufferSize = AudioRecord.getMinBufferSize(SamplingRate, channelConfiguration, audioEncoding);
            //建立audioRecord实例
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SamplingRate, channelConfiguration, audioEncoding, bufferSize);

            //设置用来承接从audiorecord实例中获取的原始数据的数组
            byte[] buffer = new byte[bufferSize];
            //启动audioRecord
            audioRecord.startRecording();
            //设置正在录音的参数isRecording为true
            isRecording = true;
            //只要isRecording为true就一直从audioRecord读出数据，并写入文件输出流。
            //当停止按钮被按下，isRecording会变为false，循环停止
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.write(buffer[i]);
                }
            }
            //停止audioRecord，关闭输出流
            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {
            Log.e("MainActivity", "录音失败");
        }
    }

    private void copyWaveFile(String inFileName, String outFileName)
    {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        //wav文件比原始数据文件多出了44个字节，除去表头和文件大小的8个字节剩余文件长度比原始数据多36个字节
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = SamplingRate;
        int channels = 2;
        //每分钟录到的数据的字节数
        long byteRate = 16 * SamplingRate * channels / 8;

        byte[] data = new byte[bufferSize];
        try
        {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);
            //获取真实的原始数据长度
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            //为wav文件写文件头
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            //把原始数据写入到wav文件中。
            while(in.read(data) != -1)
            {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // WAV type format = 1
        header[21] = 0;
        header[22] = (byte) channels; //指示是单声道还是双声道
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff); //采样频率
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff); //每分钟录到的字节数
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff); //真实数据的长度
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        //把header写入wav文件
        out.write(header, 0, 44);
    }

    public void play(String musicFileName){
        if (player == null) {
            if(!setNewPlayer(musicFileName)) return;
        }
        // play music
        Log.i("zhengque","zhengque");
        player.start();
    }

    private boolean setNewPlayer(String musicFileName){
        File musicFile = new File(musicFileName);
        if (!musicFile.exists()) {
            Toast.makeText(context,
                    "文件不存在！",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // new a player
        player = new MediaPlayer();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(musicFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            player.setDataSource(fis.getFD());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 音乐播放完成后的操作
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (player != null) {
                    player.release();
                    player = null;
                }

            }
        });
        return true;
    }

}
