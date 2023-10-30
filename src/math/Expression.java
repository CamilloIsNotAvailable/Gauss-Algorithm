package math;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Expression {
    private Map<Character, ERational> exp;

    public Expression(String txt_exp) {
        exp = new LinkedHashMap<>();

        Pattern plus = Pattern.compile("\\+");
        Pattern minus = Pattern.compile("-");

        String[] positive = plus.split(txt_exp);
        for (String pos : positive) {
            String[] negative = minus.split(pos);

            for (int i = 0; i < negative.length; i++) {
                if (!negative[i].equals("")) {
                    String var = find_variable(negative[i]);
                    String[] num = negative[i].replaceAll(var, "").split("/");
                    if (num[0].equals("")) {
                        num = new String[] {"1", "1"};
                    }
                    else if (num.length == 1) {
                        num = new String[] {num[0], "1"};
                    }
                    ERational coef = new ERational(Integer.valueOf(num[0]) * ((i == 0) ? 1 : -1), Integer.valueOf(num[1]));

                    add_monomial(var.charAt(0), coef);
                }
            }
        }
    }

    public Character[] get_var_array() {
        return exp.keySet().toArray(new Character[0]);
    }

    public void multiply_by(ERational num) {
        for (ERational coef : exp.values().toArray(new ERational[0])) {
            coef.multiply_by(num);
        }
    }

    public void add_polinomial(Expression add_ex) {
        for (char key : add_ex.get_var_array()) {
            add_monomial(key, add_ex.get_coefficient(key));
        }
    }

    public void add_monomial(char var, ERational coef) {
        boolean added = false;
        for (char pol_var : get_var_array()) {
            if (pol_var == var) {
                exp.get(pol_var).add(coef);
                added = true;

                break;
            }
        }
        if (!added && !coef.is_equal(0)) {
            exp.put(var, coef);
        }
    }

    public ERational get_coefficient(char var) {
        ERational coef;
        if ((coef = exp.get(var)) == null) {
            return new ERational(0);
        }
        return coef;
    }

    public void sobstitute(char var, Expression exp) {
        if (this.exp.containsKey(var)) { //se contiene la variabile var
            exp.multiply_by(this.exp.get(var));
            this.exp.remove(var);
            add_polinomial(exp);
        }
    }

    public Expression clone() {
        return new Expression(toString());
    }

    @Override
    public String toString() {
        String expr_str = "";
        for (char var : get_var_array()) {
            ERational coef = exp.get(var);
            if (coef.is_positive() && !expr_str.equals("")) {
                expr_str += "+";
            }
            expr_str += coef.toString() + ((var == ' ')? "" : var);
        }

        return expr_str;
    }

    private String find_variable(String monomial) {
        String[] variable = monomial.split("-*[0-9/]*");
        if (variable.length == 0) { //if is a known coefficient
            return " ";
        }

        if (variable[0].equals("")) {
            return variable[2];
        }
        return variable[0];
    }
}
