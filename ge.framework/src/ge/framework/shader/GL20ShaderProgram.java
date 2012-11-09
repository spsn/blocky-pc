package ge.framework.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GL20ShaderProgram
{

	private final int ID;

	public GL20ShaderProgram(final GL20Shader... shaders) {
		this.ID = GL20.glCreateProgram();

		for ( GL20Shader shader : shaders )
			GL20.glAttachShader(ID, shader.getID());

				GL20.glLinkProgram(ID);

		if ( GL20.glGetProgram(ID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE ) {
			printInfoLog();
			destroy();
			throw new RuntimeException("Failed to link a Shader Program: " + ID);
		}
	}

	public void validate() {
		GL20.glValidateProgram(ID);

		final boolean error = GL20.glGetProgram(ID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE;

		if ( error ) {
			printInfoLog();
			throw new RuntimeException("Failed to validate a Shader Program.");
		}
	}

	public int getID() {
		return ID;
	}

	public void destroy() {
		GL20.glDeleteProgram(ID);
	}

	public void enable() {
		GL20.glUseProgram(ID);
	}

	public static void disable() {
		GL20.glUseProgram(0);
	}

	public int getUniformLocation(final String uniform) {
		final int location = GL20.glGetUniformLocation(ID, uniform);

		if ( location == -1 )
			throw new IllegalArgumentException("Invalid uniform name specified: " + uniform);

		return location;
	}

	public int getAttributeLocation(final String attrib) {
		final int location = GL20.glGetAttribLocation(ID, attrib);

		if ( location == -1 )
			throw new IllegalArgumentException("Invalid attribute name specified: " + attrib);

		return location;
	}

	private void printInfoLog() {
		final int logLength = GL20.glGetProgram(ID, GL20.GL_INFO_LOG_LENGTH);

		System.out.println(logLength);
		if ( logLength <= 1 )
			return;

		System.out.println("\nInfo Log of Shader Program: " + ID);
		System.out.println("-------------------");
		System.out.println(GL20.glGetProgramInfoLog(ID, logLength));
		System.out.println("-------------------");
	}

}