package student_canteen.threads;

import student_canteen.gui.UpdateInterface;

abstract public class MyThread extends Thread  {
    private int dinner;
    static protected boolean end = false;
    protected UpdateInterface ui;

    public MyThread(String name) {
        super(name);
        this.dinner = 0;
    }

    // this would be used if I'd want to add start stop button
    public static void setEnd(boolean k) {
        end = k;
    }

    public static boolean getEnd() {
        return end;
    }

    public void setDinner(int dinner) {
        this.dinner = dinner;
    }

    public int getDinner() {
        return dinner;
    }

    public void setUI(UpdateInterface ui) {
        this.ui = ui;
    }


}
