package net.halalaboos.huzuni.api.mod;

import java.lang.reflect.Method;

import net.halalaboos.huzuni.Huzuni;

/**
 * Command that can be generated through an annotation being present upon a function.
 * */
public class AnnotationCommand implements Command {

    private static final ParameterTransformer<?>[] validParameters = ParameterTransformer.getDefaults();

    private final String[] aliases;
	
	private final String description, help;
	
	private final Object source;
	
	private final Method method;
	
	private final ParameterTransformer<?>[] transformers;
	
	private AnnotationCommand(Object source, Method method, ParameterTransformer<?>[] transformers, String description, String... aliases) {
		this.source = source;
		this.method = method;
		this.transformers = transformers;
		this.description = description;
		this.aliases = aliases;
		this.help = generateHelp();
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public void giveHelp() {
		Huzuni.INSTANCE.addChatMessage(help);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void run(String input, String[] args) throws Exception {
		Object[] methodArgs = transformers.length <= 0 ? null : new Object[transformers.length];
		if (args != null && args.length >= transformers.length) {
			for (int i = 0; i < transformers.length && i < args.length; i++) {
				if (transformers[i].canTransform(args[i])) {
					methodArgs[i] = transformers[i].transform(args[i]);
				}
			}
		}
		method.invoke(source, methodArgs);
	}

    /**
     * Attempts to create an annotation command from the method provided. Will add the command into the command manager provided.
     * */
	public static void createCommand(CommandManager commandManager, Object source, Method method) {
		if (method.isAnnotationPresent(CommandPointer.class)) {
			boolean valid = true;
			Class<?>[] parameters = method.getParameterTypes();
			ParameterTransformer<?>[] transformers = new ParameterTransformer<?>[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				ParameterTransformer<?> transformer = getParameterTransformer(parameters[i]);
				if (transformer != null) {
					transformers[i] = transformer;
				} else {
					valid = false;
					break;
				}
			}
			if (valid) {
				CommandPointer pointer = method.getAnnotation(CommandPointer.class);
				commandManager.addCommand(new AnnotationCommand(source, method, transformers, pointer.description(), pointer.value()));
			}
		}
	}

	/**
     * @return The parameter transformer which is used for the parameter class provided.
     * */
	private static ParameterTransformer<?> getParameterTransformer(Class<?> parameter) {
		for (ParameterTransformer<?> transformer : validParameters) {
			if (transformer.isAssignable(parameter))
				return transformer;
		}
		return null;
	}

	/**
     * Generates the help string for this annotation command.
     * */
	private String generateHelp() {
		String help = aliases[0] + " ";
		for (ParameterTransformer<?> transformer : transformers) {
			help += transformer.getFormat() + " ";
		}
		return help.substring(0, help.length() - 1);
	}
}
