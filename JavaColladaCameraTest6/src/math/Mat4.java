package math;

/**
 *
 * @author leonardo
 */
public class Mat4 {

    public double m00, m01, m02, m03;
    public double m10, m11, m12, m13;
    public double m20, m21, m22, m23;
    public double m30, m31, m32, m33;

    public Mat4() {
    }

    public void set(Mat4 m) {
        m00 = m.m00; m01 = m.m01; m02 = m.m02; m03 = m.m03;
        m10 = m.m10; m11 = m.m11; m12 = m.m12; m13 = m.m13;
        m20 = m.m20; m21 = m.m21; m22 = m.m22; m23 = m.m23;
        m30 = m.m30; m31 = m.m31; m32 = m.m32; m33 = m.m33;
    }

    public void setIdentity() {
        m01 = m02 = m03 = 0;
        m10 = m12 = m13 = 0;
        m20 = m21 = m23 = 0;
        m30 = m31 = m32 = 0;
        m00 = m11 = m22 = m33 = 1;
    }

    public void setPerspectiveProjection(double fov, double screenWidth) {
        double d = (screenWidth * 0.5) / Math.tan(fov * 0.5);
        setIdentity();
        m32 = -1 / d;
        m33 = 0;
    }
    
    // https://www.scratchapixel.com/code.php?id=4&origin=/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix
    public void setProjectionMatrix2(double angleOfView, double near, double far) { 
        double scale = 1 / Math.tan(angleOfView * 0.5 * Math.PI / 180); 
        m00 = scale; 
        m11 = scale; 
        m22 = -far / (far - near); 
        m32 = -far * near / (far - near); 
        m23 = -1; 
        m33 = 0; 
    } 

    // https://www.scratchapixel.com/code.php?id=4&origin=/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix&src=1
    // https://www.khronos.org/opengl/wiki/GluPerspective_code
    public void setPerspective( 
        double angleOfViewInDegrees, double imageAspectRatio, double n, double f) { 
        double scale = Math.tan(angleOfViewInDegrees * Math.PI / 360.0) * n; 
        double r = imageAspectRatio * scale;
        double l = -r; 
        double t = scale;
        double b = -t; 
        //setFrustum(b, t, l, r, n, f);
        setFrustum2(l, r, b, t, n, f);
    } 

    // https://www.khronos.org/opengl/wiki/GluPerspective_code
    void setFrustum2(double left, double right, double bottom, double top,
                      double znear, double zfar)
    {
        double temp, temp2, temp3, temp4;
        temp = 2.0 * znear;
        temp2 = right - left;
        temp3 = top - bottom;
        temp4 = zfar - znear;
        m00 = temp / temp2;
        m10 = 0.0;
        m20 = 0.0;
        m30 = 0.0;
        m01 = 0.0;
        m11 = temp / temp3;
        m21 = 0.0;
        m31 = 0.0;
        m02 = (right + left) / temp2;
        m12 = (top + bottom) / temp3;
        m22 = (-zfar - znear) / temp4;
        m32 = -1.0;
        m03 = 0.0;
        m13 = 0.0;
        m23 = (-temp * zfar) / temp4;
        m33 = 0.0;
    }

    // https://www.scratchapixel.com/code.php?id=4&origin=/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix&src=1
    // set OpenGL perspective projection matrix
    // b = bottom
    // t = top
    // l = left
    // r = right
    // n = near
    // f = far
    public void setFrustum(
            double b, double t, double l, double r, double n, double f) { 
        
        m00 = 2 * n / (r - l); 
        m10 = 0; 
        m20 = 0; 
        m30 = 0; 

        m01 = 0; 
        m11 = 2 * n / (t - b); 
        m21 = 0; 
        m31 = 0; 

        m02 = (r + l) / (r - l); 
        m12 = (t + b) / (t - b); 
        m22 = -(f + n) / (f - n); 
        m32 = -1; 

        m03 = 0; 
        m13 = 0; 
        m23 = -2 * f * n / (f - n); 
        m33 = 0; 
    } 

    // http://glasnost.itcarlow.ie/~powerk/GeneralGraphicsNotes/projection/viewport_transformation.html
    public void setViewportTransformation(double x, double y, double width, double height) {
        double left = x;
        double bottom = y + height;
        double right = x + width;
        double top = y;
        
        m00 = (right - left) / 2;
        m10 = 0; 
        m20 = 0; 
        m30 = 0; 

        m01 = 0; 
        m11 = (top - bottom) / 2;
        m21 = 0; 
        m31 = 0; 

        m02 = 0;
        m12 = 0;
        m22 = 0.5;
        m32 = 0;

        m03 = (right + left) / 2; 
        m13 = (top + bottom) / 2; 
        m23 = 0.5; 
        m33 = 1;         
    }

    public void setScale(double sx, double sy, double sz) {
        setIdentity();
        m00 = sx;
        m11 = sy;
        m22 = sz;
        m33 = 1;
    }
    
    public void setTranslation(double x, double y, double z) {
        setIdentity();
        m03 = x;
        m13 = y;
        m23 = z;
    }
    
    public void setRotationX(double angle) {
        setIdentity();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        m11 =  c; m12 = s;
        m21 = -s; m22 = c;
    }

    public void setRotationY(double angle) {
        setIdentity();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        m00 = c; m02 = -s;
        m20 = s; m22 =  c;
    }

