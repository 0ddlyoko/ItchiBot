package fr.grandoz.itchibot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class Main  {
	
public static void main(String[] args) {
	
	JDA jda = null;
	try {
		jda = new JDABuilder(AccountType.BOT).setToken("NjY1NjgyMTg1NDQwODU0MDY1.XhpPnQ.rYkpaVkxg1XuTNh3np5xj-BuMNA").build();
		jda.addEventListener(new Eventlistener());
	} catch (LoginException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);jda.addEventListener();	
}

	}

	
