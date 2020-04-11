package com.borwe.bonfireadventures.viewActions;

import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

public class ViewAction <T extends View> {

    AtomicBoolean actionDone;
    AtomicBoolean actionStarted;

    T view;

    public ViewAction(T view,boolean actionPerformed){
        this.view=view;
        actionDone=new AtomicBoolean(actionPerformed);
        actionStarted=new AtomicBoolean(false);
    }

    public void setOnClickListerner(View.OnClickListener listener){
        view.setOnClickListener(listener);
    }

    /**
     * Check if action is done, if not set it's value to true,
     * otherwise fail, since it was already clocked,
     * also make sure action was already started, otherwise it's a joke
     */
    public void doneAction()throws RuntimeException{
        //make sure action already started
        if(actionStarted.get()==false){
            throw new RuntimeException("Please ViewAction.start() method before tyring "+
                    "to make one as completed");
        }
        if(actionDone.get()==false){
            actionDone.set(true);
            //mark the end of an action
            actionStarted.set(false);
        }else{
            //meaning action was already done, so throw an error here
            throw new RuntimeException("Please call ViewAction.reset() method"+
                    " before trying to do an action that has already occured");
        }
    }

    /**
     * Check if action is started or not, start if it not, otherwise
     * throw since, you can't start an action that already is started
     */
    public void startAction() {
        //make sure action not already started
        if (actionStarted.get()) {
            throw new RuntimeException("Please call ViewAction.reset() method" +
                    " as it appears the current action was already started");
        } else {
            actionStarted.set(true);
            //since action was just started
            actionDone.set(false);
        }
    }

    /**
     * Used for reseting an action to allow it to be
     * performed again
     */
    public void resetAction(){
        actionStarted.set(false);
        actionDone.set(false);
    }

    /**
     * Check if action is done or not
     * @return
     */
    public boolean isDone(){
        if(actionDone.get()){
            return true;
        }
        return false;
    }

    /**
     * check if action started or not
     * @return
     */
    public boolean isStarted(){
        if(actionStarted.get()){
            return true;
        }
        return false;
    }
}
