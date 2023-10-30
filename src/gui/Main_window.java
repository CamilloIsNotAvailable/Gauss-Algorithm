package gui;

import com.sun.tools.javac.Main;
import math.ERational;
import math.Expression;
import math.Gauss_algorithm;
import math.Matrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public abstract class Main_window {
    protected static JFrame f = null;
    protected static JPanel main_layer;
    protected static JPanel equation_fields;

    protected static Equation_fields_vector equations;
    protected static JTextField var_text_list;
    protected static Map<Character, Integer> var_list = new LinkedHashMap<>();
    protected static JPanel popup_layer;
    protected static JTextField prog_bar;
    protected static int prog_ind = 0;

    protected static Color background_color = new Color(60, 60, 60);
    protected static Color text_color = new Color(140, 140, 140);

    public static void start() {
        if (f == null) {
            var_list.put(' ', 0); //add the known coefficient variable

            f = new JFrame("Gauss algorithm");
            f.setBackground(background_color);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setResizable(false);

            //setup main layer elements
            main_layer = new JPanel();
            main_layer.setLayout(new GridBagLayout());
            main_layer.setBackground(background_color);

            equation_fields = new JPanel();
            equation_fields.setLayout(new GridLayout(0, 1));
            equation_fields.add(new Equation_field());
            equation_fields.setBackground(Color.ORANGE);

            var_text_list = new JTextField("variables: ");
            equations = new Equation_fields_vector();
            JButton add_line = new JButton();
            JButton remove_line = new JButton();
            JButton solve = new JButton("Done");

            var_text_list.setEditable(false);

            var_text_list.setBorder(null);
            add_line.setBorder(null);
            remove_line.setBorder(null);
            solve.setBorder(null);

            var_text_list.setBackground(background_color);
            solve.setBackground(background_color);

            var_text_list.setForeground(text_color);
            solve.setForeground(text_color);

            add_line.setIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/add.png"));
            add_line.setRolloverIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/add_hover.png"));
            add_line.setPressedIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/add_press.png"));
            remove_line.setIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/remove.png"));
            remove_line.setRolloverIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/remove_hover.png"));
            remove_line.setPressedIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/remove_press.png"));

            add_line.addActionListener(add_line_listener);
            remove_line.addActionListener(remove_line_listener);
            solve.addActionListener(solve_listener);

            GridBagConstraints c = new GridBagConstraints();

            c.weighty = 0; //first the elements wich do not resize
            c.weightx = 0;

            c.gridx = 0;
            c.gridy = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            c.insets = new Insets(10, 10, 0, 0);
            main_layer.add(add_line, c);

            c.gridx = 1;
            c.anchor = GridBagConstraints.LAST_LINE_END;
            c.insets = new Insets(10, 10, 0, 10);
            main_layer.add(remove_line, c);

            c.gridy = 2;
            c.insets = new Insets(10, 10, 10, 10);
            main_layer.add(solve, c);

            c.gridx = 0;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.insets = new Insets(10, 10, 10, 0);
            main_layer.add(var_text_list, c);

            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            c.weighty = 1;
            c.insets = new Insets(10, 10, 0, 10);
            main_layer.add(equation_fields, c);

            //setup the progress layer and result elements
            popup_layer = new JPanel();
            popup_layer.setLayout(null);
            popup_layer.setBackground(new Color(80,80,80));
            popup_layer.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)));

            prog_bar = new JTextField();
            JButton close_popup = new JButton();

            close_popup.setIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/cross.png"));
            close_popup.setRolloverIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/cross_hover.png"));
            close_popup.setPressedIcon(new ImageIcon("/home/camillo/java-projects/AlgoritmoGauss/images/cross_press.png"));

            close_popup.setBorder(null);
            close_popup.setBounds(5, 5, 10, 10);

            prog_bar.setBorder(null);
            prog_bar.setEditable(false);
            prog_bar.setBackground(new Color(80, 80, 80));
            prog_bar.setSelectionColor(new Color(100, 100, 100));
            prog_bar.setForeground(new Color(140, 140, 140));

            popup_layer.add(close_popup);
            popup_layer.add(prog_bar);

            close_popup.addActionListener(e -> {
                popup_layer.setVisible(false);

                if (!prog_bar.isVisible()) {
                    prog_bar.setVisible(true);

                    popup_layer.remove(2);
                }
            });

            //add all the elements to the frame and make it visible
            Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension fd = new Dimension(300, 85);

            main_layer.setBounds(0, 0, fd.width, fd.height);
            popup_layer.setBounds(0, 0, 0, 0);
            f.getLayeredPane().add(main_layer, JLayeredPane.DEFAULT_LAYER);
            f.getLayeredPane().add(popup_layer, JLayeredPane.POPUP_LAYER);

            f.setBounds(sd.width/2 - fd.width/2, sd.height/2 - fd.height/2, fd.width, fd.height+37);
            f.setVisible(true);
        }
    }

    public static void next_step() {
        if (!popup_layer.isVisible()) {
            popup_layer.setVisible(true);
        }

        if (prog_ind == 0) {
            prog_bar.setText("Generating the matrix");
        } else if (prog_ind <= equation_fields.getComponents().length) {
            prog_bar.setText("Gauss-protocol, line = " + prog_ind);
        } else {
            prog_bar.setText("Solving matrix, line = " + (2*equation_fields.getComponents().length - prog_ind));
        }

        popup_fit();
        prog_ind++;
    }

    private static ActionListener add_line_listener = e -> {
        equations.add(new Equation_field());
    };

    private static ActionListener remove_line_listener = e -> {
        equations.removeLastElement();
    };

    private static ActionListener solve_listener = e -> {
        String[] equations = new String[equation_fields.getComponents().length];

        for (int i = 0; i < equations.length; i++) { //copy every equation text in the array equations
            equations[i] = ((JTextField)equation_fields.getComponents()[i]).getText();
        }

        Matrix mat = build_matrix(equations);
        Gauss_algorithm.transform_matrix(mat);

        Map<Character, Expression> solution = solve_matrix(mat);
        try {
            if (solution.size() == 0) { //0 = 0
                prog_bar.setText("0 = 0");
                popup_fit();
            } else {
                JList<String> solution_list = new JList<>();
                JScrollPane scrollPane = new JScrollPane(solution_list);

                Vector<String> solution_vec = new Vector<>();
                for (char var : solution.keySet().toArray(new Character[0])) {
                    solution_vec.add(var + " = " + solution.get(var));
                }
                solution_list.setListData(solution_vec);

                scrollPane.setBounds(
                        5,
                        20,
                        200,
                        solution_vec.size()*20
                );
                popup_layer.setBounds(
                        main_layer.getWidth()/2 - scrollPane.getSize().width/2 - 5,
                        main_layer.getHeight()/2 - scrollPane.getSize().height/2 - 12,
                        scrollPane.getSize().width + 10,
                        scrollPane.getSize().height + 25
                );

                scrollPane.setBorder(null);
                scrollPane.setBackground(new Color(80, 80, 80));
                scrollPane.setFocusable(false);

                solution_list.setEnabled(false);
                solution_list.setBorder(null);
                solution_list.setBackground(new Color(80, 80, 80));
                solution_list.setForeground(new Color(140, 140, 140));

                prog_bar.setVisible(false);
                popup_layer.add(scrollPane);
            }
        } catch (NullPointerException ex) { //if there are no solution
            prog_bar.setText("there are no solution");
            popup_fit();
        }

        prog_ind = 0; //system solved, reset the progress indicator
    };

    private static void popup_fit() {
        prog_bar.setBounds(
                5,
                20,
                prog_bar.getPreferredSize().width + 3,
                prog_bar.getPreferredSize().height
        );

        popup_layer.setBounds(
                main_layer.getWidth()/2 - prog_bar.getPreferredSize().width/2 - 5,
                main_layer.getHeight()/2 - prog_bar.getPreferredSize().height/2 - 12,
                prog_bar.getPreferredSize().width + 10,
                prog_bar.getPreferredSize().height + 25
        );
    }

    private static void print(Matrix mat) {
        for (int i = 0; i < mat.matrix_dim.height; i++) {
            ERational[] line = mat.get_line(i);
            for (int j = 0; j < mat.matrix_dim.width; j++) {
                System.out.printf(line[j] + ((j == mat.matrix_dim.width-1)? "\n" : " || "));
            }
        }
    }

    private static Map<Character, Expression> solve_matrix(Matrix mat) {
        Map<Character, Expression> solutions = new LinkedHashMap<>();

        Character[] var_list = Main_window.var_list.keySet().toArray(new Character[0]);
        for (int i = mat.matrix_dim.height-1; i >= 0; i--) {
            next_step();

            ERational[] line = mat.get_line(i);
            Expression line_exp = new Expression(""); //build the expression for the line
            for (int j = var_list.length-1; j >= 0; j--) {
                line_exp.add_monomial(var_list[j], line[var_list.length-j-1]);

                if (solutions.keySet().contains(var_list[j])) {
                    line_exp.sobstitute(var_list[j], solutions.get(var_list[j]).clone());
                }
            }

            if (!line_exp.toString().equals("")) { //if it's not an all-zero line
                Expression rhs = to_rhs(line_exp);
                char line_var = line_exp.get_var_array()[0];

                if (line_var == ' ') {
                    return null; //the line is 1=0 so there is no solution to this matrix
                }
                else {
                    solutions.put(line_var, rhs);
                }
            }
        }

        return solutions;
    }


    private static Expression to_rhs(Expression e) { //move all the monomial to the RHS but the left-most
        Expression rhs = new Expression("");

        Character[] var_arr = e.get_var_array();
        for (int i = 1; i < var_arr.length; i++) {
            ERational coef = e.get_coefficient(var_arr[i]);
            coef.multiply_by(new ERational(-1)); //move from LHS to RHS

            rhs.add_monomial(var_arr[i], coef);
        }

        return rhs;
    }

    private static Matrix build_matrix(String[] equations) {
        Matrix mat = new Matrix(equations.length, var_list.size());
        Pattern eq_side_pattern = Pattern.compile("=");

        for (int i = 0; i < equations.length; i++) {
            String[] eq_side = eq_side_pattern.split(equations[i]); //split RHS and LHS

            //move LHS to RHS
            Expression[] eq_side_exp = new Expression[] {new Expression(eq_side[0]), new Expression(eq_side[1])};
            eq_side_exp[1].multiply_by(new ERational(-1));
            eq_side_exp[0].add_polinomial(eq_side_exp[1]);

            Character[] var_array = var_list.keySet().toArray(new Character[0]);
            for (int j = var_array.length-1; j >= 0; j--) {
                mat.define_next_element(eq_side_exp[0].get_coefficient(var_array[j]));
            }
        }

        return mat;
    }
}

