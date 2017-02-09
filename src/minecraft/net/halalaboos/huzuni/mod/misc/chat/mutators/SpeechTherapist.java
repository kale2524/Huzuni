package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.api.util.BasicDictionary;
import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Replaces all 'bad words' with 'better words' so-to-say. Attempts to censor profanities within messages in humorous ways.
 * */
public class SpeechTherapist extends Mutator {
		
	private BasicDictionary dictionary = new BasicDictionary();
	
	public SpeechTherapist() {
		super("Speech Therapist", "Helps maintain a healthy vocabulary");
		this.setEnabled(true);
		dictionary.addFull("hell", "heck");
		dictionary.addFull("shit", "shoot", "darn", "crap");
		dictionary.addFull("ass", "butt", "donk", "booty");
		dictionary.addFull("arse", "butt", "donk", "booty");
		dictionary.addFull("bitch", "buddy", "pal", "bestie", "turd");
		dictionary.addFull("bich", "buddy", "pal", "bestie", "turd");
		dictionary.addFull("bastard", "bus-tard", "banana");
		dictionary.addFull("bollocks", "darn", "heck", "shoot");
		dictionary.addFull("chinc", "china").addFull("chink", "china");
		dictionary.addFull("choad", "toad").addFull("chode", "elf");
		dictionary.addFull("clit", "cat", "pit", "special spot", "private temple", "peepee");
		dictionary.addFull("cock", "cork", "special spot", "private temple", "peepee");
		dictionary.addFull("cok", "cork", "special spot", "private temple", "peepee");
		dictionary.addFull("cooch", "conch", "pit", "special spot", "private temple", "peepee");
		dictionary.addFull("cooter", "scooter", "pit", "special spot", "private temple", "peepee");
		dictionary.addFull("coon", "colored", "African American");
		dictionary.addFull("cum", "milk");
		dictionary.addFull("damn", "darn", "shoot", "heck", "doot");
		dictionary.addFull("dam", "darn", "shoot", "heck", "doot");
		dictionary.addFull("dick", "weewee", "peepee", "richard", "genitals", "gonads");
		dictionary.addFull("dik", "weewee", "peepee", "richard", "genitals", "gonads");
		dictionary.addFull("dike", "lesbian").addFull("dyke", "lesbian");
		dictionary.addFull("douche", "sploosh");
		dictionary.addFull("dumbass", "sillybilly").addFull("dumass", "sillybilly");
		dictionary.addFull("fag", "niceperson", "cigarette", "flower");
		dictionary.addFull("faggot", "niceperson", "cigarette", "flower");
		dictionary.addFull("faggit", "niceperson", "cigarette", "flower");
		dictionary.addFull("fagot", "niceperson", "cigarette", "flower");
		dictionary.addFull("fagit", "niceperson", "cigarette", "flower");
		dictionary.addFull("flamer", "niceperson", "cigarette", "flower");
		dictionary.addFull("fudgepacker", "niceperson", "cigarette", "flower");
		dictionary.addFull("fuck", "fudge", "shoot", "darn", "heck", "fiddle", "frick");
		dictionary.addFull("fuk", "shoot", "darn", "hek", "fidle", "frik");
		dictionary.addFull("gook", "china");
		dictionary.addFull("gringo", "perfect human");
		dictionary.addFull("homo", "homeowner");
		dictionary.addFull("honkey", "hottie");
		dictionary.addFull("hoe", "turd");
		dictionary.addFull("jigaboo", "African American");
		dictionary.addFull("nigger", "African American", "nagger");
		dictionary.addFull("nigga", "African American");
		dictionary.addFull("pussy", "weenie", "special spot", "private temple", "peepee", "weewee");
		dictionary.addFull("queer", "niceperson", "cigarette", "flower");
		dictionary.addFull("skank", "open");
		dictionary.addFull("skeet", "waterjet");
		dictionary.addFull("slut", "cutie");
		dictionary.addFull("spick", "Mexican");
		dictionary.addFull("spic", "Mexican");
		dictionary.addFull("twat", "bergina");
		dictionary.addFull("whore", "babe");
		dictionary.addFull("wetback", "Mexican");
		dictionary.addFull("wank", "hand-exercise");
	}

	@Override
	public boolean modifyServerCommands() {
		return true;
	}

	@Override
	public boolean modifyClientCommands() {
		return true;
	}

	@Override
	public String mutate(String message) {
		return dictionary.replaceAll(message, 0);
	}
	
}
