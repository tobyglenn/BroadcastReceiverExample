package com.example.testapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testapp.AppService.LocalBinder;

public class MainActivity extends ActionBarActivity {
	BroadcastReceiver reciever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    
    @Override
	public void onResume() {
     super.onResume();
     reciever = new MyBroadcastReceiver();
    LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter(AppService.NOTIFICATION));
    Log.d(MainActivity.class.getName(), "Registered");
    }
    
    @Override
	public void onPause() {
      super.onPause();
      if (reciever != null) {
          LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
          reciever = null;
      }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
    	Button start;
    	Button startOrdered;
    	Button startService;
    	Button sendSticky;
    	EditText text;
    	private static String SOMETHING_HAPPENED = "com.example.somethinghappened";
        private static String EXTRA_INTEGER = "extra integer";
        AppService mService;
        boolean mBound = false;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            start = (Button)rootView.findViewById(R.id.start);
            text = (EditText) rootView.findViewById(R.id.time);
            start.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				    int i = Integer.parseInt(text.getText().toString());
				    Intent intent = new Intent(container.getContext(), MyBroadcastReceiver.class);
				    intent.putExtra("saying", "Don't panik but your time is up!!!!.");
				    PendingIntent pendingIntent = PendingIntent.getBroadcast(container.getContext().getApplicationContext(), 234324243, intent, 0);
				    AlarmManager alarmManager = (AlarmManager) container.getContext().getSystemService(ALARM_SERVICE);
				    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				        + (i * 1000), pendingIntent);
				    Toast.makeText(container.getContext(), "Alarm set in " + i + " seconds", Toast.LENGTH_LONG).show();
				}
			});
            startOrdered = (Button)rootView.findViewById(R.id.orderedStart);
            startOrdered.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 IntentFilter filter = new IntentFilter(SOMETHING_HAPPENED);

					 container.getContext().registerReceiver(new BroadcastReceiver() {
				            @Override
				            public void onReceive(Context context, Intent intent) {
				                Bundle results = getResultExtras(true);
				                results.putInt(EXTRA_INTEGER, 100);
				                Toast.makeText(container.getContext(),"In Initial Receiver: Put 'extra integer' = 100", Toast.LENGTH_LONG).show();
				            }
				        }, filter);

				        Intent intent = new Intent(SOMETHING_HAPPENED);
				        container.getContext().sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
				            @Override
				            public void onReceive(Context context, Intent intent) {
				                Bundle results = getResultExtras(true);
				                Toast.makeText(container.getContext(),
				                        "In Result Receiver: Got 'extra integer' = "
				                                + results.getInt(EXTRA_INTEGER, -1), Toast.LENGTH_LONG).show();
				            }
				        }, null, Activity.RESULT_OK, null, null);
				}
			});
            startService = (Button)rootView.findViewById(R.id.localBroadcast);
            startService.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d("sender", "binding service");
					Intent intent = new Intent(container.getContext(), AppService.class);
					container.getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
				}
			});
            sendSticky = (Button)rootView.findViewById(R.id.sendSticky);
            sendSticky.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent("pass.data.from.activities.withSticky");
					intent.putExtra("message", "This is passing with Sticky Intents");
					getActivity().sendStickyBroadcast(intent);
					Intent myIntent = new Intent(getActivity(), SecondActivity.class);
					startActivity(myIntent);
				}
			});
            return rootView;
        }
        /** Defines callbacks for service binding, passed to bindService() */
        private ServiceConnection mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                    IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                LocalBinder binder = (LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };
        @Override
		public void onStop() {
            super.onStop();
            // Unbind from the service
            if (mBound) {
                this.getActivity().unbindService(mConnection);
                mBound = false;
            }
        }
    }

}
