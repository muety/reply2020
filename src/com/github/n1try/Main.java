package com.github.n1try;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.github.n1try.model.Developer;
import com.github.n1try.model.Manager;
import com.github.n1try.model.Office;
import com.github.n1try.model.Solution;
import com.github.n1try.solver.GreedySolver;
import com.github.n1try.solver.Solver;

public class Main {
    private static Office office;
    private static List<Developer> developers = new ArrayList<>();
    private static List<Manager> managers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            readInput(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Solver solver = new GreedySolver(office, developers, managers);
        Solution solution = solver.solve();

        try {
            String which = Paths.get(args[0]).getFileName().toString().substring(0, 1);
            Files.write(Paths.get(String.format("%s_solution.txt", which)), solution.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

                developers.add(developer);
                countReplyers++;
            } else if (lc == height + nDevs + 2) {
                nManagers = Integer.valueOf(line);
            } else if (lc > height + nDevs + 2 && lc <= height + nDevs + nManagers + 2) {
                String[] parts = line.split(" ");

                Manager manager = new Manager(countReplyers);
                manager.setCompany(parts[0]);
                manager.setBonus(Integer.valueOf(parts[1]));

                managers.add(manager);
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
