package com.odoo.work.addons.fcm.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.odoo.work.R;
import com.odoo.work.addons.teams.InvitationAccept;

import java.util.HashMap;

public class OdooWorkFCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        HashMap<String, String> data = new HashMap<>(remoteMessage.getData());
        String model = data.get("model");

        switch (model) {
            case "project.teams":
                processProjectTeams(data);
                break;
        }
    }

    private void processProjectTeams(HashMap<String, String> data) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_ALL);
        int team_id = Integer.parseInt(data.get("res_id"));
        if (data.containsKey("type") && data.get("type").equals("invitation")) {
            // Invitation notification
            builder.setContentTitle(getString(R.string.team_invitation));
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.title_new_invitation, data.get("author_name"), data.get("subject"))));
            builder.setOngoing(true);

            Intent invitationAccept = new Intent(getApplicationContext(), InvitationAccept.class);
            invitationAccept.putExtra("model", data.get("model"));
            invitationAccept.putExtra("res_id", data.get("res_id"));
            invitationAccept.putExtra("author_name", data.get("author_name"));
            builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), team_id, invitationAccept, 0));
        } else {
            builder.setContentTitle(data.get("subject"));
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(data.get("body")));
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(team_id, builder.build());
    }


}
