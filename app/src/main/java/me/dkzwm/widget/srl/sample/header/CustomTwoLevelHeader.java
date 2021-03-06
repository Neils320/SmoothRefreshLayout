package me.dkzwm.widget.srl.sample.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.TwoLevelSmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.TwoLevelRefreshView;
import me.dkzwm.widget.srl.indicator.IIndicator;
import me.dkzwm.widget.srl.indicator.ITwoLevelIndicator;
import me.dkzwm.widget.srl.sample.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by dkzwm on 2017/6/12.
 *
 * @author dkzwm
 */

public class CustomTwoLevelHeader extends FrameLayout implements TwoLevelRefreshView {
    private static final byte STATUS_PULL_DOWN = 1;
    private static final byte STATUS_RELEASE_TO_REFRESH = 2;
    private static final byte STATUS_TWO_LEVEL_REFRESH_HINT = 4;
    private static final byte STATUS_TWO_LEVEL_RELEASE_TO_REFRESH = 5;
    protected TextView mTextViewTitle;
    private byte mStatus = STATUS_PULL_DOWN;


    public CustomTwoLevelHeader(Context context) {
        this(context, null);
    }

    public CustomTwoLevelHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTwoLevelHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View header = LayoutInflater.from(context).inflate(R.layout.layout_custom_two_level_header, this);
        mTextViewTitle = (TextView) header.findViewById(R.id.textView_two_level_header_title);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    @Override
    public int getStyle() {
        return STYLE_DEFAULT;
    }

    @Override
    public int getCustomHeight() {
        return 0;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onReset(SmoothRefreshLayout frame) {
        if (frame.isEnabledPullToRefresh()) {
            mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_pull_down_to_refresh);
        } else {
            mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_pull_down);
        }
    }

    @Override
    public void onRefreshPrepare(SmoothRefreshLayout frame) {
        if (frame.isEnabledPullToRefresh()) {
            mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_pull_down_to_refresh);
        } else {
            mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_pull_down);
        }
    }

    @Override
    public void onFingerUp(SmoothRefreshLayout layout, IIndicator indicator) {

    }

    @Override
    public void onRefreshBegin(SmoothRefreshLayout frame, IIndicator indicator) {
        mTextViewTitle.setVisibility(VISIBLE);
        mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_refreshing);
    }


    @Override
    public void onRefreshComplete(SmoothRefreshLayout frame, boolean isSuccessful) {
        mTextViewTitle.setVisibility(VISIBLE);
        mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_refresh_complete);
    }


    @Override
    public void onRefreshPositionChanged(SmoothRefreshLayout layout, byte status, ITwoLevelIndicator indicator) {
        final int currentPos = indicator.getCurrentPosY();
        if (layout instanceof TwoLevelSmoothRefreshLayout) {
            TwoLevelSmoothRefreshLayout refreshLayout = (TwoLevelSmoothRefreshLayout) layout;
            Log.d(getClass().getSimpleName(),"-------------:"+refreshLayout
                    .isDisabledPerformRefresh());
            if (!refreshLayout.isDisabledTwoLevelRefresh()) {
                final int offSetToHintTwoLevelRefresh = indicator.getOffsetToHintTwoLevelRefresh();
                final int offSetToTwoLevelRefresh = indicator.getOffsetToTwoLevelRefresh();
                if (currentPos < offSetToTwoLevelRefresh && currentPos >= offSetToHintTwoLevelRefresh
                        && status == SmoothRefreshLayout.SR_STATUS_PREPARE) {
                    if (mStatus != STATUS_TWO_LEVEL_REFRESH_HINT) {
                        mTextViewTitle.setText(R.string.continue_pull_down_to_have_a_surprise);
                        mStatus = STATUS_TWO_LEVEL_REFRESH_HINT;
                    }
                    return;
                } else if (currentPos > offSetToTwoLevelRefresh
                        && status == SmoothRefreshLayout.SR_STATUS_PREPARE) {
                    if (mStatus != STATUS_TWO_LEVEL_RELEASE_TO_REFRESH) {
                        mStatus = STATUS_TWO_LEVEL_RELEASE_TO_REFRESH;
                        if (!layout.isEnabledPullToRefresh()) {
                            mTextViewTitle.setText(R.string.release_your_finger_to_get_surprise);
                        }
                    }
                    return;
                }
            }
        }
        final int mOffsetToRefresh = indicator.getOffsetToRefresh();
        if (currentPos < mOffsetToRefresh && mStatus != STATUS_PULL_DOWN
                && status == SmoothRefreshLayout.SR_STATUS_PREPARE) {
            mStatus = STATUS_PULL_DOWN;
            if (layout.isEnabledPullToRefresh()) {
                mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_pull_down_to_refresh);
            } else {
                mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_pull_down);
            }
        } else if (currentPos > mOffsetToRefresh && mStatus != STATUS_RELEASE_TO_REFRESH
                && indicator.hasTouched() && status == SmoothRefreshLayout.SR_STATUS_PREPARE) {
            mStatus = STATUS_RELEASE_TO_REFRESH;
            if (!layout.isEnabledPullToRefresh()) {
                mTextViewTitle.setText(me.dkzwm.widget.srl.R.string.sr_release_to_refresh);
            }
        }
    }

    @Override
    public void onTwoLevelRefreshBegin(TwoLevelSmoothRefreshLayout layout, ITwoLevelIndicator twoLevelIndicator) {
        mTextViewTitle.setText(R.string.welcome_to_secondary_menu);
    }


}
