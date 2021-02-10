package no.nordicsemi.android.mesh.transport;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.utils.SecureUtils;

/**
 * To be used as a wrapper class when creating a GenericOnOffSet message.
 */
@SuppressWarnings("unused")
public class GenericOnOffSet extends GenericMessage {

    private static final String TAG = GenericOnOffSet.class.getSimpleName();
    private static final int OP_CODE = ApplicationMessageOpCodes.GENERIC_ON_OFF_SET;
    private static final int GENERIC_ON_OFF_SET_TRANSITION_PARAMS_LENGTH = 33;   //4
    private static final int GENERIC_ON_OFF_SET_PARAMS_LENGTH = 2;    //2

    private final Integer mTransitionSteps;
    private final Integer mTransitionResolution;
    private final Integer mDelay;
    private final boolean mState;
    private final int tId;

    private byte x;                  //x
    private byte y;                  //y
    private byte w;                  //w
    private byte h;                  //h
    private boolean color;            //esl顏色  black & white, false = red & white
    private int n;                    //第n包封包
    private final byte[] esl;         //esl資料


    /**
     * Constructs GenericOnOffSet message.
     *
     * @param appKey {@link ApplicationKey} key for this message
     * @param state  Boolean state of the GenericOnOffModel
     * @param tId    Transaction id
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public GenericOnOffSet(@NonNull final ApplicationKey appKey,
                           final boolean state,
                           final int tId, final byte x, final byte y, final byte w, final byte h, boolean color, final int n , final byte[] esl) throws IllegalArgumentException {
        this(appKey, state, tId, null, null, null, x, y, w, h, color, n,esl);
    }

    /**
     * Constructs GenericOnOffSet message.
     *
     * @param appKey               {@link ApplicationKey} key for this message
     * @param state                Boolean state of the GenericOnOffModel
     * @param tId                  Transaction id
     * @param transitionSteps      Transition steps for the level
     * @param transitionResolution Transition resolution for the level
     * @param delay                Delay for this message to be executed 0 - 1275 milliseconds
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public GenericOnOffSet(@NonNull final ApplicationKey appKey,
                           final boolean state,
                           final int tId,
                           @Nullable final Integer transitionSteps,
                           @Nullable final Integer transitionResolution,
                           @Nullable final Integer delay,
                           final byte x,
                           final byte y,
                           final byte w,
                           final byte h,
                           boolean color,
                           final int n,
                           final byte[] esl) {
        super(appKey);
        this.mTransitionSteps = transitionSteps;
        this.mTransitionResolution = transitionResolution;
        this.mDelay = delay;
        this.mState = state;
        this.tId = tId;
        this.x = x;   //add
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
        this.n = n;
        this.esl = esl;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey.getKey());
        final ByteBuffer paramsBuffer;
        Log.v(TAG, "State: " + (mState ? "ON" : "OFF"));

        if ( w == 0 && h == 0) {
            paramsBuffer = ByteBuffer.allocate(GENERIC_ON_OFF_SET_PARAMS_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
            paramsBuffer.put((byte) (mState ? 0x01 : 0x00));
            paramsBuffer.put((byte) tId);

        } else {
            Log.v(TAG, "Transition steps: " + mTransitionSteps);
            Log.v(TAG, "Transition step resolution: " + mTransitionResolution);
            paramsBuffer = ByteBuffer.allocate(GENERIC_ON_OFF_SET_TRANSITION_PARAMS_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
            paramsBuffer.put((byte) (mState ? 0x01 : 0x00));
            paramsBuffer.put((byte) tId);
            //paramsBuffer.put((byte) (mTransitionResolution << 6 | mTransitionSteps));
            //final int delay = mDelay;
            //paramsBuffer.put((byte) delay);
            paramsBuffer.put((byte) (x + 256));
            paramsBuffer.put((byte) (y + 256));
            paramsBuffer.put((byte) (w + 256));
            paramsBuffer.put((byte) (h + 256));
            paramsBuffer.put((byte) (color ? 0x01 : 0x00));
            paramsBuffer.putShort((short) (n + 65536));

            if(esl.length - n < 24){
                int left = esl.length - n;
                for(int i = 0; i < 24 ; i++) {
                    if(i < left)
                        paramsBuffer.put((byte) esl[n + i]);    //add
                    else{
                        if(color)
                            paramsBuffer.put((byte) 0xFF);    //add
                        else
                            paramsBuffer.put((byte) 0x00);    //add
                    }
                }
            }
            else{
                for(int i = 0; i < 24 ; i++)
                    paramsBuffer.put((byte) esl[n + i]);    //add
            }
        }
        mParameters = paramsBuffer.array();
    }
}
