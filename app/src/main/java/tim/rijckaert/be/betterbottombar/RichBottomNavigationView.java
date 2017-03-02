package tim.rijckaert.be.betterbottombar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.support.design.widget.TabLayout.Tab.INVALID_POSITION;

public class RichBottomNavigationView extends BottomNavigationView {
    private ViewGroup mBottomItemsHolder;
    private int mLastSelection = INVALID_POSITION;
    private Drawable mShadowDrawable;
    private boolean mShadowVisible = true;
    private int mWidth;
    private int mHeight;
    private int mShadowElevation = 8;

    public RichBottomNavigationView(Context context) {
        super(context);
        init();
    }

    public RichBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mShadowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.shadow);
        if (mShadowDrawable != null) {
            mShadowDrawable.setCallback(this);
        }
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        setShadowVisible(true);
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h + mShadowElevation, oldw, oldh);
        mWidth = w;
        mHeight = h;
        updateShadowBounds();
    }

    private void updateShadowBounds() {
        if (mShadowDrawable != null && mBottomItemsHolder != null) {
            mShadowDrawable.setBounds(0, 0, mWidth, mShadowElevation);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mShadowDrawable != null && mShadowVisible) {
            mShadowDrawable.draw(canvas);
        }
    }

    public void setShadowVisible(boolean shadowVisible) {
        setWillNotDraw(!mShadowVisible);
        updateShadowBounds();
    }

    public int getShadowElevation() {
        return mShadowVisible ? mShadowElevation : 0;
    }

    public int getSelectedItem() {
        return mLastSelection = findSelectedItem();
    }

    @CallSuper
    public void setSelectedItem(int position) {
        if (position >= getMenu().size() || position < 0) return;

        View menuItemView = getMenuItemView(position);
        if (menuItemView == null) return;
        MenuItemImpl itemData = ((MenuView.ItemView) menuItemView).getItemData();


        itemData.setChecked(true);

        boolean previousHapticFeedbackEnabled = menuItemView.isHapticFeedbackEnabled();
        menuItemView.setSoundEffectsEnabled(false);
        menuItemView.setHapticFeedbackEnabled(false); //avoid hearing click sounds, disable haptic and restore settings later of that view
        menuItemView.performClick();
        menuItemView.setHapticFeedbackEnabled(previousHapticFeedbackEnabled);
        menuItemView.setSoundEffectsEnabled(true);


        mLastSelection = position;

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        BottomNavigationState state = new BottomNavigationState(superState);
        mLastSelection = getSelectedItem();
        state.lastSelection = mLastSelection;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof BottomNavigationState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        BottomNavigationState bottomNavigationState = (BottomNavigationState) state;
        mLastSelection = bottomNavigationState.lastSelection;
        dispatchRestoredState();
        super.onRestoreInstanceState(bottomNavigationState.getSuperState());
    }

    private void dispatchRestoredState() {
        if (mLastSelection != 0) { //Since the first item is always selected by the default implementation, dont waste time
            setSelectedItem(mLastSelection);
        }
    }

    private View getMenuItemView(int position) {
        View bottomItem = mBottomItemsHolder.getChildAt(position);
        if (bottomItem instanceof MenuView.ItemView) {
            return bottomItem;
        }
        return null;
    }

    private int findSelectedItem() {
        int itemCount = getMenu().size();
        for (int i = 0; i < itemCount; i++) {
            View bottomItem = mBottomItemsHolder.getChildAt(i);
            if (bottomItem instanceof MenuView.ItemView) {
                MenuItemImpl itemData = ((MenuView.ItemView) bottomItem).getItemData();
                if (itemData.isChecked()) return i;
            }
        }
        return INVALID_POSITION;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBottomItemsHolder = (ViewGroup) getChildAt(0);
        updateShadowBounds();
        //This sucks.
        MarginLayoutParams layoutParams = (MarginLayoutParams) mBottomItemsHolder.getLayoutParams();
        layoutParams.topMargin = (mShadowElevation + 2) / 2;
    }

    static class BottomNavigationState extends BaseSavedState {
        public int lastSelection;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public BottomNavigationState(Parcel in, ClassLoader loader) {
            super(in, loader);
            lastSelection = in.readInt();
        }

        public BottomNavigationState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(lastSelection);
        }

        public static final Parcelable.Creator<NavigationView.SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<NavigationView.SavedState>() {
            @Override
            public NavigationView.SavedState createFromParcel(Parcel parcel, ClassLoader loader) {
                return new NavigationView.SavedState(parcel, loader);
            }

            @Override
            public NavigationView.SavedState[] newArray(int size) {
                return new NavigationView.SavedState[size];
            }
        });
    }
}