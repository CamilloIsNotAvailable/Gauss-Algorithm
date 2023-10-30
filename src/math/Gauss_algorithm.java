package math;

import gui.Main_window;
import java.awt.*;

public abstract class Gauss_algorithm {
    public static void transform_matrix(Matrix m) {
        for (int line_done = 0; line_done < m.matrix_dim.height; line_done++) {
            Main_window.next_step();

            Point first_el = find_first_non_zero(m, line_done);
            if (first_el.x == -1) { // if all the line below line_done are of only zeros
                break; //the algorithm is done
            }
            else if(first_el.y != line_done) { //the line with the first non-zero entry have to be the top one
                m.swap_line(line_done, first_el.y);
                first_el.y = line_done;
            }

            if (!m.get_line(first_el.y)[first_el.x].is_equal(1)) { //the entry at first_el have to be a 1
                m.multiply_line_by(first_el.y, m.get_line(first_el.y)[first_el.x].get_inverse());
            }

            ERational[] column = m.get_column(first_el.x);
            for (int i = line_done+1; i < m.matrix_dim.height; i++) { //the entry at first_el have to be the only non-zero entry in the column first_el.x below line_done
                if (!column[i].is_equal(0)) {
                    column[i].multiply_by(new ERational(-1));
                    m.add_line_to_line(line_done, i, column[i]);
                }
            }
        }
    }

    private static Point find_first_non_zero(Matrix m, int lineDone) {
        Point first_el = new Point(-1, -1);

        for (int i = 0; i < m.matrix_dim.width; i++) {
            ERational[] column = m.get_column(i);
            for (int j = lineDone; j < m.matrix_dim.height; j++) {
                if (!column[j].is_equal(0)) {
                    first_el.x = i;
                    first_el.y = j;

                    return first_el;
                }
            }
        }

        return  first_el;
    }
}
