package com.example.demo.updates;

import com.example.demo.features.models.Item;
import com.example.demo.features.repositories.ItemRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class EmailSendJob implements Job {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EmailUtil emailUtil;

    private static final int WITHIN_DAYS = 3;

    private void init() {
        ApplicationContext springContext =
                WebApplicationContextUtils.getWebApplicationContext(ContextLoaderListener.getCurrentWebApplicationContext().getServletContext());
        System.out.println("Attempting to initialize since autowired failed");
        itemRepository = springContext.getBean(ItemRepository.class);
        emailUtil = springContext.getBean(EmailUtil.class);
        System.out.println(itemRepository);
    }
    private boolean withinDateRange(Date date) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, WITHIN_DAYS);
        Date expiresAt = calendar.getTime();
        return !(date.before(new Date()) || date.after(expiresAt));
    }

    private List<Item> checkForExpiringItems() {
        if (itemRepository == null) {
            init();
        }
        List<Item> items = itemRepository.findAll();
        List<Item> expiring = new ArrayList<>();
        for (Item item :items) {
            if (withinDateRange(item.getExpiryDate())) {
                expiring.add(item);
            }
        }
        return expiring;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Item> expiringItems = checkForExpiringItems();
        for (Item item : expiringItems) {
            emailUtil.sendEmail(item);
        }
    }
}