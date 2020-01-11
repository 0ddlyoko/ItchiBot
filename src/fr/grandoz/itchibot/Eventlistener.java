package fr.grandoz.itchibot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Eventlistener implements net.dv8tion.jda.api.hooks.EventListener {
	List<String> SuggestID = new ArrayList<>();
	public Eventlistener() {
		//Load suggestion channels ID from config
		SuggestID.add("648195385411633152");
		SuggestID.add("665650524875522068");
		SuggestID.add("639122530904571924");
	}
	
	@Override
	public void onEvent(GenericEvent event) {
		if(event instanceof MessageReceivedEvent)OnMesage((MessageReceivedEvent) event);
		
	}

	
	private void OnMesage(MessageReceivedEvent event) {
		for(int i =0 ; i<SuggestID.size() ; i++) {
			if(event.getTextChannel().getId().equals(SuggestID.get(i))) {
				
		
		
		if(event.getAuthor().equals(event.getJDA().getSelfUser())){
				event.getMessage().addReaction("UsValid:642724648517238785").queue();
				event.getMessage().addReaction("USnot_Valid:642724619454906378").queue();
				return;
		}else {
				
					String message = event.getMessage().getContentRaw();
				
					 	String[] split = message.split(" ");
					 	if(split[0].equalsIgnoreCase("&suggest") && split.length>=2) { 
					 			String suggestion = "";
					 				for(int o= 1 ; o<split.length ;o++) {
					 					suggestion = suggestion + split[o] +  " ";
								}
						 				EmbedBuilder em = new EmbedBuilder();
						 				em.setColor(Color.cyan);
						 				em.addField("Suggestion de **"+event.getAuthor().getName() + "**",suggestion, false);
						 				em.setFooter("Faire une suggestion : &suggest <suggestion> " , event.getAuthor().getEffectiveAvatarUrl());
						 				event.getChannel().sendMessage(em.build()).complete();
							}
						event.getMessage().delete().complete();
					 	return;
				}
			}
			
		}
	}

}
