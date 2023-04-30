package com.example.amplitude_test_android;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import linc.com.amplituda.Amplituda;
import linc.com.amplituda.AmplitudaProcessingOutput;
import linc.com.amplituda.AmplitudaProgressListener;
import linc.com.amplituda.AmplitudaResult;
import linc.com.amplituda.Cache;
import linc.com.amplituda.Compress;
import linc.com.amplituda.InputAudio;
import linc.com.amplituda.ProgressOperation;
import linc.com.amplituda.callback.AmplitudaErrorListener;
import linc.com.amplituda.exceptions.AmplitudaException;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://file-examples.com/storage/fe644084cb644d3709528c4/2017/11/file_example_MP3_1MG.mp3";
        new DownloadFileTask(this).execute(url);
    }

    private static class DownloadFileTask extends AsyncTask<String, Void, AmplitudaResult<?>> {
        private WeakReference<MainActivity> activityReference;

        DownloadFileTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityReference.get();
            if (activity != null) {
                // Update UI to show progress bar or loading indicator
            }
        }

        @Override
        protected AmplitudaResult<?> doInBackground(String... urls) {
            String url = urls[0];
            Amplituda amplituda = new Amplituda(activityReference.get());
            amplituda.setLogConfig(Log.ERROR, true);
            return amplituda.processAudio(
                    url,
                    Compress.withParams(Compress.AVERAGE, 1),
                    Cache.withParams(Cache.REUSE),
                    null
            ).get();
        }

        @Override
        protected void onPostExecute(AmplitudaResult<?> result) {
            super.onPostExecute(result);
            MainActivity activity = activityReference.get();
            if (activity != null) {
                activity.printResult(result);
                // Update UI to display result
            }
        }
    }

    private void printResult(AmplitudaResult<?> result) {
        System.out.printf(Locale.US,
                "Audio info:\n" +
                        "millis = %d\n" +
                        "seconds = %d\n\n" +
                        "source = %s\n" +
                        "source type = %s\n\n" +
                        "Amplitudes:\n" +
                        "size: = %d\n" +
                        "list: = %s\n" +
                        "amplitudes for second 1: = %s\n" +
                        "json: = %s\n" +
                        "single line sequence = %s\n" +
                        "new line sequence = %s\n" +
                        "custom delimiter sequence = %s\n%n",
                result.getAudioDuration(AmplitudaResult.DurationUnit.MILLIS),
                result.getAudioDuration(AmplitudaResult.DurationUnit.SECONDS),
                result.getInputAudioType() == InputAudio.Type.FILE ? ((File) result.getAudioSource()).getAbsolutePath() : result.getAudioSource(),
                result.getInputAudioType().name(),
                result.amplitudesAsList().size(),
                Arrays.toString(result.amplitudesAsList().toArray()),
                Arrays.toString(result.amplitudesForSecond(1).toArray()),
                result.amplitudesAsJson(),
                result.amplitudesAsSequence(AmplitudaResult.SequenceFormat.SINGLE_LINE),
                result.amplitudesAsSequence(AmplitudaResult.SequenceFormat.NEW_LINE),
                result.amplitudesAsSequence(AmplitudaResult.SequenceFormat.SINGLE_LINE, " * ")
        );
    }
}
