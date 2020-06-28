import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import static AST.Main.*;

public class Main {
    public static double getLowBound(double d, double[] intervals){
        if(d == Double.NaN)
            return 1.0;
        if(d == 0.0)
            return -1.0;
        int i = 0;
        for(;i < intervals.length;i++)
            if(d == 1.0)
                return d;
            else if(d > intervals[i])
                break;

        return i == intervals.length ? 0.0 : intervals[i];
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        AST.Main.runCmd("git reset --hard 56a8");
        File f = new File(".\\release");
        File[] children = f.listFiles();

        double[] intervals = new double[]{0.99,0.98,0.97,0.96,0.95,0.94,0.93,0.92,0.91,0.90,0.85,0.80};
        ArrayList<String> packageNames = new ArrayList<>();
        getAllPackageName(javaRoot, packageNames);
        HashMap<String, TreeMap<Double, Integer>> packageSims = new HashMap<>();

        ArrayList<String> ids = getCommitIDs("git log");


        for(String s : packageNames) {
            TreeMap<Double, Integer> sims = new TreeMap<>();
            for (Double d : intervals)
                sims.put(d, 0);
            sims.put(1.0,0);
            sims.put(0.0,0);
            sims.put(-1.0,0);
            packageSims.put(s, sims);

        }
        int min = Integer.MAX_VALUE;

        double MaxDiff = 0.8;
        int MaxDiffNum = 2;
        HashMap<String, Integer> diffNums = new HashMap<>();
        HashMap<String, Double> diffBefore = new HashMap<>();
        for(String s : packageNames) {
            diffNums.put(s, 0);
            diffBefore.put(s, 1.0);
        }
        for(int i = 0;i < 937;i++) {
            String chd = ids.get(i) + "#" + ids.get(i + 1) + ".txt";
            try {
                BufferedReader in = new BufferedReader(new FileReader(chd));
                String str;
                while ((str = in.readLine()) != null) {
                    String[] temp = str.split(" ");
                    String packName = temp[0];
                    double sim = Double.parseDouble(temp[1]);
                    double bound = getLowBound(sim, intervals);
                    TreeMap<Double, Integer> sims = packageSims.get(packName);
                    if (sims != null) {
                        sims.put(bound, sims.get(bound) + 1);

                        if(sim <= MaxDiff){
                            diffNums.put(packName, diffNums.get(packName) + 1);
                            //System.out.println(packName + "," + ids.get(i) + "," + ids.get(i + 1));
                        }else {
                            if(diffNums.get(packName) > 0)
                                System.out.println(packName + " " + diffNums.get(packName));
                            if(diffNums.get(packName) >= MaxDiffNum)
                                System.out.println(packName + "," + ids.get(i) + "," + ids.get(i + 1));
                            diffNums.put(packName, 0);
                        }

                    } else {
                    }

                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        System.out.println(min);

        for(String s : packageNames) {
            BufferedWriter out = new BufferedWriter(new FileWriter("releasePackage\\" + s +".txt"));
            TreeMap<Double, Integer> sims = packageSims.get(s);
            for(Double d : sims.keySet()){
                out.write(d + " " + sims.get(d) + System.getProperty("line.separator"));
            }
            out.close();
        }


//        double[] intervals = new double[]{0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1};
//
//        ArrayList<String> packageNames = new ArrayList<>();
//        getAllPackageName(javaRoot, packageNames);
//        HashMap<String, TreeMap<Double, Integer>> packageSims = new HashMap<>();
//
//        ArrayList<String> ids = getCommitIDs("git log");
//
//
//        for(String s : packageNames) {
//            TreeMap<Double, Integer> sims = new TreeMap<>();
//            for (Double d : intervals)
//                sims.put(d, 0);
//            sims.put(1.0,0);
//            sims.put(0.0,0);
//            sims.put(-1.0,0);
//            packageSims.put(s, sims);
//
//        }
//        int min = Integer.MAX_VALUE;
//
//        for(File chd : children) {
//            String name = chd.getName();
//            if (chd.isFile() && name.endsWith(".txt")) {
//                try {
//                    BufferedReader in = new BufferedReader(new FileReader(chd));
//                    String str;
//                    while ((str = in.readLine()) != null) {
//                        String[] temp = str.split(" ");
//                        String packName = temp[0];
//                        double sim = Double.parseDouble(temp[1]);
//                        double bound = getLowBound(sim, intervals);
//                        TreeMap<Double, Integer> sims = packageSims.get(packName);
//                        if(sims != null) {
//                            sims.put(bound, sims.get(bound) + 1);
//                        }else{
//                            String _2ids = name.substring(0,name.length() - 4);
//                            String[] _2idArrs = _2ids.split("#");
//                            int idx = ids.indexOf(_2idArrs[0]);
//                            if(idx < min) min = idx;
//                            System.out.println(name + " " + idx + " " + temp[0]);
//                        }
//                    }
//
//                } catch (IOException e) {
//                }
//            }
//        }
//
//        System.out.println(min);
//
//        BufferedWriter out = new BufferedWriter(new FileWriter("result\\" +"commits.csv"));
//        out.write("package");
//
//        out.write(",0.0");
//        out.write(",0.0~0.8");
//        out.write(",0.8~0.85");
//        out.write(",0.85~0.9");
//        out.write(",0.9~0.91");
//        out.write(",0.91~0.92");
//        out.write(",0.92~0.93");
//        out.write(",0.93~0.94");
//        out.write(",0.94~0.95");
//        out.write(",0.95~0.96");
//        out.write(",0.96~0.97");
//        out.write(",0.97~0.98");
//        out.write(",0.98~0.99");
//        out.write(",0.99~1.0");
//        out.write(",1.0\r\n");

        System.out.println(min);

        BufferedWriter out = new BufferedWriter(new FileWriter("result\\" +"releases.csv"));
        out.write("release");

        out.write(",0.0");
        out.write(",0.0~0.1");
        out.write(",0.1~0.2");
        out.write(",0.2~0.3");
        out.write(",0.3~0.4");
        out.write(",0.4~0.5");
        out.write(",0.5~0.6");
        out.write(",0.6~0.7");
        out.write(",0.7~0.8");
        out.write(",0.8~0.9");
        out.write(",0.9~1.0");
        out.write(",1.0\r\n");

        for(String s : packageNames){
            out.write(s);
            TreeMap<Double, Integer> sims = packageSims.get(s);
            for(Double d : sims.keySet()){
                out.write("," + sims.get(d));
            }
            out.write("\r\n");
        }
        out.close();
//        for(String s : packageNames) {
//            BufferedWriter out = new BufferedWriter(new FileWriter("releasePackage\\" + s +".txt"));
//            TreeMap<Double, Integer> sims = packageSims.get(s);
//            for(Double d : sims.keySet()){
//                out.write(d + " " + sims.get(d) + System.getProperty("line.separator"));
//            }
//            out.close();
//        }



//        TreeMap<Double, Integer> cnts = new TreeMap<>();
//        for(int i = 0;i < intervals.length;i++)
//            cnts.put(intervals[i], 0);
//        cnts.put(0.0, 0);
//        cnts.put(1.0, 0);
//        cnts.put(-1.0, 0);
//        BufferedWriter overall = new BufferedWriter(new FileWriter("overall\\overall.txt"));
//
//        for(File chd : children) {
//            String name = chd.getName();
//            if (chd.isFile() && name.endsWith(".txt")) {
//                name = name.substring(0,name.length() - 4);
//                //String[] temp = name.split("#");
//                try {
//                    BufferedReader in = new BufferedReader(new FileReader(chd));
//                    String str;
//                    while ((str = in.readLine()) != null) {
//                        String[] temp = str.split(" ");
//                        double sim = Double.parseDouble(temp[1]);
//                        double bound = getLowBound(sim, intervals);
//                        cnts.put(bound, cnts.get(bound) + 1);
//                    }
//                    //System.out.println(name);
//                    overall.write(name + System.getProperty("line.separator"));
//                    BufferedWriter out = new BufferedWriter(new FileWriter("interval\\" + name +".txt"));
//
//                    int _099 = 0;
//
//                    for(Double d : cnts.keySet()) {
//                        out.write(d + " " + cnts.get(d) + System.getProperty("line.separator"));
//                        overall.write(d + " " + cnts.get(d) + System.getProperty("line.separator"));
//                        if(cnts.get(d) != 0)
//                            if(d < 0.99) {
//                                _099 += cnts.get(d);
//                                //System.out.println(d + " " + cnts.get(d));
//                            }
//                        cnts.put(d, 0);
//                    }
//                    if(_099 > 6) {
//                        System.out.println(name);
//                        System.out.println(_099);
//                    }
//                    overall.write(System.getProperty("line.separator"));
//                    out.close();
//
//                } catch (IOException e) {
//                }
//            }
//        }
//        overall.close();
    }
}
