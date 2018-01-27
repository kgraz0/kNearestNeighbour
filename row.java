
public class row {
    double pid, ct, csi, csh, ma, ec, bn, bc, nn, mit;
    String cla, pcla;
    double dist;
    
    public row(double ct, double csi, double csh, double ma, double ec, double bn, double bc, double nn, double mit) {
        this.ct = ct;
        this.csi = csi;
        this.csh = csh;
        this.ma = ma;
        this.ec = ec;
        this.bn = bn;
        this.bc = bc;
        this.nn = nn;
        this.mit = mit;
    }
    
    public row(double pid, double ct, double csi, double csh, double ma, double ec, double bn, double bc, double nn, double mit, String cla) {
        this.pid = pid;
        this.ct = ct;
        this.csi = csi;
        this.csh = csh;
        this.ma = ma;
        this.ec = ec;
        this.bn = bn;
        this.bc = bc;
        this.nn = nn;
        this.mit = mit;
        this.cla = cla;
    }
    
    public void setDistance(double dist) {
        this.dist = dist;
    }
    
    public double getDistance() {
        return dist;
    }

    public void setPredictedClass(String pcla) {
        this.pcla = pcla;
    }

    public String getPredictedClass() {
        return pcla;
    }
}

