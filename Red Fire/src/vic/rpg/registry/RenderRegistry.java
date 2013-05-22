package vic.rpg.registry;

import java.awt.Font;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Random;

import vic.rpg.render.ImageBuffer;
import vic.rpg.render.TextureFX;
import vic.rpg.utils.Utils;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;

public class RenderRegistry 
{
	public static Font RPGFont;
	
	public static final String IMG_TERRAIN_WATER = "imgterrainwater";
	public static final String IMG_TERRAIN_GRASS = "imgterraingrass";
	public static final String IMG_ENTITY_STATIC_HOUSE = "imgterrainentitystatichouse";
	public static final String IMG_ENTITY_STATIC_TREE = "imgterrainentitystatictree";
	public static final String IMG_ENTITY_STATIC_APLTREE = "imgterrainentitystaticapltree";
	
	public static TextureFX anim_water = null;
	
	public static boolean CL_ENABLED = false;
	public static CLContext CL_CONTEXT;
	public static CLDevice CL_DEVICE;
	public static CLCommandQueue CL_QUEUE;
	public static int CL_MEMORY;
	
	public static void bufferImages()
	{
		ImageBuffer.bufferImage(IMG_TERRAIN_WATER, "/vic/rpg/resources/terrain/waterfx_1.png");
		ImageBuffer.bufferImage(IMG_TERRAIN_GRASS, "/vic/rpg/resources/terrain/grass.png");
		ImageBuffer.bufferImage(IMG_ENTITY_STATIC_HOUSE, "/vic/rpg/resources/terrain/house.png");
		ImageBuffer.bufferImage(IMG_ENTITY_STATIC_TREE, "/vic/rpg/resources/terrain/tree.png");
		ImageBuffer.bufferImage(IMG_ENTITY_STATIC_APLTREE, "/vic/rpg/resources/terrain/apple_tree.png");
		
		anim_water = new TextureFX(ImageBuffer.getAnimatedImageData("/vic/rpg/resources/terrain/test.gif"), 5);
	}

	public static void setup() 
	{	
		try {
			InputStream is = Utils.getStreamFromString("/vic/rpg/resources/allember.ttf");
			RPGFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20f);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        /*
		try{
        	// set up (uses default CLPlatform and creates context for all devices)
    		CL_CONTEXT = CLContext.create();
            System.out.println("created "+ CL_CONTEXT);
            
            // always make sure to release the context under all circumstances
            // not needed for this particular sample but recommented
            
            // select fastest device
            CL_DEVICE = CL_CONTEXT.getMaxFlopsDevice();
            System.out.println("using "+ CL_DEVICE);

            // create command queue on device.
            CL_QUEUE = CL_DEVICE.createCommandQueue();
            
            CL_ENABLED = false;

            
            //int elementCount = 1444477;                                  // Length of arrays to process
            CL_MEMORY = Math.min(CL_DEVICE.getMaxWorkGroupSize(), 256);  // Local work size dimensions
            //int globalWorkSize = roundUp(localWorkSize, elementCount);   // rounded up to the nearest multiple of the localWorkSize

            /*
            // load sources, create and build program
            CLProgram program = CL_CONTEXT.createProgram(Utils.getStreamFromString("/vic/rpg/resources/jocl/VectorAdd.cl")).build();
            
            // A, B are input buffers, C is for the result
            CLBuffer<FloatBuffer> clBufferA = CL_CONTEXT.createFloatBuffer(globalWorkSize, READ_ONLY);
            CLBuffer<FloatBuffer> clBufferB = CL_CONTEXT.createFloatBuffer(globalWorkSize, READ_ONLY);
            CLBuffer<FloatBuffer> clBufferC = CL_CONTEXT.createFloatBuffer(globalWorkSize, WRITE_ONLY);

            System.out.println("used device memory: " + (clBufferA.getCLSize()+clBufferB.getCLSize()+clBufferC.getCLSize())/1000000 +"MB");

            // fill input buffers with random numbers
            // (just to have test data; seed is fixed -> results will not change between runs).
            fillBuffer(clBufferA.getBuffer(), 12345);
            fillBuffer(clBufferB.getBuffer(), 67890);

            // get a reference to the kernel function with the name 'VectorAdd'
            // and map the buffers to its input parameters.
            CLKernel kernel = program.createCLKernel("VectorAdd");
            kernel.putArgs(clBufferA, clBufferB, clBufferC).putArg(elementCount);

            // asynchronous write of data to GPU device,
            // followed by blocking read to get the computed results back.
            long time = System.nanoTime();
            CL_QUEUE.putWriteBuffer(clBufferA, false)
                 .putWriteBuffer(clBufferB, false)
                 .put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize)
                 .putReadBuffer(clBufferC, true);
            time = System.nanoTime() - time;

            // print first few elements of the resulting buffer to the console.
            System.out.println("a+b=c results snapshot: ");
            for(int i = 0; i < 10; i++)
            	System.out.print(clBufferC.getBuffer().get() + ", ");
            System.out.println("...; " + clBufferC.getBuffer().remaining() + " more");

            System.out.println("computation took: "+(time/1000000)+"ms");
            
            Render.setup();
        } catch (Exception e) {
			e.printStackTrace();
        }
        */
	}
	
	public static void fillBuffer(FloatBuffer buffer, int seed) 
	{
        Random rnd = new Random(seed);
        while(buffer.remaining() != 0) buffer.put(rnd.nextFloat()*100);
        buffer.rewind();
    }
	
	public static int roundUp(int groupSize, int globalSize) 
	{
        int r = globalSize % groupSize;
        if (r == 0) 
        {
            return globalSize;
        } 
        else 
        {
            return globalSize + groupSize - r;
        }
    }

	public static void stop() 
	{
		if(CL_ENABLED)
		{
			System.out.println("Releasing CL_CONTEXT...");
			CL_CONTEXT.release();
		}
	}
}
	
