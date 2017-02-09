package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.api.util.BasicDictionary;
import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Attempts to jumble up messages to look goofy.
 * */
public class DolanSpeak extends Mutator {

	private final BasicDictionary dictionary = new BasicDictionary();
	
	public DolanSpeak() {
		super("Dolan Speak", "Spaek liek dis");
		this.setEnabled(true);
		dictionary.addSurrounded("kr", "qw");
		dictionary.addSurrounded("cr", "kr");
		dictionary.addSuffix("th", "f", "d");
		dictionary.addFull("you", "u", "yu");
		dictionary.addSurrounded("ou", "u", "oo");
		dictionary.addFull("and", "&");
		dictionary.addPrefix("th", "d");
		dictionary.addSuffix("ct", "k");
		dictionary.addFull("are", "r", "arr");
		dictionary.addSuffix("er", "a");
		dictionary.addSuffix("ft", "f");
		dictionary.addSurrounded("eou", "u", "oo", "ou");
		dictionary.addSuffix("ph", "f", "ff").addPrefix("ph", "f");
		dictionary.addFull("jesus", "jebus");
		dictionary.addFull("check", "czech");
		dictionary.addFull("your", "ur");
		dictionary.addFull("who", "hoo", "who");
		dictionary.addSuffix("cks", "x");
		dictionary.addSurrounded("ck", "k");
		dictionary.addPrefix("wh", "w");
		dictionary.addPrefix("trans", "truns");
		dictionary.addPrefix("inter", "intre", "intra", "intar");
		dictionary.addPrefix("multi", "moleti", "molti");
		dictionary.addSuffix("ks", "x");
		dictionary.addSuffix("ing", "in");
		dictionary.addSuffix("ove", "uv");
		dictionary.addSuffix("ood", "ud");
		dictionary.addSurrounded("ove", "uv");
		dictionary.addSurrounded("ie", "ei");
		dictionary.addSurrounded("upe", "oope");
		dictionary.addSurrounded("agh", "ag");
		dictionary.addSuffix("ome", "um");
		dictionary.addSuffix("sion", "shun");
		dictionary.addSuffix("tion", "shun");
		dictionary.addSuffix("ate", "8");
		dictionary.addSuffix("esque", "esk");
		dictionary.addSuffix("arium", "aryum");
		dictionary.addSuffix("orium", "oryum");
		dictionary.addSuffix("dom", "dum");
		dictionary.addSuffix("ence", "ents");
		dictionary.addSuffix("ency", "entsy");
		dictionary.addSuffix("oid", "oyd");
		dictionary.addFull("i", "me");
		dictionary.addFull("i'm", "me am");
		dictionary.addFull("im", "me am");
		dictionary.addFull("racist", "rasist", "raycist");
		dictionary.addFull("to", "2");
		dictionary.addFull("have", "hav");
		dictionary.addReplace("'", "");
	}

	// original dolan speak from poohbear. it's still pretty funny.
	/*@Override
	public String mutate(String message) {
		return message.replaceAll("(?i) and ", " & ").replaceAll("(?i)respect", "respek")
				.replaceAll("(?i) are ", " r ").replaceAll("(?i)that", "tht")
				.replaceAll("(?i)this", "dis").replaceAll("(?i)the", "da")
				.replaceAll("(?i)er ", "a ").replaceAll("(?i)er", "ir")
				.replaceAll("(?i)eou", "u").replaceAll("(?i)se", "s")
				.replaceAll("(?i)ce", "s").replaceAll("(?i)ci", "s")
				.replaceAll("(?i)ai", "a").replaceAll("(?i)ie", "ei")
				.replaceAll("(?i)cr", "kr").replaceAll("(?i)ft", "f")
				.replaceAll("(?i)ke", "k").replaceAll("(?i)kr", "qw")
				.replaceAll("(?i)ll", "l").replaceAll("(?i)what", "wat")
				.replaceAll("(?i)aw", "ow").replaceAll("(?i) i ", " me ")
				.replaceAll("(?i) am ", " is ").replaceAll("(?i)cks", "x")
				.replaceAll("(?i)ks", "x").replaceAll("(?i)ck", "k")
				.replaceAll("(?i)ng", "n").replaceAll("(?i)mb", "m")
				.replaceAll("(?i)ot", "it").replaceAll("(?i)nn", "n")
				.replaceAll("(?i)gh ", " ").replaceAll("(?i)check", "czech")
				.replaceAll("(?i)ou", "u").replaceAll("(?i)es ", "s ")
				.replaceAll("(?i)your", "ur").replaceAll("(?i)jesus", "jebus")
				.replaceAll("(?i)ith", "if").replaceAll("(?i)ph", "f")
				.replaceAll("(?i)oi", "oh");
	}*/

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
