package com.example.demo.features.services;

import com.example.demo.features.repositories.ItemRepository;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Service
public class GCalService {

    @Autowired
    ItemRepository itemRepository;

    public Event createEvent(Long itemId, String name, String remarks, Date expiryDate) {
        Event event = new Event()
                .setSummary("Expiring: " + name)
                .setDescription(remarks);

        Date endDate = new Date(expiryDate.getTime() + 86400000); // An all-day event is 1 day (or 86400000 ms) long

        //start.date The date, in the format "yyyy-mm-dd", if this is an all-day event.
        String startDateStr = new SimpleDateFormat("yyyy-MM-dd").format(expiryDate);
        String endDateStr = new SimpleDateFormat("yyyy-MM-dd").format(endDate);

        DateTime startDateT = new DateTime(startDateStr);
        DateTime endDateT = new DateTime(endDateStr);
        EventDateTime start = new EventDateTime().setDate(startDateT);
        EventDateTime end = new EventDateTime().setDate(endDateT);
        event.setStart(start);
        event.setEnd(end);

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24*60), // 1 day before
                new EventReminder().setMethod("popup").setMinutes(10),
        };

        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        return event;
    }
}
