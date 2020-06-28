package AST;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Main {
    public static String javaRoot = "D:\\soot\\src\\main\\java";
    public static ArrayList<String> getReleaseCommitIDs() throws IOException, InterruptedException {
        ArrayList<String> tags = getOutputFromCmd("git tag --sort=taggerdate");
        return tags;
    }

    public static ArrayList<String> getCommitIDs(String cmd) throws IOException, InterruptedException {
        String command = "c:\\windows\\system32\\cmd.exe /c ";
        File dir = new File("D:\\soot");
        ArrayList<String> res = new ArrayList<>();
        Process p = Runtime.getRuntime().exec(cmd,null,dir);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String readLine = br.readLine();
            while (readLine != null) {
                if(readLine.startsWith("commit")) {
                    String[] temp = readLine.split(" ");
                    res.add(temp[1]);
                }
                readLine = br.readLine();
            }

            p.waitFor();
            int i = p.exitValue();
            if (i == 0) {
                return res;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static boolean runCmd(String cmd) throws IOException, InterruptedException {
        String command = "c:\\windows\\system32\\cmd.exe /c ";
        File dir = new File("D:\\soot");
        ArrayList<String> res = new ArrayList<>();
        Process p = Runtime.getRuntime().exec(cmd,null,dir);
        BufferedReader br = null;
        try {
            p.waitFor();
            int i = p.exitValue();
            if (i == 0) {
                return true;
            } else {
                return false;
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static ArrayList<String> getOutputFromCmd(String cmd) throws IOException, InterruptedException {
        String command = "c:\\windows\\system32\\cmd.exe /c ";
        File dir = new File("D:\\soot");
        ArrayList<String> res = new ArrayList<>();
        Process p = Runtime.getRuntime().exec(cmd,null,dir);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String readLine = br.readLine();
            while (readLine != null) {
                res.add(readLine);
                readLine = br.readLine();
            }
            p.waitFor();
            int i = p.exitValue();
            if (i == 0) {
                return res;
            } else {
                return null;
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static int calDistance(String str1, String str2){
        int[][] cal = new int[str1.length()+1][str2.length()+1];
        //初始化
        for (int i = 0; i < str2.length()+1; i++) {
            cal[0][i] = i;
        }
        for (int i = 0; i < str1.length()+1; i++) {
            cal[i][0] = i;
        }
        //根据递归方程自底向上计算
        for (int i = 1; i < str1.length()+1; i++) {
            for (int j = 1; j < str2.length()+1; j++) {
                if(str1.charAt(i-1) == str2.charAt(j-1)){
                    cal[i][j] = cal[i-1][j-1];
                }else{
                    cal[i][j] = Math.min(Math.min(cal[i-1][j-1], cal[i-1][j]), cal[i][j-1])+1;
                }
            }
        }
        return cal[str1.length()][str2.length()];
    }

    public static int computeLevenshteinDistance_Optimized(CharSequence src,CharSequence dst){
        int lenSrc = src.length() + 1;
        int lenDst = dst.length() + 1;

        CharSequence newSrc = src;
        CharSequence newDst = dst;
        //如果src长度比dst的短，表示数组的列数更多，此时我们
        //交换二者的位置，使得数组的列数变为较小的值。
        if (lenSrc < lenDst){
            newSrc = dst;
            newDst = src;
            int temp = lenDst;
            lenDst = lenSrc;
            lenSrc = temp;
        }

        //创建滚动数组，此时列数为lenDst，是最小的
        int[] cost = new int[lenDst];   //当前行依赖的上一行数据
        int[] newCost = new int[lenDst];//当前行正在修改的数据

        //对第一行进行初始化
        for(int i = 0;i < lenDst;i++)
            cost[i] = i;

        for(int i = 1;i < lenSrc;i++){
            //对第一列进行初始化
            newCost[0] = i;

            for(int j = 1;j < lenDst;j++){
                int flag = (newDst.charAt(j - 1) == newSrc.charAt(i - 1)) ? 0 : 1;

                int cost_insert = cost[j] + 1;        //表示“上面”的数据，即对应d(i - 1,j)
                int cost_replace = cost[j - 1] + flag;//表示“左上方的数据”，即对应d(i - 1,j - 1)
                int cost_delete = newCost[j - 1] + 1; //表示“左边的数据”，对应d(i,j - 1)

                newCost[j] = minimum(cost_insert,cost_replace,cost_delete); //对应d(i,j)
            }

            //把当前行的数据交换到上一行内
            int[] temp = cost;
            cost = newCost;
            newCost = temp;
        }

        return cost[lenDst - 1];
    }

    private static int minimum(int cost_insert, int cost_replace, int cost_delete) {
        int tempMin = cost_insert > cost_replace ? cost_replace : cost_insert;
        return tempMin > cost_delete ? cost_delete : tempMin;
    }

    public static void getAllPackageName(String path, ArrayList<String> res){
        File f = new File(path);
        if(f.exists()){
            //是否目录
            if(f.isDirectory()){
                File[] children = f.listFiles();
                boolean hasJavaFile = false;
                for(File ff : children){
                    if(ff.isFile() && ff.getName().endsWith(".java")){
                        hasJavaFile = true;
                        break;
                    }
                }
                if(f.getAbsolutePath() != javaRoot && hasJavaFile) {
                    String relPath = f.getAbsolutePath().replace(javaRoot + "\\", "");
                    String[] temp = relPath.split("\\\\");
                    String packageName = "";
                    for (int i = 0; i < temp.length; i++)
                        packageName += temp[i] + ".";
                    res.add(packageName.substring(0, packageName.length() - 1));
                }

                for(File ff : children){
                    getAllPackageName(ff.getAbsolutePath(), res);
                }
            }
        }
    }

    public static HashMap<String, String> getPackageASTString(String packageName){
        String[] levels = packageName.split("\\.");
        String packagePath = javaRoot;
        for(String s : levels)
            packagePath += "\\" + s;
        File f = new File(packagePath);
        HashMap<String,String> ASTString = new HashMap<>();
        if(f.exists() && f.isDirectory())
            for(File javaFile : f.listFiles()) {
                if (javaFile.isFile() && javaFile.getName().endsWith(".java")) {
                    ASTString.put(javaFile.getName(), ASTGenerator.getCompilationUnit(javaFile.getAbsolutePath()));
                }
            }

        return ASTString;
    }
    public static HashMap<String, HashMap<String, String>> generateASTStrings(String commitID, ArrayList<String> packageNames) throws IOException, InterruptedException {
        runCmd("git reset --hard " + commitID);
        HashMap<String, HashMap<String, String>> ASTStrings = new HashMap<>();
        for(String name : packageNames)
            ASTStrings.put(name, getPackageASTString(name));
        return ASTStrings;
    }

    static void compareTwoCommits(ArrayList<String> packageNames, String id1, String id2) throws IOException, InterruptedException{
        //String id1 = ids.get(i);
        HashMap<String, HashMap<String, String>> astStrings1 = generateASTStrings(id1, packageNames);
        //String id2 = ids.get(i + 1);
        HashMap<String, HashMap<String, String>> astStrings2 = generateASTStrings(id2, packageNames);

        HashMap<String, Double> sims = new HashMap<>();
        for(String name : packageNames){
            HashMap<String, String> ast1 = astStrings1.get(name);
            HashMap<String, String> ast2 = astStrings2.get(name);
            Set<String> javaClasses = new HashSet<>();
            javaClasses.addAll(ast1.keySet());
            javaClasses.addAll(ast2.keySet());
            int maxlen = 0;
            int totalDist = 0;
            for(String javaClass : javaClasses){
                String astStr1 = ast1.getOrDefault(javaClass, "");
                String astStr2 = ast2.getOrDefault(javaClass, "");
                int distance = computeLevenshteinDistance_Optimized(astStr1, astStr2);
                totalDist += distance;
                maxlen += Math.max(astStr1.length(), astStr2.length());
            }
//                int maxLen = ast1.length() > ast2.length() ? ast1.length() : ast2.length();
//                //int distance = calDistance(ast1, ast2);
//                int distance = computeLevenshteinDistance_Optimized(ast1, ast2);
            double similarity = (double)(maxlen - totalDist) / (double)maxlen;
            sims.put(name, similarity);
        }

        String key = id1 + "#" + id2;
        System.out.println(key);
        FileWriter fw = new FileWriter(key + ".txt");
        for(String pack : sims.keySet()){
            String res = pack + " " + sims.get(pack);
            if(sims.get(pack) < 1){
                System.out.println(res);
            }
            fw.write(res + System.getProperty("line.separator"));
        }

        fw.close();
    }

    static void compareTwoSpecialCommits(ArrayList<String> packageNames, String id1, String id2) throws IOException, InterruptedException{
        javaRoot = "D:\\soot\\src";
        HashMap<String, HashMap<String, String>> astStrings1 = generateASTStrings(id1, packageNames);
        javaRoot = "D:\\soot\\src\\main\\java";
        HashMap<String, HashMap<String, String>> astStrings2 = generateASTStrings(id2, packageNames);

        HashMap<String, Double> sims = new HashMap<>();
        for(String name : packageNames){
            HashMap<String, String> ast1 = astStrings1.get(name);
            HashMap<String, String> ast2 = astStrings2.get(name);
            Set<String> javaClasses = new HashSet<>();
            javaClasses.addAll(ast1.keySet());
            javaClasses.addAll(ast2.keySet());
            int maxlen = 0;
            int totalDist = 0;
            for(String javaClass : javaClasses){
                String astStr1 = ast1.getOrDefault(javaClass, "");
                String astStr2 = ast2.getOrDefault(javaClass, "");
                int distance = computeLevenshteinDistance_Optimized(astStr1, astStr2);
                totalDist += distance;
                maxlen += Math.max(astStr1.length(), astStr2.length());
            }
//                int maxLen = ast1.length() > ast2.length() ? ast1.length() : ast2.length();
//                //int distance = calDistance(ast1, ast2);
//                int distance = computeLevenshteinDistance_Optimized(ast1, ast2);
            double similarity = (double)(maxlen - totalDist) / (double)maxlen;
            sims.put(name, similarity);
        }

        String key = id1 + "#" + id2;
        System.out.println(key);
        FileWriter fw = new FileWriter(key + ".txt");
        for(String pack : sims.keySet()){
            String res = pack + " " + sims.get(pack);
            if(sims.get(pack) < 1){
                System.out.println(res);
            }
            fw.write(res + System.getProperty("line.separator"));
        }

        fw.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //应该以最近一次commit时划分地package结果为基准，而不应该每个commit对应于一个package
        runCmd("git reset --hard 56a8");
        ArrayList<String> packageNames = new ArrayList<>();
        getAllPackageName(javaRoot, packageNames);

        ArrayList<String> ids = getCommitIDs("git log");
//        for(int i = 0;i < ids.size();i++){
//            System.out.println(i + " " + ids.get(i));
//            runCmd("git reset --hard " + ids.get(i));
//            String packagePath = javaRoot;
//            File f = new File(packagePath);
//            if(!f.exists()) {
//                System.out.println(i);
//                break;
//            }
//        }

//        HashMap<String, HashMap<String, Double>> similaritys = new HashMap<>();
//        for(int i = 0;i < ids.size() - 1;i++){
//            compareTwoCommits(packageNames, ids.get(i), ids.get(i + 1));
//        }

//        ArrayList<String> tags = getReleaseCommitIDs();
//        //git tag按时间排序有bug，直接忽略第一个tag
//        for(int i = 1;i < tags.size() - 2;i++){
//            if(i <= 23)
//                javaRoot = "D:\\soot\\src";
//            else
//                javaRoot = "D:\\soot\\src\\main\\java";
//            compareTwoCommits(packageNames, tags.get(i), tags.get(i + 1));
//        }
//        compareTwoCommits(packageNames, "4.0.0", "4.1.0");
//        compareTwoCommits(packageNames, "4.1.0", "v4.2.0");
        compareTwoSpecialCommits(packageNames, "3.0.0","3.1.0");


//        runCmd("git reset --hard " + ids.get(5));
//        runCmd("git reset --hard " + ids.get(0));
//        System.out.println(calDistance("Hello", "remelo"));

        System.out.println("exit");
    }
}
