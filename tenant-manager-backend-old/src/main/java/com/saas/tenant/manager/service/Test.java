package com.saas.tenant.manager.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test {

    public void myMethod() {
        String namespaceName = "isydf";
        String directoryPath = "src/main/resources";

        try {


            ProcessBuilder pb = new ProcessBuilder("./src/main/resources/boutique_files/test.sh");
            Process p = pb.start();
//            Process p = Runtime.getRuntime().exec(new String[]{"./src/main/resources/boutique_files/create-namespace.sh", namespaceName, directoryPath});
            InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = null;
            String lastLine = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lastLine = line;
            }
            if (lastLine.contains("http:")) {
                String routeUrl = lastLine.substring(lastLine.indexOf("http:"));
                System.out.println("URL --->" + routeUrl);
            } else {
                System.out.println("URL couldn't fetch from the FreshRSS crd!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Test t = new Test();
        t.myMethod();
    }
}
