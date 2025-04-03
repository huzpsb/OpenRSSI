package fox.num;

public class Num3d {
    private static final double EPSILON = 1e-5;

    public final double x;
    public final double y;
    public final double z;

    public Num3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        assertNonNaN();
    }

    public Num3d() {
        // 随机单位向量
        double theta = Math.random() * 2 * Math.PI;
        double phi = Math.random() * Math.PI;
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        this.x = sinPhi * cosTheta;
        this.y = sinPhi * sinTheta;
        this.z = cosPhi;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private static double acos(double value) {
        return Math.acos(clamp(value, -1.0, 1.0));
    }

    public void assertNonNaN() {
        // 检查是否有NaN
        if (Double.isNaN(this.x) || Double.isNaN(this.y) || Double.isNaN(this.z)) {
            throw new IllegalArgumentException("Coordinates cannot be NaN");
        }
    }

    public void assertNorm() {
        // 检查是否归一化
        if (Math.abs(1 - this.length()) > EPSILON) {
            throw new IllegalArgumentException("Coordinates are not normalized");
        }
    }

    public Num3d add(Num3d other) {
        // 加
        return new Num3d(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Num3d subtract(Num3d other) {
        // 减
        return new Num3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Num3d scale(double factor) {
        // 按比例缩放
        return new Num3d(this.x * factor, this.y * factor, this.z * factor);
    }

    public Num3d ofScale(double factor) {
        // 按目标长度缩放
        double r = this.length();
        return new Num3d(this.x / r * factor, this.y / r * factor, this.z / r * factor);
    }

    public Num3d ofNorm() {
        // 归一化
        return this.ofScale(1);
    }

    public double dot(Num3d other) {
        // 点乘
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Num3d cross(Num3d other) {
        // 叉乘
        return new Num3d(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public double length() {
        // 模长
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double dist(Num3d other) {
        // 距离
        return this.subtract(other).length();
    }

    public Num3d rotateAroundAxis(Num3d axis, double angle) {
        // 绕轴旋转
        // Ref: Rodrigues' rotation formula
        axis.assertNorm();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double dot = this.dot(axis);
        return new Num3d(
                this.x * cos + axis.x * dot * (1 - cos) + axis.y * this.z * sin - axis.z * this.y * sin,
                this.y * cos + axis.y * dot * (1 - cos) + axis.z * this.x * sin - axis.x * this.z * sin,
                this.z * cos + axis.z * dot * (1 - cos) + axis.x * this.y * sin - axis.y * this.x * sin
        );
    }

    public double angle(Num3d other) {
        // 夹角
        return acos(this.dot(other) / (this.length() * other.length()));
    }

    @Override
    public String toString() {
        return "Num3d(" + x + ", " + y + ", " + z + ")";
    }
}
