package com.example.gj.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import static org.opencv.imgproc.Imgproc.resize;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Mat source,dest,backGround,foreFround,output;
    int distance=750;
    private static final String TAG = "MainActivity";

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Create and set View
                    source=new Mat();
                    dest=new Mat();


                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };



    ImageView imageView1,imageView2;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mOpenCVCallBack))
        {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }





        //System.loadLibrary("opencv_java330");
        Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.bbb);
        imageView1=(ImageView)findViewById(R.id.imageView1);
        //imageView2=(ImageView)findViewById(R.id.imageView2);
        btn =(Button)findViewById(R.id.button);
        imageView1.setImageBitmap(img);
        Log.d(TAG,"00");
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {



        Log.d(TAG,"001");
        Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.aaa);
        Bitmap img1 = BitmapFactory.decodeResource(getResources(),R.drawable.aaa1);
        Bitmap img3 = BitmapFactory.decodeResource(getResources(),R.drawable.bbb);
        Bitmap img4 = BitmapFactory.decodeResource(getResources(),R.drawable.bbb1);
        Bitmap img5 = BitmapFactory.decodeResource(getResources(),R.drawable.output);
        Bitmap bimg= img.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bimg1 = img1.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bimg3 = img3.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bimg4 = img4.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bimg5 = img5.copy(Bitmap.Config.ARGB_8888, true);
        //Rect roi = new Rect(300,50,50,10);
        source = new Mat();
        Utils.bitmapToMat( bimg, source);

        Log.d(TAG,"002");
        dest = new Mat();
        Utils.bitmapToMat( bimg1, dest);

        backGround=new Mat();
        Utils.bitmapToMat( bimg3, backGround);

        foreFround=new Mat();
        Utils.bitmapToMat( bimg4, foreFround);

        output=new Mat();
        Utils.bitmapToMat( bimg5, output);


        Point location=new Point(10,20) ;

        Utils.matToBitmap(overlayImage(backGround,foreFround,output,location),bimg5);
        //Utils.matToBitmap(overlayImage(backGround,resizeMask(dest,distance),foreFround,location),bimg5);
        imageView1.setImageBitmap(bimg5);


        /*backGround.copyTo(output);

        for(int y = (int) Math.max(location.y , 0); y < backGround.rows(); ++y) {
            int fY = (int) (y - location.y);
            Log.d(TAG,"yoooYYY "+ fY);

            if (fY >= foreFround.rows())
                break;


            for (int x = (int) Math.max(location.x, 0); x < backGround.cols(); ++x) {

                Log.d(TAG,"Location "+ location.x);

                int fX = (int) (x - location.x);

                if (fX >= foreFround.cols()) {
                    break;
                }

                double opacity;
                double[] finalPixelValue = new double[4];

                opacity = foreFround.get(fY, fX)[3];

                finalPixelValue[0] = backGround.get(y, x)[0];
                finalPixelValue[1] = backGround.get(y, x)[1];
                finalPixelValue[2] = backGround.get(y, x)[2];
                finalPixelValue[3] = backGround.get(y, x)[3];

                for(int c = 0;  c < output.channels(); ++c){
                    if(opacity > 0){
                        double foregroundPx =  foreFround.get(fY, fX)[c];
                        double backgroundPx =  backGround.get(y, x)[c];

                        float fOpacity = (float) (opacity / 255);
                        finalPixelValue[c] = ((backgroundPx * ( 1.0 - fOpacity)) + (foregroundPx * fOpacity));
                        if(c==3){
                            finalPixelValue[c] = foreFround.get(fY,fX)[3];
                        }
                    }
                }
                output.put(y, x,finalPixelValue);


            }
        }
        Bitmap front_bitmap= Bitmap.createBitmap(backGround.cols(),backGround.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(output, front_bitmap);*/

        //imageView1.setImageBitmap(front_bitmap);





        /*Log.d(TAG,"003");
        source = new Mat();
        Log.d(TAG,"002");
        dest = new Mat();
        Utils.bitmapToMat( bimg, source);
        Utils.bitmapToMat( bimg1, dest);
        Bitmap back_bitmap
                = Bitmap.createBitmap(dest.cols(), dest.rows(),
                Bitmap.Config.ARGB_8888);  // Make an empty Bitmap
        Bitmap front_bitmap= Bitmap.createBitmap(source.cols(),source.rows(),Bitmap.Config.ARGB_8888);

        Mat mask2 = new Mat( new Size( dest.cols(), dest.rows() ),
                CvType.CV_8UC1 );
        mask2.setTo( new Scalar( 0 ) );
        Size sizeMask = mask2.size();
        double[] data;
        for (int i=0;i<sizeMask.height;i++ )
            for (int j=0;j<sizeMask.width;j++ ) {
                data = dest.get(i, j);
                if (data[0] + data[1] + data[2] <650)
                    mask2.put(i, j, data);
            }
            int x=10;
            int y=12;
        //dest.copyTo(source(cv:Rect(x,y,dest.cols(), dest.rows())));
       // dest.copyTo(source(cv::(roi));



        //dest.copyTo(source.submat(y,dest.rows(),x,dest.cols()));
        //
        dest.copyTo(source, mask2);
        Utils.matToBitmap(source, back_bitmap);

        imageView1.setImageBitmap(back_bitmap);*/






        // System.out.println("dest value"+dest.dump());
        //Log.d(TAG,"dest value"+dest.dump());
        /*Log.d(TAG,"4  "+dest.cols());
        int count =0;
        Log.d(TAG,"Count"+count);
        for (int i =0;i<source.rows();i++){
            for (int j =0;i<source.cols();j++){
                Log.d(TAG,"4"+i);



                double[] data= source.get(i,j);
                double[] mask= dest.get(i,j);
                for(int k = 0; k < 3; k++){
                    Log.d(TAG,"5  "+mask[k]);
                    if(mask[k]<255){

                        data[k]=mask[k];
                        Log.d(TAG,"5  "+data[k]);
                        count++;
                        //Log.d(TAG,"Count"+count);
                    }

                }*/
        // dest.put(i, j, data);



        //}
        //Log.d(TAG,"7");


        //


        //Imgproc.cvtColor(source, dest, Imgproc.COLOR_RGB2GRAY);
        //Bitmap btmp = Bitmap.createBitmap(dest.width(),dest.height(),Bitmap.Config.ARGB_8888);
        //Utils.matToBitmap(dest,bimg1);

        //imageView1.setImageBitmap (bimg1);

    }

    static {
        System.loadLibrary("native-lib");
    }
    public static Mat overlayImage(Mat background,Mat foreground,Mat output, Point location){
        Log.d(TAG,"004");

        background.copyTo(output);

        for(int y = (int) Math.max(location.y , 0); y < background.rows(); ++y){

            int fY = (int) (y - location.y);

            if(fY >= foreground.rows())
                break;

            for(int x = (int) Math.max(location.x, 0); x < background.cols(); ++x){
                int fX = (int) (x - location.x);
                if(fX >= foreground.cols()){
                    break;
                }

                double opacity;
                double[] finalPixelValue = new double[4];

                opacity = foreground.get(fY , fX)[3];
                if(opacity>100){
                    Log.d(TAG,"opacity "+opacity);}

                finalPixelValue[0] = background.get(y, x)[0];
                finalPixelValue[1] = background.get(y, x)[1];
                finalPixelValue[2] = background.get(y, x)[2];
                finalPixelValue[3] = background.get(y, x)[3];
                Log.d(TAG,"008");

                for(int c = 0;  c < output.channels(); ++c){
                    if(opacity > 0){
                        double foregroundPx =  foreground.get(fY, fX)[c];
                        double backgroundPx =  background.get(y, x)[c];

                        double fOpacity =  (opacity / 255);
                        finalPixelValue[c] = ((backgroundPx * ( 1.0 - fOpacity)) + (foregroundPx * fOpacity));
                        if(c==3){
                            finalPixelValue[c] = foreground.get(fY,fX)[3];

                            Log.d(TAG,"009  "+finalPixelValue[c]);
                        }
                    }
                }
                Log.d(TAG,"007");
                output.put(y, x,finalPixelValue);
            }
        }
        Log.d(TAG,"005");
        return output;
    }
    public static Mat resizeMask(Mat mask, int x){

        Size size = new Size(x,x);

        Mat resizeImage =new Mat( new Size( x, x ), CvType.CV_8UC1 );

        resize(mask,resizeImage,size);


        return resizeImage;
    }




}