class Equation_fields_vector { //keep track of how many equation fields are in the panel and resize the frame when added/removed
    private int size = 1;

    public void add(JTextField eq) {
        Main_window.equation_fields.add(eq);
        Main_window.f.setSize(Main_window.f.getWidth(), Main_window.f.getHeight() + 15);
        Main_window.main_layer.setSize(Main_window.main_layer.getWidth(), Main_window.main_layer.getHeight() + 15);

        size++;
    }

    public synchronized void removeLastElement() {
        if (size != 1) {
            Main_window.equation_fields.remove(size - 1);
            Main_window.f.setSize(Main_window.f.getWidth(), Main_window.f.getHeight() - 15);
            Main_window.main_layer.setSize(Main_window.main_layer.getWidth(), Main_window.main_layer.getHeight() - 15);

            size--;
        }
    }
}

class Equation_field extends JTextField { //setup graphics for the equation fields
    private Vector<Character> contained_var = new Vector<>();

    public Equation_field() {
        super();

        this.setBackground(Main_window.background_color.brighter().brighter());
        this.setForeground(Main_window.text_color.darker().darker().darker());
        this.setBorder(BorderFactory.createLineBorder(Main_window.background_color.darker()));
        this.setFont(new Font("Arial", Font.PLAIN, 11));

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() >= 65 && e.getKeyCode() <= 90) { //if it is a lower-case char
                    if (Main_window.var_list.get(e.getKeyChar()) == null) {
                        Main_window.var_list.put(e.getKeyChar(), 1);

                        if (Main_window.var_text_list.getText().equals("variables: ")) {
                            Main_window.var_text_list.setText(Main_window.var_text_list.getText() + e.getKeyChar());
                        }
                        else {
                            Main_window.var_text_list.setText(Main_window.var_text_list.getText() + ", " + e.getKeyChar());
                        }
                    } else if (!contained_var.contains(e.getKeyChar())) {
                        Main_window.var_list.replace(e.getKeyChar(), Main_window.var_list.get(e.getKeyChar()) + 1);
                    }

                    if (!contained_var.contains(e.getKeyChar())) {
                        contained_var.add(e.getKeyChar());
                    }
                }

                if (e.getKeyCode() == 8 || e.getKeyCode() == 127) { //if a char is deleted
                    for (int i = 0; i < contained_var.size(); i++) {
                        if (getText().indexOf(contained_var.elementAt(i)) == -1) {
                            Main_window.var_list.replace(contained_var.elementAt(i), Main_window.var_list.get(contained_var.elementAt(i)) - 1);
                            if (Main_window.var_list.get(contained_var.elementAt(i)) == 0) {
                                Main_window.var_list.remove(contained_var.elementAt(i));
                                Equation_field.remove_var_txt(contained_var.elementAt(i));
                            }

                            contained_var.remove(i);
                            i--;
                        }
                    }
                }
            }
        });
    }

    private static void remove_var_txt(char var) {
        String new_var_list = Main_window.var_text_list.getText().replace("variables: ", "").replaceAll("[, ]*" + var, "");
        try {
            if (new_var_list.charAt(0) == ',') {
                new_var_list = new_var_list.substring(2, new_var_list.length());
            }
        } catch (Exception e) {} //if new_var_list is empty

        Main_window.var_text_list.setText("variables: " + new_var_list);
    }
}
