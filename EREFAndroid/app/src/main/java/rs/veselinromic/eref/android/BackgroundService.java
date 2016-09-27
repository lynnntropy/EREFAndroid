package rs.veselinromic.eref.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import rs.veselinromic.eref.wrapper.SessionManager;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.UserProfile;


public class BackgroundService extends Service
{
    public static boolean isRunning = false;

    private static Timer timer = new Timer();
    private UserProfile previousProfile = null;

    private void displayNotification(String title, String body)
    {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(BackgroundService.this)
                        .setSmallIcon(R.drawable.ic_person_black_24dp)
                        .setContentTitle(title)
                        .setContentText(body);

        int NOTIFICATION_ID = 97645;

        Intent targetIntent = new Intent(BackgroundService.this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(BackgroundService.this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    private class checkTask extends TimerTask
    {
        @Override
        public void run()
        {
            // displayNotification("EREF Android", "Test");

            try
            {
                UserProfile userProfile = Wrapper.getUserProfile();

                if (previousProfile == null)
                {
                    previousProfile = userProfile;
                }
                else
                {
                    if (!previousProfile.generalCredit.equals(userProfile.generalCredit))
                    {
                        displayNotification(
                                "Promena Kredita",
                                "Vaš kredit za prijavu ispita (itd.) je sada " + userProfile.generalCredit);
                    }

                    if (!previousProfile.tuitionCredit.equals(userProfile.tuitionCredit))
                    {
                        displayNotification(
                                "Promena Kredita",
                                "Vaš kredit za upisninu i školarinu je sada " + userProfile.generalCredit);
                    }
                }
            }
            catch (IOException e)
            {
                // do nothing
            }
            catch (Exception e)
            {
                SharedPreferences existingLoginPreferences = getSharedPreferences("loginDetails", 0);
                String username = existingLoginPreferences.getString("username", null);
                String password = existingLoginPreferences.getString("password", null);

                try
                {
                    SessionManager.authenticate(username, password);
                }
                catch (IOException ioe)
                {
                    // do nothing
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (!isRunning) isRunning = true;
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        // super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
