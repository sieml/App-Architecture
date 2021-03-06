package com.frodo.app.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.android.app.R;
import com.frodo.app.framework.log.Logger;
import com.frodo.app.android.ui.FragmentStack;
import com.frodo.app.android.ui.fragment.AbstractBaseFragment;

/**
 * Created by frodo on 2015/1/27. FragmentContainer
 */
public abstract class FragmentContainerActivity extends AbstractBaseActivity {

    private FragmentStack mStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStack = FragmentStack.forContainer(this, R.id.container,
                new FragmentStack.Callback() {
                    @Override
                    public void onStackChanged(int stackSize, Fragment topFragment) {
                        Logger.fLog().tag(FragmentStack.STATE_STACK).d("onStackChanged stackSize:" + stackSize + ", topFragment: " + topFragment.getTag());
                    }
                });
        mStack.setDefaultAnimation(R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left);

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (mStack.size() > 0) {
                final Class clazz = mStack.peek().getClass();
                mStack.replace(clazz,
                        clazz.getCanonicalName() + System.currentTimeMillis(),
                        mStack.peek().getArguments());
                mStack.commit();
            }
        } else {
            mStack.restoreState(savedInstanceState);
        }
    }

    @Override
    public final int getLayoutId() {
        return R.layout.fragment_container;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mStack.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStack.restoreState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isForPop()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public final void addFragment(Class<? extends Fragment> fragment, Bundle args, boolean isFinishTopFragment) {
        addFragment(fragment, fragment.getCanonicalName() + System.currentTimeMillis(), args, isFinishTopFragment);
    }

    private void addFragment(Class<? extends Fragment> fragment, String tag, Bundle args, boolean isFinishTopFragment) {
        if (args == null) {
            args = new Bundle();
        }

        if (isFinishTopFragment) {
            if (mStack.size() > 1) {
                mStack.pop(true);
            } else {
                mStack.replace(fragment, tag, args);
                mStack.commit();
                return;
            }
        }

        mStack.push(fragment, tag, args);
        mStack.commit();
    }

    private AbstractBaseFragment getTopFragment() {
        return (AbstractBaseFragment) mStack.peek();
    }

    private boolean isForPop() {
        if (!isRootFragment()) {
            getTopFragment().onBackPressed();
            return mStack.pop(true);
        }

        return false;
    }

    private boolean isRootFragment() {
        return mStack.size() == 1;
    }
}
