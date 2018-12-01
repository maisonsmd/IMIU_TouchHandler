package imwi.com.gdg_touchevent;

import android.view.MotionEvent;
import android.view.VelocityTracker;

class Position {
    int X;
    int Y;

    void Set(int _x, int _y) {
        X = _x;
        Y = _y;
    }

    void Set(Position _position) {
        X = _position.X;
        Y = _position.Y;
    }

    int DistanceTo(Position _position) {
        float x1 = X, y1 = Y;
        float x2 = _position.X, y2 = _position.Y;
        return (int) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }
}


enum Actions {
    TOUCH,
    MOVE,
    SWIPE,
    NO_ACTION
}

class IMIUTouchHandler {
    private static final String TAG = "TouchHandler";
    private Position lastLocation = new Position();
    private Position currentPosition = new Position();
    //the position that user first touch before any action
    private Position startTouchPostion = new Position();
    //radius that we accept movements like static touches
    private static final int TOUCH_RADIUS = 200;
    private boolean simpleTouch = true;
    private boolean moved = false;
    private VelocityTracker mVelocityTracker;

    private Position minPos = new Position();
    private Position maxPos = new Position();
    private Position midPos = new Position();
    private boolean lastXDir = true;
    private boolean lastYDir = true;
    private boolean swept = true;

    OnActionRecorededHandler mListener;

    Actions action;

    IMIUTouchHandler(OnActionRecorededHandler listener) {
        mListener = listener;
    }
    void Handle(MotionEvent motionEvent) {

        /*Log.d(TAG, motionEvent.toString());
        return;*/

        currentPosition.Set((int) motionEvent.getX(), (int) motionEvent.getY());

        switch (motionEvent.getAction()) {
            //just touched
            case MotionEvent.ACTION_DOWN:
                startTouchPostion.Set(currentPosition);
                simpleTouch = true;
                moved = false;

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(motionEvent);

                minPos.Set(5000,5000);
                maxPos.Set(-1,-1);
                break;
            //just released
            case MotionEvent.ACTION_UP:
                //action = Actions.TOUCH_UP;
                if (simpleTouch)
                    mListener.OnActionRecorded(Actions.TOUCH, startTouchPostion, 0);
                break;
            //moving
            case MotionEvent.ACTION_MOVE:
                if(currentPosition.X > maxPos.X)
                    maxPos.X = currentPosition.X;
                if(currentPosition.Y > maxPos.Y)
                    maxPos.Y = currentPosition.Y;

                if(currentPosition.X < minPos.X)
                    minPos.X = currentPosition.X;
                if(currentPosition.Y < minPos.Y)
                    minPos.Y = currentPosition.Y;

                midPos.X = (minPos.X + maxPos.X) / 2;
                midPos.Y = (minPos.Y + maxPos.Y) / 2;

                boolean currentXDir = currentPosition.X > midPos.X;
                boolean currentYDir = currentPosition.Y > midPos.Y;


                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);

                double xVelocity = mVelocityTracker.getXVelocity();
                double yVelocity = mVelocityTracker.getYVelocity();
                double velocity = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);

                if (startTouchPostion.DistanceTo(currentPosition) > TOUCH_RADIUS) {
                    //no more just a simple touch
                    simpleTouch = false;
                }

                if (!simpleTouch && !moved) {
                    mListener.OnActionRecorded(Actions.MOVE, startTouchPostion, velocity);
                    moved = true;

                    lastXDir = currentXDir;
                    lastYDir = currentYDir;

                    swept = false;
                }

                if(((lastXDir != currentXDir) || (lastYDir != currentYDir)) && !swept && moved){
                    mListener.OnActionRecorded(Actions.SWIPE, startTouchPostion, velocity);
                    swept = true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
        }
    }

    public interface OnActionRecorededHandler {
        void OnActionRecorded(Actions action, Position startPosition, double velocity);
    }

}
