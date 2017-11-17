package Tools;

public class mypolyfit {
    /**
     * <p>函数功能：最小二乘法曲线拟合</p>
     * @param x 实型一维数组，长度为 n 。存放给定 n 个数据点的　X　坐标
     * @param y 实型一维数组，长度为 n 。存放给定 n 个数据点的　Y　坐标
     * @param n 变量。给定数据点的个数
     * @param m 拟合多项式的项数，即拟合多项式的最高次数为 m-1.
     *          要求 m<=n 且m<=20。若 m>n 或 m>20 ，则本函数自动按 m=min{n,20} 处理.
     * <p>Date:2007-12-25 16:21 PM</p>
     * @author qingbao-gao
     * @return
     */
    public static double[] PolyFit(double x[], double y[], int n, int m)
    {
        int i, j, k;
        double z, p, c, g, q = 0, d1, d2;
        double []s=new double[20];
        double []t=new double[20];
        double[] b=new double[20];
        double[] a=new double[m];
        double[]dt=new double[3];
        for (i = 0; i <= m-1; i++)
        {
            a[i] = 0.0;
        }
        if (m > n)
        {
            m = n;
        }
        if (m > 20)
        {
            m = 20;
        }
        z = 0.0;
        for (i = 0; i <= n-1; i++)
        {
            z = z+x[i]/(1.0 *n);
        }
        b[0] = 1.0;
        d1 = 1.0 * n;
        p = 0.0;
        c = 0.0;
        for (i = 0; i <= n-1; i++)
        {
            p = p+(x[i]-z);
            c = c+y[i];
        }
        c = c/d1;
        p = p/d1;
        a[0] = c * b[0];
        if (m > 1)
        {
            t[1] = 1.0;
            t[0] = -p;
            d2 = 0.0;
            c = 0.0;
            g = 0.0;
            for (i = 0; i <= n-1; i++)
            {
                q = x[i]-z-p;
                d2 = d2+q * q;
                c = c+y[i] *q;
                g = g+(x[i]-z) *q * q;
            }
            c = c/d2;
            p = g/d2;
            q = d2/d1;
            d1 = d2;
            a[1] = c * t[1];
            a[0] = c * t[0]+a[0];
        }
        for (j = 2; j <= m-1; j++)
        {
            s[j] = t[j-1];
            s[j-1] = -p * t[j-1]+t[j-2];
            if (j >= 3)
                for (k = j-2; k >= 1; k--)
                {
                    s[k] = -p * t[k]+t[k-1]-q * b[k];
                }
            s[0] = -p * t[0]-q * b[0];
            d2 = 0.0;
            c = 0.0;
            g = 0.0;
            for (i = 0; i <= n-1; i++)
            {
                q = s[j];
                for (k = j-1; k >= 0; k--)
                {
                    q = q *(x[i]-z)+s[k];
                }
                d2 = d2+q * q;
                c = c+y[i] *q;
                g = g+(x[i]-z) *q * q;
            }
            c = c/d2;
            p = g/d2;
            q = d2/d1;
            d1 = d2;
            a[j] = c * s[j];
            t[j] = s[j];
            for (k = j-1; k >= 0; k--)
            {
                a[k] = c * s[k]+a[k];
                b[k] = t[k];
                t[k] = s[k];
            }
        }
        dt[0] = 0.0;
        dt[1] = 0.0;
        dt[2] = 0.0;
        for (i = 0; i <= n-1; i++)
        {
            q = a[m-1];
            for (k = m-2; k >= 0; k--)
            {
                q = a[k]+q *(x[i]-z);
            }
            p = q-y[i];
            if (Math.abs(p) > dt[2])
            {
                dt[2] = Math.abs(p);
            }
            dt[0] = dt[0]+p * p;
            dt[1] = dt[1]+Math.abs(p);
        }
        return a;
    }
    /**
     * <p>对X轴数据节点球平均值</p>
     * @param x 存储X轴节点的数组
     * <p>Date:2007-12-25 20:21 PM</p>
     * @author qingbao-gao
     * @return  平均值
     */
    public static double ave(double []x)
    {
        double ave=0;
        double sum=0;
        if(x!=null)
        {
            for(int i=0;i<x.length;i++)
            {
                sum+=x[i];
            }
            System.out.println("sum-->"+sum);
            ave=sum/x.length;
            System.out.println("ave"+ave+"x.length"+x.length);
        }
        return ave;
    }
    /**
     * <p>由X值获得Y值</p>
     * @param x  当前X轴输入值,即为预测的月份
     * @param xx 当前X轴输入值的前X数据点
     * @param a  存储多项式系数的数组
     * @param m  存储多项式的最高次数的数组
     * <p>Date:2007-12-25 PM 20:07</p>
     * <P>Author:qingbao-gao</P>
     * @return   对应X轴节点值的Y轴值
     */
    public static double getY(double x,double[]xx,double[]a,int m)
    {
        double y=0;
        double ave=ave(xx);

        double l=0;
        for(int i=0;i<m;i++)
        {
            l=a[0];
            if(i>0)
            {
                y+=a[i]*Math.pow((x-ave),i );
                System.out.println(i+"--|-->"+y+"--a[i]--"+a[i]);
            }
            System.out.println("a[0]|"+a[0]);
        }
        System.out.println("l--|"+(l));
        return (y+l);
    }
    //--------------------------------------------测试代码
    public static void main(String []args)
    {

        double []x={200401,200402,200403,200404,200405,200406,200407,200408,200409,2004010,2004011,2004012,200501,200502,200503,200504};
        double []y={51,51,53,53,54,55,57,60,63,64,66,66,69,71,72,75};
        double[]a=new double[20];
        double[]aa= PolyFit(x,  y,  16, 3);
        double yy=0;
        System.out.println("拟合-->"+getY(200505,x,aa,3));

    }
}
