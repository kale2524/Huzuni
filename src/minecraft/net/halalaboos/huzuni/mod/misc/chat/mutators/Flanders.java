package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.api.util.BasicDictionary;
import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * One of the most hilarious mutators, attempts to make the user sound like nedward flanders from the simpsons.
 * */
public class Flanders extends Mutator {
	
	private BasicDictionary dictionary = new BasicDictionary();
	
	public Flanders() {
		super("Flanders", "Speak like your local Christian Nedward Flanders");
		dictionary.addSuffix("age", "agerino");
		dictionary.addSuffix("al", "alerino");
		dictionary.addSuffix("an", "anerino");
		dictionary.addSuffix("ance", "ancerino");
		dictionary.addSuffix("ant", "anterino");
		dictionary.addSuffix("ate", "aterino");
		dictionary.addSuffix("ation", "ationerino");
		dictionary.addSuffix("cide", "ciderino");
		dictionary.addSuffix("ed", "edino");
		dictionary.addSuffix("en", "enerino");
		dictionary.addSuffix("ence", "encerino");
		dictionary.addSuffix("ent", "enterino");
		dictionary.addSuffix("er", "erino");
		dictionary.addSuffix("est", "esterino");
		dictionary.addSuffix("ful", "fulerino");
		dictionary.addSuffix("graph", "grapherino");
		dictionary.addSuffix("ian", "ianerino");
		dictionary.addSuffix("ic", "icerino");
		dictionary.addSuffix("ile", "ilerino");
		dictionary.addSuffix("ing", "ingerino");
		dictionary.addSuffix("ish", "isherino");
		dictionary.addSuffix("ist", "isterino");
		dictionary.addSuffix("ive", "iverino");
		dictionary.addSuffix("ative", "ativerino");
		dictionary.addSuffix("itive", "itiverino");
		dictionary.addSuffix("ology", "ologerino");
		dictionary.addSuffix("ose", "oserino");
		dictionary.addSuffix("ure", "urino");
		dictionary.addSuffix("ste", "sterino");
		dictionary.addSuffix("ank", "ankerino");
		dictionary.addSuffix("ade", "aderino");
		dictionary.addSuffix("less", "lesserino");
		dictionary.addSuffix("ness", "nesserino");
		dictionary.addSuffix("ite", "iterino");
		dictionary.addSuffix("ike", "ikerino");
		dictionary.addPrefix("d", "diddly-d");
	}

	@Override
	public boolean modifyServerCommands() {
		return false;
	}

	@Override
	public boolean modifyClientCommands() {
		return false;
	}

	@Override
	public String mutate(String message) {
		return dictionary.replaceAll(message, 0);
	}

}
