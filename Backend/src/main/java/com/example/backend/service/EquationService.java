package com.example.backend.service;

import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

import java.util.InputMismatchException;
import java.util.List;

/**
 * This class provides services related to manipulating and analyzing the characteristic equations
 * of control systems.
 */
public class EquationService {
    private final List<Double> coeff_list;

    public EquationService(List<Double> coeff_list) {
        this.coeff_list = coeff_list;
    }

    /**
     * Parses the characteristic equation of the system.
     * @param equation The characteristic equation of the system to be parsed.
     */
    public void parseEquation(String equation) throws InputMismatchException { // equation >> 5s^2+10s+20
        try {
            String[] terms = equation.replace("+", " ").replace("-", " -").split(" ");
            int last_degree = Integer.MAX_VALUE;
            for (String term : terms) {
                int temp = last_degree;
                String[] coeff_term = term.replace("s^", " ").replace("s", " ").split(" "); // {5, 2} --> {coeff, deg}

                if (coeff_term.length == 0) // "s"
                    last_degree = 1;
                else if (coeff_term.length == 1)
                    last_degree = (term.contains("s") ? 1 : 0);
                else
                    last_degree = Integer.parseInt(coeff_term[1]);

                if (last_degree >= temp)
                    throw new InputMismatchException("Invalid Input Order");

                if (temp != Integer.MAX_VALUE) {
                    for (int i = 0; i < temp - last_degree - 1; ++i)
                        this.coeff_list.add(0.0);
                }

                double coeff;
                if (coeff_term.length == 0 || coeff_term[0].isEmpty())
                    coeff = 1.0;
                else if (coeff_term[0].equals("-"))
                    coeff = -1;
                else
                    coeff = Double.parseDouble(coeff_term[0]);
                this.coeff_list.add(coeff);
            }
            for (int i = 0; i < last_degree; ++i)
                this.coeff_list.add(0.0);
            if (this.coeff_list.isEmpty())
                throw new InputMismatchException("Invalid Input");
            System.out.print("coeffs >>  ");
            for (double coeff : this.coeff_list)
                System.out.print(coeff + " ");
            System.out.println();
        } catch (Exception e) {
            throw new InputMismatchException("Invalid Input");
        }
    }

    /**
     * Finds the poles of the system's characteristic equation.
     *
     * @return An array of Complex numbers representing the poles of the system's characteristic equation.
     */
    public Complex[] findPoles() {
        double[] coeff_arr = this.coeff_list.stream().mapToDouble(Double::doubleValue).toArray();

        int left = 0;
        int right = coeff_arr.length - 1;
        while (left < right) {
            double temp = coeff_arr[left];
            coeff_arr[left] = coeff_arr[right];
            coeff_arr[right] = temp;
            left++;
            right--;
        }

        LaguerreSolver solver = new LaguerreSolver();
        return solver.solveAllComplex(coeff_arr, 0);
    }

    /**
     * Computes the auxiliary equation for a given row of the Routh table.
     *
     * @param row    The row from which the auxiliary equation is computed.
     * @param degree The degree of the auxiliary equation.
     * @return The computed auxiliary equation.
     */
    public double[] auxiliaryEquation(double[] row, int degree) {
        double[] auxiliary = new double[row.length];
        for (int i = 0; i < row.length; ++i) {
            if (degree - 2 * i == 0.0)
                auxiliary[i] = 0;
            else
                auxiliary[i] = row[i] * (degree - 2 * i);
        }
        return auxiliary;
    }
}
