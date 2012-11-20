package ge.framework.shader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class GL20Shader
{

	protected static ByteBuffer fileBuffer = BufferUtils.createByteBuffer(1024 * 10);

	private int type;
	private int id;

	public GL20Shader() {
	}

	public GL20Shader(final int type, final CharSequence source) {
		createFromSource(type, source);
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public void destroy() {
		if ( id == 0 )
			throw new IllegalStateException("The shader has not been created");

		GL20.glDeleteShader(id);
		id = 0;
	}

	public void createFromFile(final int type, final ClassLoader loader, final String file) throws IOException {
		final InputStream inputStream = loader.getResourceAsStream(file);

		if ( inputStream == null )
			throw new IllegalArgumentException("A shader source file could not be found: " + file);

		final BufferedInputStream stream = new BufferedInputStream(inputStream);

		byte character;
		while ( (character = (byte)stream.read()) != -1 )
			fileBuffer.put(character);
		fileBuffer.flip();

		stream.close();

		final byte[] array = new byte[fileBuffer.remaining()];
		fileBuffer.get(array);

		final String source = new String(array);

		fileBuffer.clear();

		createFromSource(type, source);
	}

	public void createFromSource(final int type, final CharSequence source) {
		if ( id != 0 )
			throw new IllegalStateException("The shader has already been created");

		this.type = type;
		this.id = GL20.glCreateShader(type);

		GL20.glShaderSource(id, source);

		GL20.glCompileShader(id);

		if ( GL20.glGetShader(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE ) {
			printInfoLog();
			destroy();
			throw new RuntimeException("A compilation error occured in a shader.");
		}
	}

	public void printInfoLog() {
		if ( id == 0 )
			throw new IllegalStateException("The shader has not been created");

		final int logLength = GL20.glGetShader(id, GL20.GL_INFO_LOG_LENGTH);
		if ( logLength <= 1 )
			return;

		System.out.println("\nInfo Log of Shader Object: " + id);
		System.out.println("--------------------------");
		System.out.println(GL20.glGetShaderInfoLog(id, logLength));
	}

}