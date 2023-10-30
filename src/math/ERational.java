package math;

public class ERational {
    private int[] frac = new int[2];

    public ERational(int num, int den) {
        if (den == 0) {
            throw new RuntimeException("division by zero");
        }

        this.frac[0] = num;
        this.frac[1] = den;

        minimal_term();
    }

    public ERational(int num) {
        this.frac[0] = num;
        this.frac[1] = 1;
    }

    public void multiply_by(ERational n) {
        frac[0] *= n.frac[0];
        frac[1] *= n.frac[1];

        minimal_term();
    }

    public void add(ERational n) {
        int c_den = mcm(n.frac[1], frac[1]); //common denominator
        int mult1 = c_den/frac[1];
        int mult2 = c_den/n.frac[1];

        frac[0] = frac[0]*mult1 + n.frac[0]*mult2;
        frac[1] = c_den;

        minimal_term();
    }

    public boolean is_positive() {
        return !(frac[0]>0)^(frac[1]>0);
    }

    public int[] value() { return new int[] {frac[0], frac[1]}; }
    public String toString() { return frac[0] + ((frac[1] != 1)? "/" + frac[1] : ""); }
    public ERational get_inverse() {
        return new ERational(frac[1], frac[0]);
    }

    public boolean is_equal(ERational comp) {
        return (comp.frac[0] == frac[0]) & (comp.frac[1] == frac[1]);
    }

    public boolean is_equal(int comp) {
        return ((frac[0] == comp) & (frac[1] == 1)) || ((frac[0] == -comp) && (frac[1] == -1));
    }

    public ERational clone() {
        return new ERational(frac[0], frac[1]);
    }

    private void minimal_term() {
        //check for divisibility between numerator and denominator
        int c_div = mcd(frac[0], frac[1]);
        frac[0] /= c_div;
        frac[1] /= c_div;

        //the numerator always determine the fraction sign
        if (frac[1] < 0) {
            frac[0] *= -1;
            frac[1] *= -1;
        }
    }

    private int mcm(int n, int m) {
        return n*m / mcd(n,m);
    }

    private int mcd(int n, int m) {
        //if m=n => MCD(n,n)=n
        if (m == n) { return n; }

        int carry = m;
        while (carry != 0) {
            m = carry;
            carry = n % m;
            n = m;
        }

        return (m > 0)? m : -m;
    }
}