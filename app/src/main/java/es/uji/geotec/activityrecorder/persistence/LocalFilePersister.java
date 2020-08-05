package es.uji.geotec.activityrecorder.persistence;

import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.uji.geotec.activityrecorder.model.AccelerometerSensorRecord;
import es.uji.geotec.activityrecorder.model.ActivityEnum;

public class LocalFilePersister{

    private final String BASE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + File.separator + "ActivityRecorder";
    private final String[] HEADER = {"timestamp", "x", "y", "z"};
    private final String PATTERN = "dd-MM-yyyy_HH:mm:ss";

    private ActivityEnum activity;
    private String fileName;

    public LocalFilePersister(ActivityEnum activityEnum) {
        this.activity = activityEnum;
    }

    public String saveSensorRecords(List<AccelerometerSensorRecord> records) {
        try(CSVWriter writer = getWriter()){
            for (AccelerometerSensorRecord record : records) {
                saveSensorRecord(writer, record);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return fileName;
    }

    private void saveSensorRecord(CSVWriter writer, AccelerometerSensorRecord r){
        String[] record = {
                parseToString(r.getTimestamp()),
                parseToString(r.getX()),
                parseToString(r.getY()),
                parseToString(r.getZ())
        };
        writer.writeNext(record);
    }

    private CSVWriter getWriter() throws IOException {
        createDirIfNeeded(BASE_DIR);
        String activityPath = BASE_DIR + File.separator + activity;
        createDirIfNeeded(activityPath);

        fileName = getFileName(activityPath);
        File recordsFile = new File(fileName);
        boolean recordsFileExists = recordsFile.exists();

        FileWriter fileWriter = null;
        fileWriter = new FileWriter(recordsFile, true);

        CSVWriter writer = new CSVWriter(fileWriter, ',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        if (!recordsFileExists) {
            writer.writeNext(HEADER);
        }

        return writer;
    }

    private void createDirIfNeeded(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private String getFileName(String activityPath) {
        String date = new SimpleDateFormat(PATTERN).format(new Date());
        return activityPath + File.separator + date + ".csv";
    }

    private <T> String parseToString(T value) {
        return String.valueOf(value);
    }
}