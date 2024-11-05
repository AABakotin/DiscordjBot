package ru.discordj.bot.informer;

import ru.discordj.bot.embed.EmbedCreation;

import java.util.Timer;
import java.util.TimerTask;

public class Run {

    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                new Informer(
                        "195.18.27.92",
                        2343)
                        .listenerServers();
                EmbedCreation.get().embedServerStatus(Informer.getReceive());
            }
        };

        timer.schedule(timerTask, 5000, 20000); //первое число - когда он
                        //запуститься, второе - через сколько будет повторяться
    }
}


