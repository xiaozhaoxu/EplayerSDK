package com.jcraft.jzlib;


import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-1-14
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public class DataHelper {

    //正确的zLib界面方式
    public static String decompress(String content) {
        try {
            if(content.length()==0)
                return null;
            //采用Android系统自带Base64编解码类正常，原始版本删除最后一个文字时异常！
            // byte[] compressData = Base64.decode(content);
            byte[] compressData = android.util.Base64.decode(content, android.util.Base64.DEFAULT);
            byte[] bytes = ZipUtil.unZLib(compressData);
            return new String(bytes);
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return "";
//        return decompressZlib(content);
    }

    //采用zLib方式解码（目前解码有问题）
    public static String decompressZlib(String content) {
        int err;
        int uncomprLen = 4096;
        try {
            byte[] uncompr = new byte[uncomprLen];

            //采用Android系统自带Base64编解码类正常，原始版本删除最后一个文字时异常！
            //  byte[] compressData = Base64.decode(content);
            byte[] compressData = android.util.Base64.decode(content, android.util.Base64.DEFAULT);

            int comprLen = compressData.length;
            Inflater inflater = new Inflater();
            inflater.setOutput(uncompr);
            inflater.setInput(compressData);
            while (inflater.total_out < uncomprLen &&
                    inflater.total_in < comprLen) {
                inflater.avail_in = inflater.avail_out = 1; /* force small buffers */
                err = inflater.inflate(JZlib.Z_NO_FLUSH);
                if (err == JZlib.Z_STREAM_END) break;
                CHECK_ERR(inflater, err, "inflate");
            }
            err = inflater.end();
            CHECK_ERR(inflater, err, "inflateEnd");
//            System.out.println(inflater.total_out);
//            System.out.println(new String(uncompr,0,(int)inflater.total_out));
            return new String(uncompr, 0, (int) inflater.total_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //采用Deflate方式解压缩的处理（有问题）
    public static String decompressDeflate(String content) {
        System.out.println("DataHelper Source:" + content);
        try {
            int err = 0;
            int uncomprLen = 4096;
            byte[] uncompr = new byte[uncomprLen];
            String compressed = content;

            //采用Android系统自带Base64编解码类正常，原始版本删除最后一个文字时异常！
            // byte[] compressData = Base64.decode(compressed);
            byte[] compressData = android.util.Base64.decode(compressed, android.util.Base64.DEFAULT);

            System.out.println("DataHelper Base64 Decode:" + new String(compressData));

            int comprLen = compressData.length;
            Inflater inflater = new Inflater(-10, JZlib.W_ANY);
            inflater.setOutput(uncompr);
            inflater.setInput(compressData);
            err = inflater.inflate(JZlib.Z_FILTERED);
            if (err == JZlib.Z_STREAM_END) {
                System.out.println("DataHelper decompress err:" + err);
                err = inflater.end();
                CHECK_ERR(inflater, err, "inflateEnd");
                String temp = new String(uncompr, 2, (int) inflater.total_out - 2);
                return temp;
            } else {
                return null;
            }
//            while(inflater.total_out<uncomprLen &&
//                    inflater.total_in<comprLen) {
//                inflater.avail_in=inflater.avail_out=1; /* force small buffers */
//
//                if(err==JZlib.Z_STREAM_END) break;
//                CHECK_ERR(inflater, err, "inflate");
//            }

        } catch (Exception e) {
//            LogUtil.d("e.printStackTrace()"+e.toString());
            e.printStackTrace();
            return "";
        }

    }

    static void CHECK_ERR(ZStream z, int err, String msg) throws Exception {
        if (err != JZlib.Z_OK) {
            System.out.println("error occured when " + msg);
            if (z.msg != null) System.out.print(z.msg + " ");
            System.out.println(msg + " error: " + err);

            throw new Exception(msg + " error: " + err);
            //  System.exit(1);
        }
    }

    /**
     * 解码
     *
     * @param str
     * @return string
     */
    public static byte[] decode(String str) {
        byte[] bt = null;
        try {
            bt = android.util.Base64.decode(str, android.util.Base64.DEFAULT);
            // bt= Base64.decode(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bt;
    }

    public static void main(String[] args) {

        String decompress = "";


//        String content = "eNp1l01uFDEQRq/CASKr/W/vQWIRKRIbjsSCNQgOgbJCYsdlUJRj4PqqJ2OXv95k3pvMtKvKLtvz8cPj49O7z0+fHt+n7HJ+aMH5B58KOILn9/P5fqSc3ZG5FBcupBHqLjDuh0uUR2yZy4juQrKrlIvrjP2R8MJtfPDS+vyQWXyimO8JG6nzmMb6POZqwd/TM5KmObJWKTbXuEQ/1d1amCq/W98t4SVmLC4mVWOl1nR9MUseBWUSKSbNg1qeYzVW0S5M2pTGIvmYs7iZ1oVbkbnM3rVNqqyzHBfqrsh3E5KeZTwuqDQr3iWRsmLE/yqKscoZe3V1t+b0b7RSin6gOyv1kPoQzq5Q7ijCxmOXaplL04ru0rHjES6oWUNus4Tj0NxQC2sB0ZSFuqYaF/ThPpFGktPJL5volqrrwlrFxI2FUK30s38l+EXCcVvMdbeAUEeTLHhurtJSRrLUTtvQiMYZkwRDJWg01A6Zq93abWMy0tAyY3yp5GbSDCPqTXD6jPx3HsXyDVPLJOiDd8EJwLhockSSpkYkoL47e0Phode3FE5EmmP5RsoZs7DygSO0Zyvy/9ZR5AW9Ht4Ljq/IQb9AzJawrFrRsi3iz6tF2UTuA4SxrJv0isFAcOQrF5q+IloG95yVEVIL6KSZMTBhpOwx0B218duhG8giCIEwxqr4qBGMRiVJ3FQktsHeMga7kEjFdzyy6iZqxV9IIzSqTFlT4nJcSJT9gQqyJSzzwngUgXGb01kFL1QqCspkTOgFB8py+jHWSWaC4+9CwoWEqdhWPOWpcgvLKU5Yl9C5kFbRwjGp92W5SbwQ1I5wJhTP5tqkYLCmJ/wq5WzhXbL28M6EkuxghFH6Czku5MDeRCTLDYmxXM4Yy12Sc8xc9DcckzOrbjm87XSWJSXvLONauvO44Prbj9Yd6+vfny8/nv/9+fXy/cvL76+vz9/+A31V+7w=";
        String content = "eNp9l1F2xSAIRDfU4xGNGve/serg62MI6U87t406IGIi407Xj7Q73e1HxgRMBfrPgUBV+4CFaw0N9UjSYsjpiqGa8arvnPL6d71Sf4GaRnuQ4Fe5Iyp7dOmpvEC1jx2o21EpWIOhJfkRGWk6PfZwaak2BzPjKR0egaTitPrPA1l3BJu54zmr4SVfqT30cpeLDiaYSEIW7BRRyXCzftYA5s962MntZN6pe72dzMFiDeq6itU7ZZGGrwgEy68cs8TqO3SSFwY+9a68hyw1lHDrZd3Jecr1QIv0/K5t9YX0aFyk+977ubbe6XsfopNkBp0Vu2J1y9vFZxcdaR3p1jN0rb1dYg6wqOR1xjycSnpC11VR5A60xqXisQhwljxVLFtkr+EJuyxlpfdJOB9rE69Qd50ggH3SA702/gq1mMGVfNWy/vYGsz1IY4sJGxaCpviNJIY2zBQM7TODeDhuETzD6cVIF4GW7QbxYFqxA41Be7GjCqsFq5LWso1AQxAUJ4OGEMEpWxwCD/JXmgRFgxOtvphQ7Ax6iTw1mrheIQ+QF8AVpmeNNJIr2gYCyOgdVqNNnsvIwWkPg6VeOciQ0/tS0QQxaLqzadYfUB9PnTFvRQP0IJ8LjyGfxhSShlVIYQecOtcpqYvuUSbbWhnOHX/uWCYN6Al/d3ZI6jyCZqdwIC9w0dxMGkgEZNbCTV4dWbMMw0zOYMNgIK+Oqp2cwIbBYPfGE4VoYVCEjsZuBC/U7fyOGl4iYzq+CktzNB6A0RH1+T2CDnRz9DWX4UQR0l9zIHn87yYbw8SSBKcxa8yOtMvpqy5DgZt/qWJhgvPmga79QgPeHCHN+o3gdG7/ggYfwdRVic6LFi62GPSbw9FtPm1imKHWz6QINCUR7Ls91jPS+sIUQvmOJm2NMOALMtDGB2l8iwZ6mrFGX7K+PSJt3Dk9Wwz1uxrpbma1+o5UCvV662+hNo5JF3waRnChNUTQUqw7jmUEw05McJOapwRiifMb09yHh+CcnY4khCDaRTxNtIXitJ74XkN5mfZxQBvLP9D0FxOaWAja+WLShvlG8wW0Mcc0yBXTbW0RTHJFdO6bN5ovINaVI5ueCJBt0iczNZTYZoaTk10Zv3TMw+8=";

        byte[] compressData = Base64.decode(content);


//        decompress = decompress(content);

        try {
            byte[] bytes = ZipUtil.unZLib(compressData);
            decompress = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            ByteArrayInputStream in = new ByteArrayInputStream(compressData);
//            ZInputStream zIn = new ZInputStream(in);
////            DataInputStream objIn = new DataInputStream(zIn);
//            ObjectInputStream objIn = new ObjectInputStream(zIn);
//
////            System.out.println(objIn.readByte());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        String[] split = decompress.split(",");
        int index = 1;
        String x = "";
        for (String point : split) {

            if (index % 2 == 0) {
                System.out.println("Line Point X:" + x + "--Y:" + point);
            } else {
                x = point;
            }
            index++;
        }


//        try{
//            String hello = "Hello World!";
//
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            ZOutputStream zOut = new ZOutputStream(out, JZlib.Z_DEFAULT_COMPRESSION);
//            ObjectOutputStream objOut = new ObjectOutputStream(zOut);
//            objOut.writeObject(hello);
//            zOut.close();
//
//            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
//            ZInputStream zIn = new ZInputStream(in);
//            ObjectInputStream objIn = new ObjectInputStream(zIn);
//            System.out.println(objIn.readObject());
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }


    }


}
