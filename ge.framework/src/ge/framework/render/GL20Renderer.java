package ge.framework.render;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import ge.framework.profile.Profiler;
import ge.framework.shader.GL20Program;
import ge.framework.util.Matrix;
import ge.framework.util.Ray;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

/**
 * Represents a renderer for OpenGL 2.0.
 */
public class GL20Renderer extends Renderer
{
	// Display mode
	private DisplayMode displayMode;

	// Shader program for opaque meshes
	private GL20Program opaqueProgram;

	// Shader program for model meshes
	private GL20Program modelProgram;

	// Shader program for transparent meshes
	private GL20Program transparentProgram;

	// Vertex buffer
	private java.nio.FloatBuffer sharedVertexBuffer;

	// Index buffer
	private java.nio.ShortBuffer sharedIndexBuffer;

	// Projection matrix
	private Matrix4f projectionMatrix;

	// Model view matrix
	private Matrix4f modelViewMatrix;

	// Orthogonal matrix
	private Matrix4f orthogonalMatrix;

	// Model view projection matrix
	private Matrix4f mvpMatrix;

	// Model view projection matrix buffer
	private java.nio.FloatBuffer mvpMatrixBuffer;

	//TODO
	private int visBatchCount;

	/**
	 * Constructor.
	 */
	public GL20Renderer()
	{
		// Call super constructor
		super();

		// Create shared vertex buffer
		//TODO
		sharedVertexBuffer = BufferUtils.createFloatBuffer(16 * 16 * 16 * 6 * 4 * 12);

		// Create shared index buffer
		//TODO
		sharedIndexBuffer = BufferUtils.createShortBuffer(16 * 16 * 16 * 6 * 4);

		//TODO
		projectionMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		orthogonalMatrix = new Matrix4f();
		mvpMatrix = new Matrix4f();

		//TODO
		mvpMatrixBuffer = BufferUtils.createFloatBuffer(16);
	}

	//TODO
	public void setOpaqueProgram(
		final GL20Program opaqueProgram)
	{
		this.opaqueProgram = opaqueProgram;
	}

	//TODO
	public void setModelProgram(
		final GL20Program modelProgram)
	{
		this.modelProgram = modelProgram;
	}

	//TODO
	public void setTransparentProgram(
		final GL20Program transparentProgram)
	{
		this.transparentProgram = transparentProgram;
	}

	/**
	 * Create display.
	 */
	public void createDisplay() throws java.lang.Exception
	{
		// Control mouse
		Mouse.setGrabbed(true);

		// Set display mode
//		displayMode = Display.getDesktopDisplayMode();
//		displayMode = new DisplayMode(1280, 720);
		displayMode = new DisplayMode(800, 450);
//		displayMode = new DisplayMode(320, 200);
		Display.setDisplayMode(displayMode);

		//TODO
		Display.setInitialBackground(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());

		//TODO
		Display.setVSyncEnabled(waitForVsync);
		Display.setFullscreen(true);

		// Create display
		Display.create();

		//TODO
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
//		GL11.glDepthMask(true);
//		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	/**
	 * Set projection from camera parameters.
	 */
	private void setProjection()
	{
		// Reset projection matrix
		Matrix.glMatrixMode(Matrix.GL_PROJECTION);
		Matrix.glLoadIdentity();

		// Set viewing perspective
		Matrix.gluPerspective(camera.getFieldOfView(), ((float) displayMode.getWidth() / (float) displayMode.getHeight()), 0.1f, camera.getViewingDistance());

		// Get projection matrix
		Matrix.glMatrixMode(Matrix.GL_PROJECTION);
		Matrix.glGetMatrix(projectionMatrix);
	}

	/**
	 * Prepare mesh for rendering.
	 * @param mesh The mesh
	 */
	protected void prepareMesh(
		final Mesh mesh)
	{
		// Local variables
		FloatBuffer vertexBuffer;
		ShortBuffer indexBuffer;
		int vertexBufferId;
		int indexBufferId;

		// Get vertex buffer data
		vertexBuffer = mesh.getVertexBuffer();
		sharedVertexBuffer.put(vertexBuffer.getContent(), 0, vertexBuffer.getSize());
		sharedVertexBuffer.flip();

		// Get index buffer data
		//TODO
		indexBuffer = convertIndexBuffer(mesh.getIndexBuffer());
		sharedIndexBuffer.put(indexBuffer.getContent(), 0, indexBuffer.getSize());
		sharedIndexBuffer.flip();

		// Send vertex data
		vertexBufferId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, sharedVertexBuffer, GL15.GL_STATIC_DRAW);

		// Send index data
		indexBufferId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, sharedIndexBuffer, GL15.GL_STATIC_DRAW);

