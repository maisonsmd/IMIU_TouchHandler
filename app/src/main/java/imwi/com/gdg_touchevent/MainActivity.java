package imwi.com.gdg_touchevent;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements IMIUTouchHandler.OnActionRecorededHandler {
    private static final String TAG = "Main";

    private IMIUTouchHandler touchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchHandler = new IMIUTouchHandler(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchHandler.Handle(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void OnActionRecorded(Actions action, Position startPosition,  double velocity) {
        switch (action){
            case TOUCH:
                Log.i(TAG, "TOUCH");
                break;
            case MOVE:
                Log.i(TAG, "MOVE " + velocity);
                break;
            case SWIPE:
                Log.i(TAG, "SWIPE " + velocity);
                break;
        }
    }
}

