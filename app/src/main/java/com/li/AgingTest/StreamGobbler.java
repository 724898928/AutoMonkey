package com.li.AgingTest;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class StreamGobbler extends Thread {
    InputStream is;
    OutputStream os;
    String type;
    String path;
    public static final String logcatfile= Environment.getExternalStorageDirectory()+"";
    StreamGobbler(InputStream is, String type,String path) {
        this(is, type,path,null);
    }

    StreamGobbler(InputStream is, String type, String path,OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
        this.path=path;
    }

    public void run() {
        IOException ioe;
        Throwable th;
        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        PrintWriter printWriter = null;
        try {
            if (this.os != null) {
                printWriter = new PrintWriter(this.os);
            }
            InputStreamReader isr = new InputStreamReader(this.is);
            try {
                BufferedReader br2 = new BufferedReader(isr);
                File file = new File(logcatfile+path);
                FileWriter fr = new FileWriter(file);
                while (true) {
                    try {
                        String line = br2.readLine();
                        if (line == null) {
                            break;
                        }
                        if (printWriter != null) {
                            printWriter.println(line);
                        }
                        fr.write(line+"\r\n");
                        System.out.println(this.type + ">" + line);
                    } catch (IOException e) {
                        ioe = e;
                        br = br2;
                        inputStreamReader = isr;
                    } catch (Throwable th2) {
                        th = th2;
                        br = br2;
                        inputStreamReader = isr;
                    }
                }
                if (printWriter != null) {
                    printWriter.flush();
                }
                if (printWriter != null) {
                    try {
                        printWriter.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (br2 != null) {
                    br2.close();
                }
                if (isr != null) {
                    isr.close();
                }
                br = br2;
                fr.close();
            } catch (IOException e3) {
                ioe = e3;
                inputStreamReader = isr;
                try {
                    ioe.printStackTrace();
                    if (printWriter != null) {
                        try {
                            printWriter.close();
                        } catch (Exception e22) {
                            e22.printStackTrace();
                            return;
                        }
                    }
                    if (br != null) {
                        br.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                } catch (Throwable th3) {
                    if (printWriter != null) {
                        try {
                            printWriter.close();
                        } catch (Exception e222) {
                            e222.printStackTrace();
                            throw th3;
                        }
                    }
                    if (br != null) {
                        br.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                    throw th3;
                }
            } catch (Throwable th4) {
                inputStreamReader = isr;
                if (printWriter != null) {
                    printWriter.close();
                }
                if (br != null) {
                    br.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                throw th4;
            }

        } catch (IOException e4) {
            ioe = e4;
            ioe.printStackTrace();
            if (printWriter != null) {
                printWriter.close();
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
