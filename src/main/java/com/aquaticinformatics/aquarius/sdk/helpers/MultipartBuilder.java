package com.aquaticinformatics.aquarius.sdk.helpers;

import java.io.*;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

public class MultipartBuilder {

    public String boundaryMarker;

    private static final String LineFeed = "\r\n";

    private ByteArrayOutputStream outputStream;

    public MultipartBuilder() {
        boundaryMarker = "****" + UUID.randomUUID().toString() + "****";

        outputStream = new ByteArrayOutputStream();
    }

    public void addField(String fieldName, String value) {
        writeFieldBoundary();
        writeLine("Content-Disposition: form-data; name=\"" + fieldName + "\"");
        writeLine("Content-Type: text/plain; charset=UTF-8");
        writeLine("");
        writeLine(value);
    }

    public void addFileContent(String fieldName, String fileName, InputStream content) {
        writeFieldBoundary();
        writeLine("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"");
        writeLine("Content-Type: application/octet-stream");
        writeLine("");

        try {
            outputStream.write(net.servicestack.client.Utils.readBytesToEnd(content));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeLine("");
    }

    private void writeFieldBoundary() {
        writeLine("--" + boundaryMarker);
    }

    private void writeLine(String message)  {
        try {
            byte[] bytes = (message + LineFeed).getBytes("UTF-8");
            outputStream.write(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        writeLine("");
        writeLine("--" + boundaryMarker + "--");
    }

    public byte[] toByteArray() {
        return outputStream.toByteArray();
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundaryMarker;
    }
}
