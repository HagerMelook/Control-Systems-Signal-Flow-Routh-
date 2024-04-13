package com.example.backend.service.routh_hurwitz;

import com.example.backend.service.EquationService;
import org.apache.commons.math3.complex.Complex;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 * This class implements the Routh-Hurwitz stability criterion for analyzing the stability
 * of a given system represented by its characteristic equation.
 */
public class RouthHurwitz {
    private List<Double> coeff_list;
    private double[][] routh_table;
    private EquationService equationService;
    private int stability;

    public void init(String equation) throws InputMismatchException {
        this.stability = 0;
        this.coeff_list = new ArrayList<>();
        this.equationService = new EquationService(coeff_list);
        this.equationService.parseEquation(equation);
        int cols = (this.coeff_list.size() + 1) / 2;
        int rows = this.coeff_list.size();
        this.routh_table = new double[rows][cols];
    }

    /**
     * Checks if a row of the Routh table consists entirely of zeros and replaces it with
     * its auxiliary equation if necessary.
     *
     * @param k   The index of the row.
     */
    private void checkAllZerosRow(int k) {
        boolean is_all_zeros = true;
        for (int j = 0; j < this.routh_table[0].length; ++j) {
            if (this.routh_table[k][j] != 0.0 && this.routh_table[k][j] != -0.0) {
                is_all_zeros = false;
                break;
            }
        }
        if (is_all_zeros) {
            int degree = this.routh_table.length - k;
            this.routh_table[k] = this.equationService.auxiliaryEquation(this.routh_table[k - 1], degree);
        }
    }

    /**
     * Develops Routh Table.
     */
    public void developRouthTable() {
        System.out.println();
        for (int i = 0; i < this.routh_table[0].length; ++i) { // first & second row
            this.routh_table[0][i] = this.coeff_list.get(i * 2);
            if (1 + i * 2 < this.coeff_list.size()) // check if no. of rows even
                this.routh_table[1][i] = this.coeff_list.get(1 + i * 2);
        }
        checkAllZerosRow(1);
        for (int i = 2; i < this.routh_table.length; ++i) {
            for (int j = 0; j <  this.routh_table[i].length - i / 2; ++j) {
                double determinant = this.routh_table[i - 1][0] * this.routh_table[i - 2][j + 1] - this.routh_table[i - 2][0] * this.routh_table[i - 1][j + 1];
                if (this.routh_table[i - 1][0] == 0)
                    this.routh_table[i][j] = determinant / 0.000001; // substitute zero with small value
                else
                    this.routh_table[i][j] = determinant / this.routh_table[i - 1][0];
            }
            checkAllZerosRow(i);
        }
    }

    /**
     * Counts the number of poles on the right-hand side of the s-plane.
     * @return The number of poles on the right-hand side.
     */
    private int countRhsPoles() {
        int rhs_poles = 0;
        boolean last_sign = (this.routh_table[0][0] >= 0); // if positive or zero >> true
        for (int i = 1; i < this.routh_table.length; ++i) {
            boolean curr_sign = (this.routh_table[i][0] >= 0);
            if (last_sign != curr_sign)
                rhs_poles++;
            last_sign = curr_sign;
        }
        if (rhs_poles == 0) this.stability = 1; // stable
        return rhs_poles;
    }

    /**
     * Retrieves the poles on the right-hand side of the s-plane.
     * @return A list of poles on the right-hand side.
     */
    private List<Complex> rhsPoles() {
        Complex[] poles = equationService.findPoles();
        List<Complex> rhs_poles = new ArrayList<>();
        for (Complex pole : poles) {
            if (pole.getReal() > 0) {
                rhs_poles.add(pole);
                System.out.println(pole);
            }
        }
        return rhs_poles;
    }

    /**
     * Provides information about the system.
     * @return JSON object containing system information.
     */
    public JSONObject systemInfo() {
        JSONObject info = new JSONObject();
        int rhs_poles = countRhsPoles();
        info.put("Stability", (stability == 0 ? "Not Stable" : "Stable"));
        info.put("Routh_Table", new JSONArray(this.routh_table));
        if (stability != 1) {
            info.put("Number_of_RHS_poles", rhs_poles);
            JSONArray rhsPolesArray = new JSONArray();
            List<Complex> rhsPolesComplex = rhsPoles();
            for (Complex pole : rhsPolesComplex) {
                JSONObject poleObject = new JSONObject();
                poleObject.put("Real", pole.getReal());
                poleObject.put("Imaginary", pole.getImaginary());
                rhsPolesArray.put(poleObject);
            }
            info.put("RHS_poles", rhsPolesArray);
        }
        System.out.println(info);
        return info;
    }
}
