package com.wjs.expr.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.*;
import org.apache.commons.lang.StringUtils;

/**
 * @author wjs
 * @date 2020-01-15 11:08
 **/
public class Copy {

    public static char[] WS = new char[]{' ', '\r', '\n', '\t'};

    public static List<String> ignore = Arrays.asList("MvelEval.java", "NutzEval.java", "QLExprEval.java", "ColValueFunc.java", "StrFunc.java", "TestFunc.java");

    public static void delComment(String srcDir, String descDir, String srcPkg, String descPkg) throws IOException {
        File srcFile = new File(srcDir);
        File descFile = new File(descDir);
        descFile.delete();
        descFile.mkdirs();
        for (File f : srcFile.listFiles()){
            if (f.isFile()){
                out(f.getAbsolutePath(), descDir+f.getName(), srcPkg, descPkg);
            }else if (f.isDirectory()){
                String descTmp = descDir+f.getName()+"/";
                new File(descTmp).mkdirs();
                for (File file : f.listFiles()){
                    if (!ignore.contains(file.getName())){
                        out(file.getAbsolutePath(), descTmp+file.getName(), srcPkg, descPkg);
                    }
                }
            }
        }
    }

    public static void out(String input, String outPut, String srcPkg, String descPkg) throws IOException {
        out(new File(input), new File(outPut), srcPkg, descPkg);
    }

    public static void out(File input, File output, String srcPkg, String descPkg) throws IOException {
        String srcText = FileUtils.readFileToString(input, "utf-8");
        StringBuilder sb = new StringBuilder(srcText.length());
        boolean block = false;
        for (String line : srcText.split("\n")){
            if (block){
                if (trim(line).endsWith("*/")){
                    block = false;
                }
                continue;
            }
            if (trim(line).startsWith("//")){
                continue;
            }
            if (trim(line).startsWith("/*")){
                block = true;
                continue;
            }
            sb.append(line.replaceAll(srcPkg, descPkg)+"\n");
        }
        FileUtils.writeStringToFile(output, sb.toString(), "utf-8");
    }

    /**
     * 是否空格符
     * @param c
     * @return
     */
    public static boolean isWS(char c) {
        for (char c1 : WS){
            if (c == c1){
                return true;
            }
        }
        return false;
    }

    /**
     * 删除前后空格
     * @param sql
     * @return
     */
    public static String trim(String sql){
        if (StringUtils.isBlank(sql)){
            return "";
        }
        while (isWS(sql.charAt(0))){
            sql = sql.substring(1);
        }
        while (isWS(sql.charAt(sql.length()-1))){
            sql = sql.substring(0, sql.length()-1);
        }
        return sql;
    }

    public static void main(String[] args) throws IOException {
        delComment("/home/wjs/code/github/exprTool/src/main/java/com/wjs/expr", "/home/wjs/code/onemtdata/dsl/dsl-parse/dsl-expr/src/main/java/com/onemt/etl/dsl/expr/", "com.wjs.", "com.onemt.etl.dsl.");
    }
}
