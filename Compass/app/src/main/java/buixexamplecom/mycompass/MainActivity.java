package buixexamplecom.mycompass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
public static final String LOG_CODE="Glacio";
    private SensorManager mSensorManager;
    private Sensor mSensorLight,mSensorCompass,mSensorAccelermeter,getmSensorTemperature;
    private ImageView mImageView_compass;
    TextView mtextView_LightSenser,mTextView_Temperature,mTextView_CompassAxis_X,mTextView_CompassAxis_Y,mTextView_CompassAxis_Z,mTextView_AccelerAxis_X,mTextView_AccelerAxis_Y,mTextView_AccelerAxis_Z;
    String Listsensor;
    double CompassAxis_X,CompassAxis_Y,CompassAxis_Z;
    double AccelerAxis_X,AccelerAxis_Y,AccelerAxis_Z;
    double LightSensor,Temperrature;

    private float lastDataGeomagnetic[] = new float[ 3];
    private float lastDataAccelerometer[] = new float[ 3];
    private boolean dataMagOK = false;
    private boolean dataAccOK = false;
    private boolean dataLightOK=false;
    private float[] mR = new float[ 9];
    private float[] mOrientation = new float[ 3];
    private float mCurrentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager=(SensorManager)this.getSystemService(SENSOR_SERVICE);
        SetupGui();

        //get list sensor on the phone
        List<Sensor>sensorList=mSensorManager.getSensorList(Sensor.TYPE_ALL);
        mSensorLight=mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorCompass=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorAccelermeter=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        getmSensorTemperature=mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener,mSensorCompass, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensorEventListener,mSensorAccelermeter,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensorEventListener,mSensorLight,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensorEventListener,getmSensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void SetupGui(){
        mtextView_LightSenser=(TextView)findViewById(R.id.textViewLigth);
        mTextView_Temperature=(TextView)findViewById(R.id.textView_Temp_sensor);
        mTextView_CompassAxis_X=(TextView)findViewById(R.id.textViewAxis_X_Magnetic);
        mTextView_CompassAxis_Y=(TextView)findViewById(R.id.textViewAxis_Y_Magnetic);
        mTextView_CompassAxis_Z=(TextView)findViewById(R.id.textViewAxis_Z_Magnetic);
        mTextView_AccelerAxis_X=(TextView)findViewById(R.id.textViewAxis_X_Acceler);
        mTextView_AccelerAxis_Y=(TextView)findViewById(R.id.textViewAxis_Y_Acceler);
        mTextView_AccelerAxis_Z=(TextView)findViewById(R.id.textViewAxis_Z_Acceler);
         mImageView_compass=(ImageView)findViewById(R.id.imageView_Compass);


    }
    SensorEventListener sensorEventListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
                if(event. sensor == mSensorAccelermeter)
                {
                    System. arraycopy(event. values, 0, lastDataAccelerometer, 0, 3);
                    AccelerAxis_X=event.values[0];
                    AccelerAxis_Y=event.values[1];
                    AccelerAxis_Z=event.values[2];

                    // setup values up to textview
                    mTextView_AccelerAxis_X.setText((float)AccelerAxis_X+"");
                    mTextView_AccelerAxis_Y.setText((float)AccelerAxis_Y+"");
                    mTextView_AccelerAxis_Z.setText((float)AccelerAxis_Z+"");

                    dataAccOK = true;
                }
                if(event. sensor == mSensorCompass)
                {
                    System. arraycopy(event. values, 0, lastDataGeomagnetic, 0, 3);
                    CompassAxis_X=event.values[0];
                    CompassAxis_Y=event.values[1];
                    CompassAxis_Z=event.values[2];

                    mTextView_CompassAxis_X.setText((float)CompassAxis_X+"");
                    mTextView_CompassAxis_Y.setText((float)CompassAxis_Y+"");
                    mTextView_CompassAxis_Z.setText((float)CompassAxis_Z+"");
                    dataMagOK = true;
                }
                if (event.sensor==mSensorLight)
                {
                   LightSensor=event.values[0];
                    mtextView_LightSenser.setText((float)LightSensor+"");

                }
                if (event.sensor==getmSensorTemperature)
                {
                    Temperrature=event.values[0];
                    mTextView_Temperature.setText((int)Temperrature+"");
                }

                if(dataAccOK && dataMagOK) {
//Determine rota
                    //Determine rota
                    SensorManager.getRotationMatrix(mR, null, lastDataAccelerometer, lastDataGeomagnetic);
                    SensorManager.getOrientation(mR, mOrientation);
                    float azimuthInRadians = mOrientation[0];
                    float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
                    Log.d(LOG_CODE,"degress: "+azimuthInDegress);

                    //Rota
                    RotateAnimation rotateAnimation = new RotateAnimation(mCurrentDegree, - azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(100);
                    rotateAnimation.setFillAfter(true);
                    mImageView_compass.startAnimation(rotateAnimation);
                    mCurrentDegree = - azimuthInDegress; }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(sensorEventListener, mSensorLight);
        mSensorManager.unregisterListener(sensorEventListener, mSensorAccelermeter);
        mSensorManager.unregisterListener(sensorEventListener, mSensorCompass);
        mSensorManager.unregisterListener(sensorEventListener,getmSensorTemperature);
    }
}
