package lab1_206_02.uwaterloo.ca.pomodorotimerapp;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //declare the textview
    TextView myTV;
    //declare the starter button
    Button starter;
    //declare a new timer
    Timer myTimer;
    //declare the timer handler
    TimerTask myTimerTask;
    //create a watchdog timer to cancel the timer after 25 minutes
    Timer stop;
    //declare the timer stop handlrer
    TimerTask myTimerTask2;
    //Prgress bar
    ProgressBar progress;

    //declare the time to stop (1500sec = 25 minutes)
    int period = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void start(View v){
        //once the start button is clicked begin
        //set the textview up
        myTV = (TextView) findViewById(R.id.time);
        //set the button
        starter = (Button) findViewById(R.id.start);
        //set the progress bar
        progress = (ProgressBar)findViewById(R.id.progressBar2);
        progress.setIndeterminate(false);
        progress.setMax(1500);

        //check to see if this the first time the Start button has been clicked
        if(myTimer != null || stop != null){
            myTimer.cancel();
            stop.cancel();
            Toast.makeText(this, "Previous Timer canceled.", Toast.LENGTH_LONG);
            //Log.v(AppCompatActivity, "");
        }

        //set the timer
        myTimer = new Timer();
        //set the timer event handler
        myTimerTask myTask = new myTimerTask(this, myTV, progress);
        //set the timer to call the myTimerTask with the text view every second
        myTimer.schedule(myTask, 1000, 1000);

        //set the new timer to stop the first
        stop = new Timer();
        //first set up the watchdog :)
        myTimerTask2 mywatchDog = new myTimerTask2(this, myTV, myTimer);
        //sets the scheduale watch dog to first run after 1500 seconds to kill the timer
        stop.schedule(mywatchDog, 1500000);

    }

    public void reset(View v){
        //check to see if there is any timer running, if so then reset
        if(myTimer != null || stop != null){
            //cancel the first
            myTimer.cancel();

            //cancel the second watchdog as well
            stop.cancel();

            //let the user know that the timers has been reset
            Toast.makeText(this, "Previous Timer canceled.", Toast.LENGTH_LONG);
            //manually change the text to be set back to original state
            myTV.setText("00 : 00");
            //set the progress time to 0
            progress.setProgress(0);
            //Log.v(AppCompatActivity, "");
        }
    }
}


//class needed to uses timer
class myTimerTask extends TimerTask{
    private Activity myActivity;
    private TextView myText;
    private ProgressBar progressing;

    //the total amount of time elapsed
    private int secondCount =0;
    //the number of minutes so far
    private int minCount = 0;
    //total number of second so far
    private int secCount = 0;

    //method that just set the variables to be what they are set to in this class
    public myTimerTask(Activity myACT, TextView myTV, ProgressBar prgres){
        myActivity = myACT;
        myText= myTV;
        progressing = prgres;

    }

    //calculates the time
    public void time(int time){
        //gets the total amount of time in minutes
        //since java rounds down (floor)
        minCount = time / 60;
        //get the remaining amount of seconds
        secCount = time -(minCount*60);
    }

    //where it actually runs
    public void run(){
        //set it run on the actual thread to display the time
        myActivity.runOnUiThread(
                new Runnable() {
                    public void run() {
                        //increment time after each run
                        secondCount++;
                        //set the progress to increasse with the seconds
                        progressing.setProgress(secondCount);
                        //calculate the time into minutes and seconds
                        time(secondCount);
                        //set the tesxt in the text view to display the time
                        //in a format of 2 numbers in each minutes and seconds
                        myText.setText(String.format("%02d : %02d", minCount, secCount));
                    }
                }
        );
    }

}


//second class for the watchdog timer
class myTimerTask2 extends TimerTask{
    private Activity myActivity;
    private TextView myText;
    private Timer myTime;

    //has the time
    private int secondCount =0;

    //set the timer
    public myTimerTask2(Activity myACT, TextView myTV, Timer myTimer){
        myActivity = myACT;
        myText= myTV;
        myTime = myTimer;

    }

    //run
    public void run(){
        myActivity.runOnUiThread(
                new Runnable() {
                    public void run() {
                        //kill the timer
                        myTime.cancel();
                        //display on the textView to the user they are done their study session.
                        myText.setText(String.format("You are done!"));
                    }
                }
        );
    }

}