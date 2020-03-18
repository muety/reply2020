// Run with -Xss1024m

package com.github.n1try.reply2020;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.github.n1try.reply2020.model.Developer;
import com.github.n1try.reply2020.model.Manager;
import com.github.n1try.reply2020.model.Office;
import com.github.n1try.reply2020.model.Replyer;
import com.github.n1try.reply2020.model.Solution;
import com.github.n1try.reply2020.solver.GreedySolver;
import com.github.n1try.reply2020.solver.Solver;

public class Main {
    private static Office office;
    private static List<Replyer> replyers = new LinkedList<>();

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        try {
            readInput(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Solver solver = new GreedySolver(office, replyers);
        Solution solution = solver.solve();
        System.out.printf("Found solution with score of %d.\n", solution.getTotalScore());

        try {
            String which = Paths.get(args[0]).getFileName().toString().substring(0, 1);
            Files.write(Paths.get(String.format("%s_solution.txt", which)), solution.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        long end = System.currentTimeMillis();
        System.out.printf("Finished after %.2f seconds.\n", (end - start) / 1000f);
    }

    private static void readInput(String fileName) throws FileNotFoundException {
        File f = new File(fileName);
        Scanner s = new Scanner(f);

        int width = 0;
        int height = 0;
        int nDevs = 0;
        int nManagers = 0;
        int countReplyers = 0;
        String[] officeConfigLines = new String[]{};

        int lc = 0;
        while (s.hasNextLine()) {
            String line = s.nextLine();

            if (lc == 0) {
                String[] parts = line.split(" ");
                width = Integer.valueOf(parts[0]);
                height = Integer.valueOf(parts[1]);
                officeConfigLines = new String[height];
            } else if (lc > 0 && lc <= height) {
                officeConfigLines[lc - 1] = line;
            } else if (lc == height + 1) {
                nDevs = Integer.valueOf(line);
            } else if (lc > height + 1 && lc <= height + nDevs + 1) {
                String[] parts = line.split(" ");

                Developer developer = new Developer(countReplyers);
                developer.setCompany(parts[0]);
                developer.setBonus(Integer.valueOf(parts[1]));

                int nSkills = Integer.valueOf(parts[2]);
                for (int i = 3; i <= 2 + nSkills; i++) {
                    developer.addSkill(parts[i]);
                }

                replyers.add(developer);
                countReplyers++;
            } else if (lc == height + nDevs + 2) {
                nManagers = Integer.valueOf(line);
            } else if (lc > height + nDevs + 2 && lc <= height + nDevs + nManagers + 2) {
                String[] parts = line.split(" ");

                Manager manager = new Manager(countReplyers);
                manager.setCompany(parts[0]);
                manager.setBonus(Integer.valueOf(parts[1]));

                replyers.add(manager);
                countReplyers++;
            } else {
                throw new IllegalStateException("failed to read input");
            }

            lc++;
        }
        s.close();

        office = new Office(width, height);
        office.init(officeConfigLines);
    }
}
