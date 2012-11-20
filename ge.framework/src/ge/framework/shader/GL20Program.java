package ge.framework.shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Represents a shader program.
 */
public class GL20Program
{
	// Identifier
	private int id;

	// Model view projection matrix uniform location
	private int mvpMatrixUniform;

	// Model position rotation matrix uniform location
	private int mprMatrixUniform;

	// Model position uniform location
	private int modelPositionUniform;

	// Model rotation uniform location
	private int modelRotationUniform;

	// Vertex position attribute location
	private int vertexPositionAttribute;

	// Vertex normal attribute location
	private int vertexNormalAttribute;

	// Vertex color attribute location
	private int vertexColorAttribute;

	// Vertex texture coordinate attribute location
	private int vertexTextureAttribute;

	// Fragment texture sampler uniform location
	private int fragmentSamplerUniform;

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
	 * Get identifier.
	 * @return The identifier
	 */
	public int getId()
	{
		return id;
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
	 * Get model view projection matrix uniform location.
	 * @return The model view projection matrix uniform location
	 */
	public int getMvpMatrixUniform()
	{
		return mvpMatrixUniform;
	}

	/**
	 * Get model position rotation matrix uniform location.
	 * @return The model position rotation matrix uniform location
	 */
	public int getMprMatrixUniform()
	{
		return mprMatrixUniform;
	}

	/**
	 * Get model position uniform location.
	 * @return The model position uniform location
	 */
	public int getModelPositionUniform()
	{
		return modelPositionUniform;
	}

	/**
	 * Get model rotation uniform location.
	 * @return The model rotation uniform location
	 */
	public int getModelRotationUniform()
	{
		return modelRotationUniform;
	}

	/**
	 * Get vertex position attribute location.
	 * @return The vertex position attribute location
	 */
	public int getVertexPositionAttribute()
	{
		return vertexPositionAttribute;
	}

	/**
	 * Get vertex normal attribute location.
	 * @return The vertex normal attribute location
	 */
	public int getVertexNormalAttribute()
	{
		return vertexNormalAttribute;
	}

	/**
	 * Get vertex color attribute location.
	 * @return The vertex color attribute location
	 */
	public int getVertexColorAttribute()
	{
		return vertexColorAttribute;
	}

	/**
	 * Get vertex texture coordinate attribute location.
	 * @return The vertex texture coordinate attribute location
	 */
	public int getVertexTextureAttribute()
	{
		return vertexTextureAttribute;
	}

	/**
	 * Get fragment texture sampler uniform location.
	 * @return The fragment texture sampler uniform location
	 */
	public int getFragmentSamplerUniform()
	{
		return fragmentSamplerUniform;
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