		// Clear buffers
		sharedVertexBuffer.clear();
		sharedIndexBuffer.clear();

		// Update mesh
		mesh.setVertexBufferId(vertexBufferId);
		mesh.setIndexBufferId(indexBufferId);
		//TODO
		mesh.setIndexCount((short)indexBuffer.getSize());
	}

	/**
	 * Destroy mesh from rendering.
	 * @param mesh The mesh
	 */
	protected void destroyMesh(
		final Mesh mesh)
	{
		// Remove vertex data
		GL15.glDeleteBuffers(mesh.getVertexBufferId());

		// Remove index data
		GL15.glDeleteBuffers(mesh.getIndexBufferId());
	}

	/**
	 * Convert index buffer from quad layout to triangle layout.
	 * @param indexBuffer The index buffer
	 * @return The converted index buffer
	 */
	private ShortBuffer convertIndexBuffer(
		final ShortBuffer indexBuffer)
	{
		// Local variables
		int size;
		short[] data;
		ShortBuffer convertedBuffer;

		// Get index buffer data
		size = indexBuffer.getSize();
		data = indexBuffer.getContent();

		// Convert index buffer data
		convertedBuffer = new ShortBuffer((int)(size * 1.5));

		for (int i = 0; i < (size / 4); i++)
		{
			int iindex = (i * 4);
			convertedBuffer.add(data[iindex + 0]);
			convertedBuffer.add(data[iindex + 1]);
			convertedBuffer.add(data[iindex + 2]);
			convertedBuffer.add(data[iindex + 2]);
			convertedBuffer.add(data[iindex + 3]);
			convertedBuffer.add(data[iindex + 0]);
		}

		return convertedBuffer;
	}

	/**
	 * Render scene.
	 */
	public boolean renderScene()
	{
		//TODO
		profiler.measure(Profiler.OTHER);

		//TODO
		// Close requested?
		if (Display.isCloseRequested() == true)
		{
			// Destroy display
			Display.destroy();

			return false;
		}

		// Has camera view changed?
		if (camera.hasViewChanged() == true)
		{
			// Set projection from camera parameters
			setProjection();
		}

		//TODO
		GL11.glClearColor(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());

		// Clear buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		//TODO
		profiler.measure(Profiler.CLEAR_BUFFER);

		// Reset model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glLoadIdentity();

		// Rotate view to camera orientation
		Matrix.glRotatef(camera.getYaw(), 1.0f, 0.0f, 0.0f);
		Matrix.glRotatef(camera.getPitch(), 0.0f, 1.0f, 0.0f);
//TODO		System.out.println("------------------------------>>> " + camera.getPitch());

		// Move view to camera position
		Matrix.glTranslatef(camera.getPositionX(), camera.getPositionY(), camera.getPositionZ());

		//TODO
		profiler.measure(Profiler.UPDATE_MATRIX);

		// Get model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glGetMatrix(modelViewMatrix);

		// Generate model view projection matrix
		Matrix4f.mul(projectionMatrix, modelViewMatrix, mvpMatrix);
		mvpMatrix.store(mvpMatrixBuffer);
		mvpMatrixBuffer.flip();

		//TODO
		profiler.measure(Profiler.GET_MATRIX);

		// Calculate viewing frustum for culling
		frustum.calculateFrustum(mvpMatrixBuffer);

		//TODO
		profiler.measure(Profiler.CALCULATE_FRUSTUM);

		//TODO - per mesh / per list
		// Texture defined?
		if (texture != null)
		{
			// Bind texture
			bindTexture(texture);
		}

		//TODO
		visBatchCount = 0;

		// Disable alpha blending for opaque meshes
		GL11.glDisable(GL11.GL_BLEND);

		// Activate shader program for opaque meshes
		activateProgram(opaqueProgram);

		// Set model view projection matrix in shader program
		GL20.glUniformMatrix4(opaqueProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

		// Set texture sampler in shader program
		GL20.glUniform1i(opaqueProgram.getFragmentSamplerUniform(), 0);

		//TODO
		profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

		// Render opaque meshes
		renderMeshList(opaqueMeshList,
			opaqueProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
			opaqueProgram.getVertexPositionAttribute(), opaqueProgram.getVertexNormalAttribute(),
			opaqueProgram.getVertexColorAttribute(), opaqueProgram.getVertexTextureAttribute());

		// Scene contains model meshes?
		if (modelMeshList.size() > 0)
		{
			// Activate shader program for model meshes
			activateProgram(modelProgram);

			// Set model view projection matrix in shader program
			GL20.glUniformMatrix4(modelProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

			// Set texture sampler in shader program
			GL20.glUniform1i(modelProgram.getFragmentSamplerUniform(), 0);

			//TODO
			profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

			// Render model meshes
			renderMeshList(modelMeshList,
				modelProgram.getModelPositionUniform(), modelProgram.getModelRotationUniform(),
				modelProgram.getVertexPositionAttribute(), modelProgram.getVertexNormalAttribute(),
				modelProgram.getVertexColorAttribute(), modelProgram.getVertexTextureAttribute());
		}

		// Scene contains transparent meshes?
		if ((transparentMeshList.size() > 0)
			|| (overlayMeshList.size() > 0))
		{

			//TODO - per mesh / per list
			// Texture defined?
			if (texture != null)
			{
				// Bind texture
				bindTexture(texture);
			}

			// Enable alpha blending for transparent meshes
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); 
			GL11.glEnable(GL11.GL_BLEND);

			// Activate shader program for transparent meshes
			activateProgram(transparentProgram);

			//TODO - ifdef
			// Set model view projection matrix in shader program
			GL20.glUniformMatrix4(transparentProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

			//TODO - ifdef
			// Set texture sampler in shader program
			GL20.glUniform1i(transparentProgram.getFragmentSamplerUniform(), 0);

			//TODO
			profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

			// Render transparent meshes
			renderMeshList(transparentMeshList,
				transparentProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
				transparentProgram.getVertexPositionAttribute(), transparentProgram.getVertexNormalAttribute(),
				transparentProgram.getVertexColorAttribute(), transparentProgram.getVertexTextureAttribute());

			//TODO
			if (overlayMeshList.size() > 0)
			{
				//TODO
				// Reset model view matrix
				Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
				Matrix.glLoadIdentity();

				// Get model view matrix
				Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
				Matrix.glGetMatrix(orthogonalMatrix);

				// Generate orthogonal projection matrix
				orthogonalMatrix.store(mvpMatrixBuffer);
				mvpMatrixBuffer.flip();

				//TODO
				profiler.measure(Profiler.GET_MATRIX);

				// Enable color invert blending for overlay meshes
//				GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
//				GL11.glEnable(GL11.GL_BLEND);

				//TODO - ifdef
				// Set orthogonal projection matrix in shader program
				GL20.glUniformMatrix4(transparentProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

				//TODO
				profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

				// Render overlay meshes
				renderMeshList(overlayMeshList,
					transparentProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
					transparentProgram.getVertexPositionAttribute(), transparentProgram.getVertexNormalAttribute(),
					transparentProgram.getVertexColorAttribute(), transparentProgram.getVertexTextureAttribute());
			}

		}

		//TODO
		counters.visBatchCount = visBatchCount;

		// Swap buffers
//		GL11.glFlush();
//		GL11.glFinish();
		Display.update(false);
		Display.processMessages();

		//TODO
		profiler.measure(Profiler.SWAP_BUFFERS);

		return true;
	}

	/**
	 * Render overlays.
	 */
	public boolean renderOverlays()
	{
		//TODO
		profiler.measure(Profiler.OTHER);

		//TODO
		// Close requested?
		if (Display.isCloseRequested() == true)
		{
			// Destroy display
			Display.destroy();

			return false;
		}

		//TODO
		GL11.glClearColor(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());

		// Clear buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		//TODO
		profiler.measure(Profiler.CLEAR_BUFFER);

		//TODO
		// Reset model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glLoadIdentity();

		// Get model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glGetMatrix(orthogonalMatrix);

		// Generate orthogonal projection matrix
		orthogonalMatrix.store(mvpMatrixBuffer);
		mvpMatrixBuffer.flip();

		//TODO
		profiler.measure(Profiler.GET_MATRIX);

		//TODO - per mesh / per list
		// Texture defined?
		if (texture != null)
		{
			// Bind texture
			bindTexture(texture);
		}

		//TODO
		visBatchCount = 0;

		// Enable alpha blending for transparent meshes
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); 
		GL11.glEnable(GL11.GL_BLEND);
		// Enable color invert blending for overlay meshes
//		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
//		GL11.glEnable(GL11.GL_BLEND);

		// Activate shader program for transparent meshes
		activateProgram(transparentProgram);

		//TODO - ifdef
		// Set orthogonal projection matrix in shader program
		GL20.glUniformMatrix4(transparentProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

		//TODO - ifdef
		// Set texture sampler in shader program
		GL20.glUniform1i(transparentProgram.getFragmentSamplerUniform(), 0);

		//TODO
		profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

		// Render overlay meshes
		renderMeshList(overlayMeshList,
			transparentProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
			transparentProgram.getVertexPositionAttribute(), transparentProgram.getVertexNormalAttribute(),
			transparentProgram.getVertexColorAttribute(), transparentProgram.getVertexTextureAttribute());

		//TODO
		counters.visBatchCount = visBatchCount;

		// Swap buffers
//		GL11.glFlush();
//		GL11.glFinish();
		Display.update(false);
		Display.processMessages();

		//TODO
		profiler.measure(Profiler.SWAP_BUFFERS);

		return true;
	}

	/**
	 * TODO
	 * Render mesh list.
	 * @param meshList The mesh list
	 * @param modelPositionUniform The model position uniform location
	 * @param modelRotationUniform The model rotation uniform location
	 * @param vertexPositionAttribute The vertex position attribute location
	 * @param vertexNormalAttribute The vertex normal attribute location
	 * @param vertexColorAttribute The vertex color attribute location
	 * @param vertexTextureAttribute The vertex texture attribute location
	 */
	private void renderMeshList(
		final java.util.List<Mesh> meshList,
		final int modelPositionUniform,
		final int modelRotationUniform,
		final int vertexPositionAttribute,
		final int vertexNormalAttribute,
		final int vertexColorAttribute,
		final int vertexTextureAttribute)
	{
		// Local variables
		java.util.ListIterator<Mesh> iterator;
		Mesh mesh;
		boolean render;
		Vector3f position;
		Vector3f rotation;

		//TODO - get from mesh
		int stride = 48; 
		int positionOffset = 0;
		int normalOffset = 12;
		int colorOffset = 24;
		int textureOffset = 40;

		// Render mesh list
		for (iterator = meshList.listIterator(); iterator.hasNext() == true;)
		{
			mesh = (Mesh) iterator.next();

			//TODO
//			profiler.measure(Profiler.ITERATE_LOOP);

			// Default do not render mesh
			render = false;

			// Mesh is overlay mesh?
			if ((mesh.getMeshType() == Mesh.MeshType.OVERLAY) || (mesh.getMeshType() == Mesh.MeshType.MODEL))
			{
				// Render mesh
				render = true;
			}
			else
			{

				// Bounding box within viewing frustum?
				if (frustum.boxInFrustum(mesh.getBoundingBox(), profiler) == true)
				{
					// Render mesh
					render = true;
				}

				//TODO
//				profiler.measure(Profiler.BOX_IN_FRUSTUM);
			}

			// Render mesh ?
			if (render == true)
			{

				// Texture defined for mesh?
				if (mesh.getTexture() != null)
				{
					// Bind texture
					bindTexture(mesh.getTexture(), false);
				}

				//TODO - model position
				if ((modelPositionUniform != -1) && (mesh.getPosition() != null))
				{
					//TODO
					position = mesh.getPosition();

					//TODO
					GL20.glUniform3f(modelPositionUniform, position.x, position.y, position.z);
				}

				//TODO - model rotation
				if ((modelRotationUniform != -1) && (mesh.getRotation() != null))
				{
					//TODO
					rotation = mesh.getRotation();

					//TODO
					GL20.glUniform3f(modelRotationUniform, (float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
				}

				// Bind to vertex buffer
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getVertexBufferId());

				//TODO
//				profiler.measure(Profiler.BIND_BUFFER);

				// Set vertex attributes
				if (vertexPositionAttribute != -1)
				{
					GL20.glVertexAttribPointer(vertexPositionAttribute, 3, GL11.GL_FLOAT, false, stride, positionOffset);
				}

				if (vertexNormalAttribute != -1)
				{
					GL20.glVertexAttribPointer(vertexNormalAttribute, 3, GL11.GL_FLOAT, false, stride, normalOffset);
				}

				if (vertexColorAttribute != -1)
				{
					GL20.glVertexAttribPointer(vertexColorAttribute, 4, GL11.GL_FLOAT, false, stride, colorOffset);
				}

				if (vertexTextureAttribute != -1)
				{
					GL20.glVertexAttribPointer(vertexTextureAttribute, 2, GL11.GL_FLOAT, false, stride, textureOffset);
				}

				//TODO
//				profiler.measure(Profiler.SET_ATTRIB_POINTER);

				// Bind to index buffer
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexBufferId());

				//TODO
//				profiler.measure(Profiler.BIND_BUFFER);

				//TODO
				if (mesh.getIndexOffsets() != null)
				{
					short[] indexOffsets = mesh.getIndexOffsets();

					//TODO
					for (int i = 0; i < indexOffsets.length; i++)
					{
						// Render mesh
						GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_SHORT, indexOffsets[i]);
					}

				}
				else
				{
					// Render mesh
					GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_SHORT, mesh.getIndexOffset());
				}

				//TODO
//				profiler.measure(Profiler.DRAW_ELEMENTS);

				//TODO
				visBatchCount++;
			}

		}

		//TODO
		profiler.measure(Profiler.DRAW_ELEMENTS);
	}

	/**
	 * Set texture parameters.
	 * @param texture The texture
	 */
	protected void setTextureParameters(
		final Texture texture)
	{
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
//		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
//		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
	}

	//TODO
	public Ray pick(
		final float magnitude)
	{
		//TODO - picking
		Matrix4f mviMatrix = new Matrix4f();
		Matrix4f.invert(modelViewMatrix, mviMatrix);

		Vector4f camSpaceNear = new Vector4f(0, 0, 0, 1);
		Vector4f worldSpaceNear = new Vector4f();
		Matrix4f.transform(mviMatrix, camSpaceNear, worldSpaceNear);

		Vector4f camSpaceFar = new Vector4f(0, 0, magnitude, 1);
		Vector4f worldSpaceFar = new Vector4f();
		Matrix4f.transform(mviMatrix, camSpaceFar, worldSpaceFar);
		
		Vector3f rayPosition = new Vector3f(worldSpaceNear.x, worldSpaceNear.y, worldSpaceNear.z);
		Vector3f rayDirection = new Vector3f(worldSpaceFar.x - worldSpaceNear.x, worldSpaceFar.y - worldSpaceNear.y, worldSpaceFar.z - worldSpaceNear.z);
		rayDirection.normalise();
		//TODO - picking

		return new Ray(rayPosition, rayDirection);
	}

}