    public void setRotationZ(double angle) {
        setIdentity();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        m00 =  c; m01 = s;
        m10 = -s; m11 = c;
    }
    
    public void multiply(Mat4 m) {
        double nm00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30;
        double nm01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31;
        double nm02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32;
        double nm03 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33;
        double nm10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30;
        double nm11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31;
        double nm12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32;
        double nm13 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33;
        double nm20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30;
        double nm21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31;
        double nm22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32;
        double nm23 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33;
        double nm30 = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30;
        double nm31 = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31;
        double nm32 = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32;
        double nm33 = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33;
        m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03;
        m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13;
        m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23;
        m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33;
    }

    public void multiply(Vec4 v) {
        double nx = m00 * v.x + m01 * v.y + m02 * v.z + m03 * v.w;
        double ny = m10 * v.x + m11 * v.y + m12 * v.z + m13 * v.w;
        double nz = m20 * v.x + m21 * v.y + m22 * v.z + m23 * v.w;
        double nw = m30 * v.x + m31 * v.y + m32 * v.z + m33 * v.w;
        v.x = nx;
        v.y = ny;
        v.z = nz;
        v.w = nw;
    }

    public void scale(double s) {
        m00 *= s; m01 *= s; m02 *= s; m03 *= s;
        m10 *= s; m11 *= s; m12 *= s; m13 *= s;
        m20 *= s; m21 *= s; m22 *= s; m23 *= s;
        m30 *= s; m31 *= s; m32 *= s; m33 *= s;
    }
    
    // http://www.cg.info.hiroshima-cu.ac.jp/~miyazaki/knowledge/teche23.html
    // http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
    public void invert() {
        double determinant = getDeterminant();
        if (determinant == 0) {
            throw new RuntimeException("Matrix is not invertible !");
        }
        double nm00 = m12*m23*m31 - m13*m22*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33;
        double nm01 = m03*m22*m31 - m02*m23*m31 - m03*m21*m32 + m01*m23*m32 + m02*m21*m33 - m01*m22*m33;
        double nm02 = m02*m13*m31 - m03*m12*m31 + m03*m11*m32 - m01*m13*m32 - m02*m11*m33 + m01*m12*m33;
        double nm03 = m03*m12*m21 - m02*m13*m21 - m03*m11*m22 + m01*m13*m22 + m02*m11*m23 - m01*m12*m23;
        double nm10 = m13*m22*m30 - m12*m23*m30 - m13*m20*m32 + m10*m23*m32 + m12*m20*m33 - m10*m22*m33;
        double nm11 = m02*m23*m30 - m03*m22*m30 + m03*m20*m32 - m00*m23*m32 - m02*m20*m33 + m00*m22*m33;
        double nm12 = m03*m12*m30 - m02*m13*m30 - m03*m10*m32 + m00*m13*m32 + m02*m10*m33 - m00*m12*m33;
        double nm13 = m02*m13*m20 - m03*m12*m20 + m03*m10*m22 - m00*m13*m22 - m02*m10*m23 + m00*m12*m23;
        double nm20 = m11*m23*m30 - m13*m21*m30 + m13*m20*m31 - m10*m23*m31 - m11*m20*m33 + m10*m21*m33;
        double nm21 = m03*m21*m30 - m01*m23*m30 - m03*m20*m31 + m00*m23*m31 + m01*m20*m33 - m00*m21*m33;
        double nm22 = m01*m13*m30 - m03*m11*m30 + m03*m10*m31 - m00*m13*m31 - m01*m10*m33 + m00*m11*m33;
        double nm23 = m03*m11*m20 - m01*m13*m20 - m03*m10*m21 + m00*m13*m21 + m01*m10*m23 - m00*m11*m23;
        double nm30 = m12*m21*m30 - m11*m22*m30 - m12*m20*m31 + m10*m22*m31 + m11*m20*m32 - m10*m21*m32;
        double nm31 = m01*m22*m30 - m02*m21*m30 + m02*m20*m31 - m00*m22*m31 - m01*m20*m32 + m00*m21*m32;
        double nm32 = m02*m11*m30 - m01*m12*m30 - m02*m10*m31 + m00*m12*m31 + m01*m10*m32 - m00*m11*m32;
        double nm33 = m01*m12*m20 - m02*m11*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 + m00*m11*m22;
        m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03;
        m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13;
        m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23;
        m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33;
        scale(1 / determinant);
    }

    public double getDeterminant() {
        return m03 * m12 * m21 * m30 - m02 * m13 * m21 * m30 - m03 * m11 * m22 * m30 + m01 * m13 * m22 * m30
             + m02 * m11 * m23 * m30 - m01 * m12 * m23 * m30 - m03 * m12 * m20 * m31 + m02 * m13 * m20 * m31
             + m03 * m10 * m22 * m31 - m00 * m13 * m22 * m31 - m02 * m10 * m23 * m31 + m00 * m12 * m23 * m31
             + m03 * m11 * m20 * m32 - m01 * m13 * m20 * m32 - m03 * m10 * m21 * m32 + m00 * m13 * m21 * m32
             + m01 * m10 * m23 * m32 - m00 * m11 * m23 * m32 - m02 * m11 * m20 * m33 + m01 * m12 * m20 * m33
             + m02 * m10 * m21 * m33 - m00 * m12 * m21 * m33 - m01 * m10 * m22 * m33 + m00 * m11 * m22 * m33;
    }
        
}
