package ge.framework.shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Represents a shader program.
 */
public class GL20Program extends Program
{

	/**
	 * Constructor.
	 * @param vertexShader The vertex shader code
	 * @param fragmentShader The fragment shader code
	 */
	public GL20Program(
		final String vertexShaderCode,
		final String fragmentShaderCode)
	{
		// Call constructor
		this(new GL20Shader(GL20.GL_VERTEX_SHADER, vertexShaderCode), new GL20Shader(GL20.GL_FRAGMENT_SHADER, fragmentShaderCode));
	}

	/**
	 * Constructor.
	 * @param shaders The list of shaders to attach
	 */
	public GL20Program(
		final GL20Shader... shaders)
	{
		// Call constructor
		super();

		// Create program
		id = GL20.glCreateProgram();

		// Attach shaders to program
		for (GL20Shader shader : shaders)
		{
			GL20.glAttachShader(id, shader.getId());
		}

		// Link program
		GL20.glLinkProgram(id);

		// Failed to link program?
		if (GL20.glGetProgram(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
		{
			// Log program information
			logProgramInformation();

			// Destroy program
			destroy();

			throw new java.lang.RuntimeException("Failed to link shader program: " + id);
		}

		// Get model view projection matrix uniform location
		mvpMatrixUniform = getUniformLocationChecked("ModelViewProjectionMatrix");

		// Get model position rotation matrix uniform location
		mprMatrixUniform = getUniformLocationChecked("ModelPositionRotationMatrix");

		// Get model position uniform location
		modelPositionUniform = getUniformLocationChecked("mPosition");

		// Get model rotation uniform location
		modelRotationUniform = getUniformLocationChecked("mRotation");

		// Get vertex position attribute location
		vertexPositionAttribute = getAttributeLocationChecked("vPosition");

		// Get vertex normal attribute location
		vertexNormalAttribute = getAttributeLocationChecked("vNormal");

		// Get vertex color attribute location
		vertexColorAttribute = getAttributeLocationChecked("vColor");

		// Get vertex texture coordinate attribute location
		vertexTextureAttribute = getAttributeLocationChecked("vTexture");

		// Get fragment texture sampler uniform location
		fragmentSamplerUniform = getUniformLocationChecked("fTextureSampler");
	}

	/**
	 * Validate shader program.
	 */
	public void validate()
	{
		// Validate program
		GL20.glValidateProgram(id);

		// Failed to validate program?
		if (GL20.glGetProgram(id, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE)
		{
			// Log program information
			logProgramInformation();

			throw new RuntimeException("Failed to validate shader program.");
		}

	}

	/**
	 * Destroy shader program.
	 */
	public void destroy()
	{
		// Delete program
		GL20.glDeleteProgram(id);
	}

	/**
	 * Get uniform location.
	 * @param uniform The uniform
	 * @return The uniform location
	 */
	public int getUniformLocation(
		final String uniform)
	{
		// Local variables
		int location;

		// Get uniform location
		location = GL20.glGetUniformLocation(id, uniform);

		// Uniform not defined?
		if (location == -1)
		{
			throw new IllegalArgumentException("Invalid uniform name specified: " + uniform);
		}

		return location;
	}

	/**
	 * Get uniform location.
	 * @param uniform The uniform
	 * @return The uniform location
	 */
	private int getUniformLocationChecked(
		final String uniform)
	{

		try
		{
			// Get uniform location
			return getUniformLocation(uniform);
		}
		catch (java.lang.IllegalArgumentException exception)
		{
			// Uniform not used
			return -1;
		}

	}

	/**
	 * Get attribute location.
	 * @param attribute The attribute
	 * @return The attribute location
	 */
	public int getAttributeLocation(
		final String attribute)
	{
		// Local variables
		int location;

		// Get attribute location
		location = GL20.glGetAttribLocation(id, attribute);

		// Attribute not defined?
		if (location == -1)
		{
			throw new IllegalArgumentException("Invalid attribute name specified: " + attribute);
		}

		return location;
	}

	/**
	 * Get attribute location.
	 * @param attribute The attribute
	 * @return The attribute location
	 */
	private int getAttributeLocationChecked(
		final String attribute)
	{

		try
		{
			// Get attribute location
			return getAttributeLocation(attribute);
		}
		catch (java.lang.IllegalArgumentException exception)
		{
			// Attribute not used
			return -1;
		}

	}

	/**
	 * Activate shader program.
	 */
	public void activate()
	{
		// Enable shader program
		GL20.glUseProgram(id);

		// Enable vertex attributes
		if (vertexPositionAttribute != -1)
		{
			GL20.glEnableVertexAttribArray(vertexPositionAttribute);
		}

		if (vertexNormalAttribute != -1)
		{
			GL20.glEnableVertexAttribArray(vertexNormalAttribute);
		}

		if (vertexColorAttribute != -1)
		{
			GL20.glEnableVertexAttribArray(vertexColorAttribute);
		}

		if (vertexTextureAttribute != -1)
		{
			GL20.glEnableVertexAttribArray(vertexTextureAttribute);
		}

	}

	/**
	 * Deactivate shader program.
	 */
	public void deactivate()
	{

		// Disable vertex attributes
		if (vertexPositionAttribute != -1)
		{
			GL20.glDisableVertexAttribArray(vertexPositionAttribute);
		}

		if (vertexNormalAttribute != -1)
		{
			GL20.glDisableVertexAttribArray(vertexNormalAttribute);
		}

		if (vertexColorAttribute != -1)
		{
			GL20.glDisableVertexAttribArray(vertexColorAttribute);
		}

		if (vertexTextureAttribute != -1)
		{
			GL20.glDisableVertexAttribArray(vertexTextureAttribute);
		}

		// Disable shader program
		GL20.glUseProgram(0);
	}

	/**
	 * Log program information.
	 */
	private void logProgramInformation()
	{
		// Local variables
		int logLength;

		// Get information log length
		logLength = GL20.glGetProgram(id, GL20.GL_INFO_LOG_LENGTH);

		// Print information log length
		System.out.println(logLength);

		// Information log not defined?
		if (logLength <= 1)
		{
			return;
		}

		// Print information log length
		System.out.println("\nInfo log of shader program: " + id);
		System.out.println("-------------------");
		System.out.println(GL20.glGetProgramInfoLog(id, logLength));
		System.out.println("-------------------");
	}

}
