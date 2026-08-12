package iaroslav.baranov.tracklog.ast.term;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Stream implements Term{
    InputStream inputStream;
    OutputStream outputStream;
    Reader reader;

    public Stream(InputStream inputStream) {
        this.inputStream = inputStream;
        reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    public void close(){
        if(inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int read() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPrincipalFunctor() {
        return toCode() + "/0";
    }

    @Override
    public String toCode() {
        return "<stream>(" + hashCode() + ")";
    }

    @Override
    public boolean contains(String v) {
        return false;
    }

    @Override
    public Term substitute(String varName, Term term) {
        return this;
    }

    @Override
    public Term substitute(Map<String, Term> map) {
        return this;
    }
}
