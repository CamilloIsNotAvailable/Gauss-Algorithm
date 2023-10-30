package math;

import java.awt.*;

public class Matrix {
    private Matrix_line[] matrix;
    public Dimension matrix_dim;

    private int def_index = 0; //track the progress defining the matrix

    public Matrix(int h, int w) {
        matrix_dim = new Dimension(w, h);

        matrix = new Matrix_line[h];
        for (int i = 0; i < h; i++) {
            matrix[i] = new Matrix_line(w);
        }
    }

    public void define_next_element(ERational el) {
        matrix[def_index / matrix_dim.width].set_element_at(def_index % matrix_dim.width, el);
        def_index++;
    }

    public ERational[] get_column(int index) {
        ERational[] col = new ERational[matrix_dim.height];
        for (int i = 0; i < matrix_dim.height; i++) {
            col[i] = matrix[i].get_element_at(index);
        }

        return col;
    }

    public ERational[] get_line(int index) {
        return matrix[index].line;
    }

    public void swap_line(int index1, int index2) {
        Matrix_line mid = matrix[index1];

        matrix[index1] = matrix[index2];
        matrix[index2] = mid;
    }

    public void multiply_line_by(int index, ERational num) {
        matrix[index].multiply_by(num);
    }

    public void add_line_to_line(int index1, int index2, ERational num) {
        Matrix_line add_line = matrix[index1].clone();
        add_line.multiply_by(num);

        matrix[index2].add(add_line);
    }

    private class Matrix_line {
        private ERational[] line;

        public Matrix_line(int len) {
            line = new ERational[len];
        }

        public void set_element_at(int index, ERational el) {
            line[index] = el;
        }

        public ERational get_element_at(int index) {
            return line[index].clone();
        }

        public void multiply_by(ERational num) {
            for (ERational el : line) {
                el.multiply_by(num);
            }
        }

        public void add(Matrix_line add_line) {
            for (int i = 0; i < line.length; i++) {
                line[i].add(add_line.get_element_at(i));
            }
        }

        public Matrix_line clone() {
            Matrix_line cloned = new Matrix_line(line.length);
            for (int i = 0; i < line.length; i++) {
                cloned.set_element_at(i, new ERational(line[i].value()[0], line[i].value()[1]));
            }

            return cloned;
        }
    }
}
