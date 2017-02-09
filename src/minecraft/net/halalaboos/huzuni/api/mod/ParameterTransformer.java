package net.halalaboos.huzuni.api.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Transforms a string argument into an object. Used for parameters within an annotation command.
 * */
public abstract class ParameterTransformer <T> {
	
	private final Class<?> clazz;
	
	public ParameterTransformer(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
     * @return True if the class parameter is valid for transformation with this transformer.
     * */
	public boolean isAssignable(Class<?> parameter) {
		return clazz.isAssignableFrom(parameter);
	}

	/**
     * @return The format of this parameter.
     * */
	public abstract String getFormat();

    /**
     * @return True if the input can be transformed into the required object.
     * */
	public abstract boolean canTransform(String input);

    /**
     * Transforms the given argument to an object.
     * */
	public abstract T transform(String input);

	public Class<?> getClazz() {
		return clazz;
	}

	/**
     * @return The default parameter transformers available for the annotation command generation.
     * */
	public static ParameterTransformer<?>[] getDefaults() {
		return  new ParameterTransformer[] {
				new IntegerTransformer(),
				new FloatTransformer(),
				new DoubleTransformer(),
				new StringTransformer(),
				new PlayerTransformer()
			};
	}
	
	public static class PlayerTransformer extends ParameterTransformer<EntityPlayer> {

		public PlayerTransformer() {
			super(EntityPlayer.class);
		}

		@Override
		public EntityPlayer transform(String input) {
			for (EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
				if (player.getName().equalsIgnoreCase(input))
					return player;
			}
			return null;
		}

		@Override
		public String getFormat() {
			return "\"username\"";
		}
		
		@Override
		public boolean canTransform(String input) {
			return true;
		}

	}

	public static class IntegerTransformer extends ParameterTransformer<Integer> {

		public IntegerTransformer() {
			super(int.class);
		}

		@Override
		public Integer transform(String input) {
			return Integer.parseInt(input);
		}
		
		@Override
		public String getFormat() {
			return "<int>";
		}

		@Override
		public boolean canTransform(String input) {
			try {
				Integer.parseInt(input);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	public static class DoubleTransformer extends ParameterTransformer<Double> {

		public DoubleTransformer() {
			super(double.class);
		}

		@Override
		public Double transform(String input) {
			return Double.parseDouble(input);
		}
		
		@Override
		public String getFormat() {
			return "<double>";
		}

		@Override
		public boolean canTransform(String input) {
			try {
				Double.parseDouble(input);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	public static class FloatTransformer extends ParameterTransformer<Float> {

		public FloatTransformer() {
			super(float.class);
		}

		@Override
		public Float transform(String input) {
			return Float.parseFloat(input);
		}
		
		@Override
		public String getFormat() {
			return "<float>";
		}

		@Override
		public boolean canTransform(String input) {
			try {
				Float.parseFloat(input);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	public static class StringTransformer extends ParameterTransformer<String> {

		public StringTransformer() {
			super(String.class);
		}

		@Override
		public String transform(String input) {
			return input;
		}
		
		@Override
		public String getFormat() {
			return "\"text\"";
		}

		@Override
		public boolean canTransform(String input) {
			return true;
		}
	}
}
